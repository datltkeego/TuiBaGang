# Phân tích logic "chặn theo phần" (Shorts/Reels) bằng Accessibility

## Kết luận ngắn
Có. App này có logic **chặn theo tính năng con trong app** (không phải chặn toàn bộ package), ví dụ:
- `YouTube Shorts`
- `Facebook Reels`

Logic nằm trong pipeline Accessibility + parser name theo màn hình/route.

---

## 1) Nền tảng Accessibility được bật toàn cục
Service Accessibility khai báo trong manifest:
- `com.burockgames.timeclocker.accessibility.StayFreeAccessibilityService`
- dùng quyền `android.permission.BIND_ACCESSIBILITY_SERVICE`

Tham chiếu:
- `app/src/main/com.burockgames.timeclocker.apk/AndroidManifest.xml:315`
- `app/src/main/com.burockgames.timeclocker.apk/AndroidManifest.xml:316`
- `app/src/main/com.burockgames.timeclocker.apk/AndroidManifest.xml:320`
- `app/src/main/com.burockgames.timeclocker.apk/AndroidManifest.xml:323`

Cấu hình service nhận mọi event + đọc view tree:
- `typeAllMask`
- `flagRetrieveInteractiveWindows|flagReportViewIds`
- `canRetrieveWindowContent=true`

Tham chiếu:
- `app/src/main/com.burockgames.timeclocker.apk/res/xml-v22/stayfree_accessibility_service.xml`

---

## 2) Event Accessibility đi vào pipeline xử lý block
Luồng event:
1. `Wa.n.onAccessibilityEvent(...)` nhận event hệ thống.
2. Event đi qua `w(Cb.a, ...)` ở lớp kế thừa (`AbstractC5878e` -> `StayFreeAccessibilityService`).

Tham chiếu:
- `app/src/main/java/Wa/n.java:467`
- `app/src/main/java/Wa/n.java:471`
- `app/src/main/java/Wa/n.java:473`
- `app/src/main/java/Wa/n.java:477`
- `app/src/main/java/Wa/n.java:529`
- `app/src/main/java/Fc/AbstractC5878e.java:628`

Lưu ý: một số hàm lõi trong file decompile bị `UnsupportedOperationException` (không hiện full body), nhưng wiring class và dữ liệu cấu hình vẫn đủ rõ để xác định cơ chế.

---

## 3) Dữ liệu cấu hình cho "chặn theo phần" (điểm mấu chốt)
### 3.1 Enum loại block theo tính năng con
`EnumC4022a` định nghĩa từng loại block riêng, gồm:
- `appId` (package app)
- `appParserNames`
- `webParserNames`
- `prefsKey`

Ví dụ đúng theo câu hỏi:
- `ACCESSIBILITY_BLOCK_YOUTUBE_SHORTS`
  - package: `com.google.android.youtube`
  - parser app/web: từ `AbstractC4023b.f40459a/f40460b`
  - key: `accessibility-sdk-youtube-plus-block-shorts`
- `ACCESSIBILITY_BLOCK_FACEBOOK_REELS`
  - package: `com.facebook.katana`
  - parser app/web: từ `AbstractC4023b.f40475q/f40476r`
  - key: `accessibility-sdk-facebook-plus-reels`

Tham chiếu:
- `app/src/main/java/bb/EnumC4022a.java:40`
- `app/src/main/java/bb/EnumC4022a.java:48`

### 3.2 Parser name xác định đúng "phần" cần chặn
Giá trị parser cho các loại trên:
- YouTube Shorts:
  - app parser: `"shorts"`
  - web parser: `"shorts"`
- Facebook Reels:
  - app parser: `"reels"`
  - web parser: `"watch"`

Tham chiếu:
- `app/src/main/java/bb/AbstractC4023b.java:12`
- `app/src/main/java/bb/AbstractC4023b.java:15`
- `app/src/main/java/bb/AbstractC4023b.java:60`
- `app/src/main/java/bb/AbstractC4023b.java:63`

