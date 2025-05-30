# 7장 분산 시스템을 위한 유일 ID 생성기 설계
일반적으로 생각할 수 있는 답은 `auto_increment`속성이다. 이는 분산 환경에서는 통하지 않는 접근법이다.

## 1단계 문제 이해 및 설계 범위 확정
해당 문제에 대한 요구 사항은 아래와 같다.
- ID는 유일해야 한다.
- ID는 숫자로만 구성되어야 한다.
- ID는 64비트로 표현될 수 있는 값이어야 한다.
- ID는 발급 날짜에 따라 정렬 가능해야 한다.
- 초당 10,000개의 ID를 만들 수 있어야 한다.

## 2단계 개략적 설계안 제시 및 동의 구하기
유일성이 보장되는 ID를 만드는 다양한 방법
- 다중 마스터 복제
- UUID
- 티켓 서버
- 트위터 스노플레이크 접근법

### 다중 마스터 복제
데이터베이스의 auto_increment 기능을 활용한 것. 1만큼 증가시키는 것이 아닌, k(데이터베이스 서버 수)만큼 증가.

**장점** 
- 규모 확장성 문제를 어느정도 해결
- 데이터베이스 수를 늘리면 초당 생산 가능ID도 함께 증가

**단점**
- 여러 데이터센터에 걸친 확장 어려움
- ID의 유일성은 보장되지만, 그 값이 시간 흐름에 맞추어 커지도록 보장 불가
- 서버 증설/삭제 할 때 잘동작하도록 하는 것의 어려움

### UUID
컴퓨터 시스템에 저장되는 정보를 유일하게 식별하기 위한 128비트짜리 수. 충돌가능성이 낮음

**장점**
- 단순하게 만들 수 있고 서버 사이 조율이 필요하지 않음
- 서버 스스로 ID를 만들기 때문에 규모 확장이 쉬움

**단점**
- 문제에서 필요한 길이는 64비트인데 UUID는 128비트
- ID를 시간순으로 정렬할 수 없음
- ID에 숫자가 아닌 값이 포함될 수 있음

### 티켓 서버
auto_increment 기능을 갖춘 데이터베이스 서버를 중앙 집중형으로 사용.

**장점**
- 유일성이 보장되고 숫자로만 구성된 ID를 쉽게 생성
- 쉬운 구현과 중소 규모 애플리케이션에 적합

**단점**
- 티켓 서버의 SPOF(Single-Point-of-Failure) 가능성

### 트위터 스노플레이크 접근법
트위터의 독창적인 ID 생성 기법
1비트(사인)-41비트(타임스탬프)-5비트(데이터센터ID)-5비트(서버ID)-12비트(일련번호)

- 사인 비트: 현재 쓰임새는 없음. 음수와 양수를 구별하는데 사용할 것
- 타임 스탬프: 41비트 할당, epoch 이후 몇 밀로가 경과했는지 확인
- 데이터센터 ID: 5비트 할당. 2^5 = 32개 데이터센터를 지원
- 서버 ID: 5비트 할당. 데이터 센터당 32개 서버 사용 가능
- 일련번호: 12비트 할당. 서버에서 ID를 생성할 때마다 일련번호 1만큼 증가. 1밀리초 경과 시 0으로 초기화.

## 3단계 상세 설계
데이터센터 ID, 서버 ID는 시스템 시작시 결정되고 나머지는 생성기가 동작중에 생성되는 값.

### 타임스탬프
시간순으로 정렬 가능하게 된다. 41비트로 표현할 수 있는 타임스탬프 최댓값은 2^41 -1 = 2199023255551 밀리초 = 약 69년인데, 69년이 지나면 기원시각을 바꾸거나 ID 체계를 마이그레이션 해야한다.

### 일련번호
2^12 = 4096개의 값을 가질 수 있다. 밀리초 동안 하나 이상의 ID를 만들어낸 경우에만 0보다 큰 값을 가진다.

## 4단계 마무리
추가로 논의할 수 있는 항목
- 시계  동기화: 위 내용들은 서버 모두가 같은 시계를 사용한다고 가정. 하나의 서버가 여러 코어에서 실행될 경우 유효하지 않을 수 있음. 이 문제를 해결하기 위해 NTP(Network Time Protocol) 사용 가능
- 각 절의 길이 최적화: 동시성이 낮고 수명이 긴 애플리케이션이라면 일련번호 절의 길이를 줄이고 타임스탬프 절의 길이를 늘릴 수 있을 것
- 고가용성: ID 생성기는 아주 높은 가용성을 제공해야 할 것