# デモ環境の説明

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
|  /v1/matching/thanks | 助けてもらった後のお礼 |


### login API
curl -XGET -H "Content-Type: application/json" http://localhost:8080/v1/user/login -d '{"userId":"my@email.org", "password":"mypass"}'

