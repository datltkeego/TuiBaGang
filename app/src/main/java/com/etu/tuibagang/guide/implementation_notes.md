## Android Version Tool - Implementation Notes

### Architecture
- UI: Jetpack Compose + Bottom Navigation (Versions, Products, Settings)
- Data source: Supabase REST `public.apks`
- Local cache: Room database (`apks` table)
- Flow:
  1. Fetch from Supabase
  2. Upsert into Room
  3. UI observes Room `Flow`

### Supabase API
- Endpoint: `/rest/v1/apks?select=*&order=created_at.desc`
- Headers:
  - `apikey: <anon_key>`
  - `Authorization: Bearer <anon_key>`

### Config
- `BuildConfig.SUPABASE_URL`
- `BuildConfig.SUPABASE_ANON_KEY`

### Current tabs
- `Versions`: implemented with fetch + Room cache + refresh + open APK links
- `Products`: placeholder
- `Settings`: placeholder
