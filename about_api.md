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
|  /v1/matching/thanks | 助けてもらった後のお礼 |


### login API
curl -XGET -H "Content-Type: application/json" http://localhost:8080/v1/user/login -d '{"userId":"my@email.org", "password":"mypass"}'

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.0/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.0/gradle-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.6.0/reference/htmlsingle/#boot-features-developing-web-applications)
* [JDBC API](https://docs.spring.io/spring-boot/docs/2.6.0/reference/htmlsingle/#boot-features-sql)
* [MyBatis Framework](https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)
* [Spring Data JDBC](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Accessing Relational Data using JDBC with Spring](https://spring.io/guides/gs/relational-data-access/)
* [Managing Transactions](https://spring.io/guides/gs/managing-transactions/)
* [MyBatis Quick Start](https://github.com/mybatis/spring-boot-starter/wiki/Quick-Start)
* [Using Spring Data JDBC](https://github.com/spring-projects/spring-data-examples/tree/master/jdbc/basics)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

