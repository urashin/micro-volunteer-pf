# API一覧

### ここで用意されているAPIは以下

| API | 説明 |
| ----------------| ------------------------|
|  /v1/user/sns_register | SNSとのID連携 API |
|  /v1/user/register | 基本的なユーザー情報の登録 |
|  /v1/user/handicap/register | 障害者の障害情報の登録 |
|  /v1/matching/checkin | 商業施設や観光スポットなどでの位置情報登録（スポット付近にいます） |
|  /v1/matching/checkout | 商業施設や観光スポットからの離脱（もう付近にはいません） |
|  /v1/matching/help| 付近にいる可能性の高いボランティアに助けを求める |
|  /v1/matching/accetp| 自分に届いたhelpに応じる |
|  /v1/matching/helpdetail | マッチング相手の情報を参照 |
|  /v1/user/thanks | 助けてもらった後のお礼 |
|  /v1/user/history | ボランティア履歴の取得 |



実行例
```
AWS_HOST=localhost

# LINE IDとの連携API（障害者、ボランティア）
curl -XGET -H "Content-Type: application/json" http://${AWS_HOST}:8080/v1/user/sns_register -d '{"sns_id":"XXXXXXXXXSNSIDXXXXXXXXXX", "sns_type":1}'

# 自分の位置情報を登録（障害者、ボランティア）
# x_geometry : 経度
# y_geometry : 緯度
curl -XPOST -H "Content-Type: application/json" http://${AWS_HOST}:8080/v1/matching/checkin -d '{"token":"f9924d98-a8fe-438e-84af-2f091f1927b5","x_geometry":"000.0001","y_geometry":"0.222222"}'

# 自身の障害と、よく発生する状況ごとの緊急度＆メッセージを事前に登録しておく（これで１ボタンでヘルプがコールできる）
curl -XPOST -H "Content-Type: application/json" http://${AWS_HOST}:8080/v1/user/handicap/register -d '{"token":"f9924d98-a8fe-438e-84af-2f091f1927b5","handicap_type":1,"handicap_level":1, "reliability_th": 1, "severity": 10, "comment": "本当に困っています、助けてください"}'
curl -XPOST -H "Content-Type: application/json" http://${AWS_HOST}:8080/v1/user/handicap/register -d '{"token":"f9924d98-a8fe-438e-84af-2f091f1927b5","handicap_type":1,"handicap_level":1, "reliability_th": 3, "severity": 1, "comment": "すこしだけ困っています、お手すきでおねがいします。"}'

# 自身の障害＆ヘルプメッセージリストを取得
curl -XGET -H "Content-Type: application/json" http://${AWS_HOST}:8080/v1/user/handicaplist -d '{"token":"f9924d98-a8fe-438e-84af-2f091f1927b5"}'

# 助けを求める（障害者）
# x_geometry : 経度
# y_geometry : 緯度
curl -XPOST -H "Content-Type: application/json" http://${AWS_HOST}:8080/v1/matching/help -d '{"token":"1d45d8c5-378a-44d0-95bc-d430d34a9100","x_geometry":"35.7107654","y_geometry":"139.795978","handicapinfo_id":2}'

# ボランティア側がヘルプに応じる
curl -XPOST -H "Content-Type: application/json" http://${AWS_HOST}:8080/v1/matching/accept -d '{"token":"1d45d8c5-378a-44d0-95bc-d430d34a9100","help_id":7}'

# 助けてくれたボランティアに「ありがとう」評価を送る
#curl -XPOST -H "Content-Type: application/json" http://${AWS_HOST}:8080/v1/user/thanks -d '{"token":"1d45d8c5-378a-44d0-95bc-d430d34a9100","help_id":7,"evaluate":10}'

# これまでの自分のボランティア履歴を取得する
curl -XGET -H "Content-Type: application/json" http://${AWS_HOST}:8080/v1/user/history -d '{"token":"1d45d8c5-378a-44d0-95bc-d430d34a9100"}'
```