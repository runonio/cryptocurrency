# 거래소 관련 api 정의
거래소 api 사용을 위한 모듈, 초기에는 단독모듈로 규모가 커지면 거래소 별로 분할
기존 바이낸스 api가 변경된걸로 보인다. 당장 다른기능들을 바꾸는데 공수를 줄이기위해 com.binance.client 관련 소스들을 포함한다.

- https://github.com/Binance-docs/Binance_Futures_Java 


### 구현 거래소 목록
- 바이낸스
- 바이낸스 선물
- okx(okex)
- okx 선물
- ftx
- ftx 선물
- 코인베이스
- kraken
- kraken 선물
- 후오비
- 업비트
- 빗썸

# 개발환경
- open jdk 17

# 캔들 데이터 형 csv
캔들시작시간(밀리초 유닉스타임)[0],종가[1],시가[2],고가[3],저가[4],직전가[5],거래량[6],거래대금[7],거래횟수[8],매수거래량[9],매수거래대금[10]

# communication
## site, blog, git
- [runon.io](https://runon.io)
- [blog.runon.io](https://blog.runon.io)
- [github.com/runonio](https://github.com/runonio)

## contact
- email: info@runon.io

## main developer
- macle
    - git: [github.com/macle86](https://github.com/macle86)