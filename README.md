## Spring webflux with r2dbc test
Webflux 와 R2DBC 테스트    
그리고 비동기 처리 

```java
route(POST("/hello").and(accept(APPLICATION_JSON)), this::hello)
    .andRoute(POST("/person").and(accept(APPLICATION_JSON)), this::save)
    .andRoute(GET("/person").and(accept(APPLICATION_JSON)), this::find)
    .andRoute(GET("/person2").and(accept(APPLICATION_JSON)), this::rewrite);
```
위와 같이 라우터가 정의 되어있는데,    
POST /person 은 데이터 생성,    
GET /person 은 단순 조회,   
GET /person2 는 트랜잭션 확인을 위한 테스트,    
POST /hello 는 비동기 작업 확인을 위한 테스트임.

POST /hello 호출시 당장은 단순 데이터 생성 하고 되돌려주지만,    
10초 후 person.name 값을 수정함.

test.http 참고


## Installation
### Requirement
* java 11
#### Option
* [gradle](https://gradle.org/install/)

  Use an build tool [gradle](https://gradle.org/install/) to managing dependencies and build.

```bash
$ brew install gradle
```    
or

manually [download gradle](https://gradle.org/releases/) and unzip binaries.

## Build

build using gradle.
```bash
$ gradlew clean build [-x test] 
```
or

```bash
$ ./gradlew clean build [-x test]
# use gradle including project
```

## Run

```bash
$ java -jar application-0.1.jar
```
or
```
$ ./gradlew bootRun
```