=> Đây là bằng chứng quan trọng cho thấy app không chặn toàn bộ package, mà chặn khi parser xác định đang ở đúng "zone" (shorts/reels/watch).

---

## 4) Bật/tắt từng loại block được lưu theo key riêng
Settings Accessibility SDK lưu trạng thái từng `EnumC4022a`:
- `O(type)`: đọc đang bật/tắt
- `v0(type, bool)`: ghi bật/tắt
- `w0(type, time)`: ghi mốc thời gian liên quan block
- `N()`: danh sách các loại in-app blocking được hỗ trợ

Tham chiếu:
- `app/src/main/java/com/sensortower/accessibility/accessibility/util/a.java:199`
- `app/src/main/java/com/sensortower/accessibility/accessibility/util/a.java:203`
- `app/src/main/java/com/sensortower/accessibility/accessibility/util/a.java:437`
- `app/src/main/java/com/sensortower/accessibility/accessibility/util/a.java:446`

Repo prefs cũng có hàm lấy package list đang có loại block bật (`b0()`):
- duyệt tất cả `EnumC4022a`
- lọc bằng `settings.O(enum)`
- map sang `enum.j()` (appId/package)

Tham chiếu:
- `app/src/main/java/i5/C6189r.java:978`

---

## 5) Mapping sang UI và mở màn hình chặn
Mapping enum Accessibility sang loại block trong app:
- `InAppBlockingType.YOUTUBE_SHORTS -> ACCESSIBILITY_BLOCK_YOUTUBE_SHORTS`
- `InAppBlockingType.FACEBOOK_REELS -> ACCESSIBILITY_BLOCK_FACEBOOK_REELS`

Tham chiếu:
- `app/src/main/java/com/burockgames/timeclocker/common/enums/InAppBlockingType.java:41`
- `app/src/main/java/com/burockgames/timeclocker/common/enums/InAppBlockingType.java:49`

Khi trigger block, callback mở `BlockScreenActivity` với `BlockScreenType.IN_APP_BLOCKING` và loại cụ thể:
- `BlockScreenActivity.INSTANCE.g(context, id, IN_APP_BLOCKING, findByAccessibilityInAppBlockingType(...), ...)`

Tham chiếu:
- `app/src/main/java/F5/g.java:27`

Text hiển thị block cũng có nhánh riêng cho `Shorts/Reels`:
- `getInAppBlockedSummaryWithType(...)`
- dùng string `this_application_is_blocked_for_today_in_app_blocking_reels_or_shorts`

Tham chiếu:
- `app/src/main/java/com/burockgames/timeclocker/common/enums/BlockScreenType.java:170`
- `app/src/main/java/com/burockgames/timeclocker/common/enums/BlockScreenType.java:175`
- `app/src/main/java/com/burockgames/timeclocker/common/enums/BlockScreenType.java:195`
- `app/src/main/java/com/burockgames/timeclocker/common/enums/BlockScreenType.java:207`

---

## 6) Diễn giải cơ chế "chặn 1 phần thay vì toàn bộ"
Cơ chế là:
1. Accessibility bắt event và trích xuất trạng thái màn hình hiện tại.
2. Với app mục tiêu (YouTube/Facebook), hệ thống parser xác định đang ở màn nào (ví dụ `shorts`, `reels`, `watch`).
3. So với danh sách parser của từng `EnumC4022a` đang bật trong settings.
4. Nếu match, gọi callback mở block screen theo đúng `InAppBlockingType`.

Vì điều kiện dựa trên **parser màn hình/route** chứ không chỉ package name, nên app đạt được "block theo phần".

---

## 7) Ghi chú về độ chắc chắn
- Các phần enum/config/settings/mapping/UI ở trên là bằng chứng trực tiếp từ code.
- Một số coroutine method lõi trong `StayFreeAccessibilityService` và các lớp parser bị decompile thiếu body, nên đoạn "so khớp parser -> trigger block" ở mức **suy luận có cơ sở mạnh từ wiring + cấu trúc dữ liệu**.
