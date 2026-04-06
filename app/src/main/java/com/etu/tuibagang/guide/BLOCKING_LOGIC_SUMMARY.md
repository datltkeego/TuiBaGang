# Tổng hợp logic "block" trong app StayFree

Tài liệu này tóm tắt các cơ chế chặn (blocking) được thấy trong mã nguồn đã giải nén. Một số hàm lõi bị lỗi decompile sẽ được ghi chú rõ.

## 1) Các loại giới hạn/chặn chính

**Enum `UsageLimitType`** gồm các loại giới hạn/chặn:  
- `BLOCK_PERMANENTLY` (chặn vĩnh viễn)  
- `BLOCK_ON_A_SCHEDULE` (chặn theo lịch)  
- `DAILY_USAGE_LIMIT` (giới hạn dùng hằng ngày)  
- `VARIABLE_SESSION_LIMIT` (giới hạn phiên dùng biến thiên)  
- `BLOCK_KEYWORDS` (chặn theo từ khóa)  
File: `app/src/main/java/com/burockgames/timeclocker/common/enums/UsageLimitType.java`

**Enum `DailyUsageLimitBlockType`**:  
- `BLOCK` (chặn khi vượt)  
- `NOTIFICATION` (chỉ thông báo)  
File: `app/src/main/java/com/burockgames/timeclocker/common/enums/DailyUsageLimitBlockType.java`

**Enum `BlockScreenType`** (loại màn hình chặn):  
- In‑app blocking, block vĩnh viễn, block theo lịch, daily limit, variable session, cooldown… cho **app** và **web**  
File: `app/src/main/java/com/burockgames/timeclocker/common/enums/BlockScreenType.java`

**Enum `InAppBlockingType`**: danh sách các “feature” bị chặn trong app cụ thể (YouTube Shorts/Search/PIP/Comments, Instagram Reels/Stories/Explore, Facebook Reels/Stories/Marketplace, Snapchat Spotlight/Stories, TikTok Search/Comments, v.v.)  
File: `app/src/main/java/com/burockgames/timeclocker/common/enums/InAppBlockingType.java`

---

## 2) Dữ liệu nền tảng cho việc chặn

### 2.1 `GenericUsageLimit`
Đây là model trung tâm cho các giới hạn/chặn. Các trường quan trọng:
- Danh sách đối tượng bị áp dụng: `ANDROID_PACKAGE_NAMES`, `WEBSITE_URLS`, `DESKTOP_APP_IDS`, `BRAND_IDS`, `CATEGORY_IDS`
- Loại giới hạn: `LIMIT_TYPE_VALUE` → map sang `UsageLimitType`
- Dữ liệu lịch: `SCHEDULE_START_HOUR/MINUTE`, `SCHEDULE_END_HOUR/MINUTE`, `SCHEDULE_IS_ALL_DAY`, `SCHEDULE_ACTIVE_DAYS`
- Daily limit: `DAILY_LIMIT_DURATION`, `DAILY_LIMIT_METRIC_TYPE_VALUE`, `DAILY_LIMIT_BLOCK_TYPE_VALUE`, `DAILY_LIMIT_CUSTOM_TEXT`, `DAILY_LIMIT_NOTIFICATION_DATE`
- Variable session: `VARIABLE_SESSION_COOLDOWN_PERIOD`
- Block keywords: `BLOCK_KEYWORDS_CUSTOM_KEYWORDS`, `BLOCK_KEYWORDS_GROUP_NAMES`

File: `app/src/main/java/com/burockgames/timeclocker/database/item/GenericUsageLimit.java`

**Các hàm logic đáng chú ý (đã thấy code):**
- `getAllBlockedKeywords(context)`  
  - Hợp nhất **keyword tùy chỉnh** + **keyword theo nhóm** (nhóm lấy từ remote config).  
- `getAllIdentifiers()`  
  - Gom tất cả identifiers từ `groupStatsList` để tính usage.  
- `getDailyLimitBlockingStatusText(context)`  
  - Nếu duration = 0 → “always blocked”, nếu hết → “limit reached”, còn lại → “time left” hoặc “sessions left”.  
- `getTimeUntilDailyUsageLimitExceeded(viewModelPrefs)`  
  - `dailyLimitDuration - usageAmount`  
- `isDailyUsageLimitExceeded(viewModelPrefs)`  
  - `usageAmount >= dailyLimitDuration`  
- `getTimeToSchedule()`  
  - Tính thời gian đến lịch chặn tiếp theo dựa trên giờ/phút và ngày trong tuần.

**Các hàm bị lỗi decompile (không xem được logic chi tiết):**
- `isScheduleActive()`  
- `isMoreLimitingThan(...)`  

