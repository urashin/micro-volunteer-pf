# デモ環境の説明

#### プラットフォームが今回の提案となります、お試しいただくには curlかRestClientをご利用ください。
#### 12/4のプレゼンではこれらのAPIを利用したアプリでデモを行います。

### デモ環境動作確認
こちらにアクセスし、{"result","OK"}が表示されることを確認してください。こちらのデモ環境は(2021/12/4のみ稼働します)

http://ec2-54-95-14-86.ap-northeast-1.compute.amazonaws.com:8080/v1/kakunin


### 実行方法
#### sns_register : tokenの取得(LINEアカウントとの紐付けに使います)
````
curl -XGET -H "Content-Type: application/json" http://ec2-54-95-14-86.ap-northeast-1.compute.amazonaws.com:8080/v1/user/sns_register -d '{"sns_id":"XXXXXXXXXSNSIDXXXXXXXXXX", "sns_type":1}'
````

#### checkin : 特定の場所に自身（主にボランティア側）が到着したことを記録することで、その周辺でのヘルプを受けやすい状態になる
````
curl -XGET -H "Content-Type: application/json" http://ec2-54-95-14-86.ap-northeast-1.compute.amazonaws.com:8080/v1/matching/checkin -d '{"token":"(sns_registerで取得したtokenをここに入れます)","x_geometry":"000.0001","y_geometry":"0.222222"}'
````

#### handicap/register : 障害者が自身の障害とその程度、深刻度などを事前に登録しておくことで、簡単に助けを求めることができます。
````
curl -XGET -H "Content-Type: application/json" http://ec2-54-95-14-86.ap-northeast-1.compute.amazonaws.com:8080/v1/matching/checkin 
-d '{"token":"(sns_registerで取得したtokenをここに入れます)","x_geometry":"000.0001","y_geometry":"0.222222"}'
````
