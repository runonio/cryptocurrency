# cryptocurrency
암호 화폐 분석 및 매매
- 암호화폐 자동매매와 관련된 기술 모듈들이 제공 됩니다. 
- 서비스에 직접적인 영향을 주는 부분은 제공되지 않습니다.
- 모듈명이 겹칠만한 내용들은 cryptocurrency-name 형태로 사용됩니다.
- 
# 개발환경
- open jdk 17

# 데이터 저장구조
암호화폐 분석에 사용하는 표준데이터구조는 아래로 설정하여 진행 합니다.
<br>
첫 매매 봇의 목적 거래소는 바이낸스 이기때문에 여러 거래소를 고려한 구조는 아닙니다.
<br>
여러 거래소를 활용할 경우 아래구조는 변경될 수 있습니다.
<br>
- data/cryptocurrency/futures
- data/cryptocurrency/spot
- data/cryptocurrency/merge
하위구조
- data/cryptocurrency/spot/candle
- data/cryptocurrency/spot/candle/order_book
- data/cryptocurrency/futures/candle
- data/cryptocurrency/futures/order_book
- data/cryptocurrency/futures/open_interest
- data/cryptocurrency/merge/volume

# 서비스 관련 모듈
- 서비스에 직접적 영향이 있는 모듈은 private repository 에서 진행됩니다. 협업요청이나 투자 외주 문의는 따로 연락주세요. 
- 대량채결(고래체결), 급등급락감지, 봇의 매수매도 시점 과 같이 서비스와 관련된 부분에 대한 내용 입니다.

# gradle
implementation 'io.runon.cryptocurrency:cryptocurrency-exchanges:0.4.8'
- etc
  - https://mvnrepository.com/artifact/io.runon.cryptocurrency/cryptocurrency-exchanges/0.4.8

implementation 'io.runon.cryptocurrency:cryptocurrency-trading:0.1.6'
- etc
  - https://mvnrepository.com/artifact/io.runon.cryptocurrency/cryptocurrency-trading/0.1.6

# communication
### blog, homepage
- [www.seomse.com](https://www.seomse.com/)
- [runon.io](https://runon.io)
- [github.com/seomse](https://github.com/seomse)
- [github.com/runonio](https://github.com/runonio)

### 카카오톡 오픈톡
- https://open.kakao.com/o/g6vzOKqb
    - 참여코드: runon

### 슬랙 slack
- https://seomse.slack.com/

### email
- comseomse@gmail.com


# main developer
- macle
    -  [github.com/macle86](https://github.com/macle86)
    -  [macle.dev](https://macle.dev)