### 2.2 `Schedule` và `FocusModeGroup`
DB có bảng `Schedule` và `FocusModeGroup` liên quan đến “Focus Mode”.  
`Schedule` lưu giờ bắt đầu/kết thúc, ngày trong tuần, all‑day, enabled, `SCHEDULE_TYPE_VALUE`, `FOCUS_MODE_GROUP_ID`.  
`FocusModeGroup` lưu tập apps/websites và thời gian toggle hết hạn.  
Files:  
- `app/src/main/java/com/burockgames/timeclocker/database/item/Schedule.java`  
- `app/src/main/java/com/burockgames/timeclocker/database/item/FocusModeGroup.java`  

### 2.3 `SessionAlarm` & `CoolingDownApp`
`SessionAlarm` lưu alarm theo phiên (packageName, time, type).  
`CoolingDownApp` lưu thời điểm “cooldown” kết thúc cho app.  
Files:  
- `app/src/main/java/com/burockgames/timeclocker/database/item/SessionAlarm.java`  
- `app/src/main/java/com/burockgames/timeclocker/common/data/CoolingDownApp.java`  

---

## 3) Luồng chặn chính trong runtime

### 3.1 Accessibility Service (chặn khi đang dùng)
`StayFreeAccessibilityService` là nơi bắt các điều kiện chặn trong lúc người dùng đang ở **app/web**.  
Đặc biệt có một enum nội bộ (BLOCK_PERMANENTLY, BLOCK_ON_A_SCHEDULE, DAILY_USAGE_LIMIT, VARIABLE_SESSION_LIMIT, VARIABLE_SESSION_LIMIT_COOLDOWN).  
Khi xác định bị chặn, service mở **BlockScreenActivity** cho **web** theo `BlockScreenType` tương ứng:  
- `BLOCK_PERMANENTLY_WEB`  
- `BLOCK_ON_A_SCHEDULE_WEB`  
- `DAILY_USAGE_LIMIT_WEB` (kèm `dailyLimitCustomText`)  
- `VARIABLE_SESSION_LIMIT_WEB`  
- `VARIABLE_SESSION_LIMIT_COOLDOWN_WEB`  
File: `app/src/main/java/com/burockgames/timeclocker/accessibility/StayFreeAccessibilityService.java`

**Lưu ý:** nhiều hàm trong service bị lỗi decompile (`w`, `X1`, `d.j`, `d.d`…), nên **không xem được toàn bộ logic điều kiện**.

### 3.2 Block Keywords (chặn theo từ khóa trên màn hình)
Khi phát hiện keyword bị chặn, service gọi `BlockKeywordsActivity`.  
`BlockKeywordsActivity` trả về `KeywordBlockResult`:
- `IGNORE` → hành vi “bỏ qua” chặn
- `EXIT` → thoát/đưa người dùng về `MainActivity`

Các kết quả này được xử lý trong `StayFreeAccessibilityService.Y1(...)`.  
Files:  
- `app/src/main/java/com/burockgames/timeclocker/service/activity/BlockKeywordsActivity.java`  
- `app/src/main/java/com/burockgames/timeclocker/accessibility/StayFreeAccessibilityService.java`  

### 3.3 Notification Listener (chặn thông báo)
`StayFreeNotificationListenerService` hủy notification của app nếu:
- có limit `BLOCK_PERMANENTLY`, hoặc  
- `BLOCK_ON_A_SCHEDULE` đang active (`isScheduleActive()`), hoặc  
- `DAILY_USAGE_LIMIT` đã vượt (`isDailyUsageLimitExceeded()`), hoặc  
- app đang trong cooldown (`CoolingDownApp`).

Logic nằm trong `onNotificationPosted()` → `k()` → coroutine `a` → `l()` (cancelNotification).  
File: `app/src/main/java/com/burockgames/timeclocker/service/foreground/StayFreeNotificationListenerService.java`

---

## 4) Màn hình chặn (BlockScreenActivity)

`BlockScreenActivity` nhận các extras:
- `extra_active_url` hoặc `extra_app_package`  
- `extra_block_screen_type`  
- `extra_in_app_blocking_type`  
- `extra_daily_limit_custom_text`  
- `extra_daily_limit_metric_type`  
- `extra_is_for_web`  

Sau đó dùng `BlockScreenType` để chọn **thông điệp lý do chặn** (app/web, theo lịch, daily limit, variable session, cooldown, in‑app blocking, …).  
File: `app/src/main/java/com/burockgames/timeclocker/service/activity/BlockScreenActivity.java`

---

## 5) Các điểm mờ do lỗi decompile

Một số logic lõi không thể xem được do lỗi decompile:
- `StayFreeAccessibilityService.w(...)`, `X1(...)`, `d.j(...)`, `d.d(...)`  
- `GenericUsageLimit.isScheduleActive()`  
- `GenericUsageLimit.isMoreLimitingThan(...)`

Nếu cần chi tiết 100%, nên xem **smali** hoặc lấy source gốc.

