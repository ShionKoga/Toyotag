# Toyotag

---

## サーバーの実行方法
1. `./server/.envSample`をコピーし、ファイル`./server/.env`を作成する。
2. 以下のように`.env`に値を記述する。
| 変数名 | 説明 |
| ---- | ---- |
| APNS_TEAM_ID | Apple Developer ProgramのチームID |
| APNS_KEY_ID | Apple Developerの「Certificates, Identifiers & Profiles」ページで作成した、APNsのキーID |
| APNS_BUNDLE_ID | アプリのバンドルID |
| APNS_AUTH_KEY | Apple Developerの「Certificates, Identifiers & Profiles」ページで作成した、APNsのキー(.p8)の中身 |
3. `./server`ディレクトリで`make start`を実行
