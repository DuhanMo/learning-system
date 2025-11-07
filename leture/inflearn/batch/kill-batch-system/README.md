## 스프링 배치 두 가지 스텝 유형
- Spring Batch Step은 두 가지 처리 모델: **Tasklet 지향 처리**와 **Chunk 지향 처리**가 있다.
- Tasklet은 단순하고 명령형 작업(파일 삭제, 알림, 외부 호출 등)에 적합하다.
- Tasklet은 `Tasklet.execute()`가 **RepeatStatus**를 반환하며 FINISHED/CONTINUABLE로 실행 반복 여부를 제어한다.
- RepeatStatus는 **작은 트랜잭션 단위**를 생성해 오류 발생 시 전체 롤백을 방지한다.
- Tasklet은 별도 구현 클래스를 만들거나 Step 설정 중 람다식으로 정의할 수 있다.
- 트랜잭션 관리가 필요 없으면 `ResourcelessTransactionManager` 사용을 고려할 수 있다.
- Chunk 지향 처리는 데이터를 **Chunk 단위**로 읽고, 처리하고, 쓰도록 구성한다.
- Chunk 모델은 `ItemReader`, `ItemProcessor`, `ItemWriter` 세 컴포넌트가 핵심 역할을 맡는다.
- Chunk 단위 트랜잭션은 **대용량 처리 안정성**과 **부분 성공**을 가능하게 한다.
- Chunk 크기는 메모리 사용량 vs. 트랜잭션 빈도 트레이드오프를 고려해 설정한다.

## 잡파라미터와 스코프
- **JobParameters**는 배치 실행 시 동적으로 입력값(날짜, 경로, 조건 등)을 전달하는 핵심 제어 변수이다.
- 단순한 프로퍼티(-D 옵션)와 달리, **메타데이터에 기록되어 Job 인스턴스 식별·재시작·이력 추적**이 가능하다.
- **기본 표기법:** `name=value,type,identifyingFlag` 형식이며, 여러 파라미터는 공백으로 구분한다.
- **지원 타입:** String, Integer, Boolean, LocalDate, LocalDateTime, Enum 등이며 자동 변환된다.
- **JSON 기반 표기법**(`{"value":"...", "type":"..."}`)을 사용하면 쉼표(,)가 포함된 값도 안전하게 전달할 수 있다.
- **JobParametersBuilder**로 코드에서 JobParameters를 생성해 JobLauncher로 Job을 실행할 수 있다.
- 파라미터는 `@StepScope`나 `@JobScope` 빈에서 `@Value("#{jobParameters['key']}")`로 주입받을 수 있다.
- `@JobScope`는 Job 실행 단위로, `@StepScope`는 Step 실행 단위로 빈을 생성·소멸시켜 **동시성 문제를 방지**한다.
- **ExecutionContext**를 이용하면 Step 간 또는 재시작 시 데이터를 저장·복원할 수 있다.
- **JobParametersValidator**를 사용해 필수 파라미터 검증이나 값의 유효성 체크를 수행할 수 있다.