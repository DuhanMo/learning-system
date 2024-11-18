Chapter 2: Kotlin Basics

2.1 Basic Elements: Functions and Variables

Functions

- fun 키워드를 사용하여 함수를 선언하며, 매개변수 타입은 이름 뒤에 명시한다.
- 코틀린은 클래스 없이도 최상위 수준에서 함수 선언 가능하며, println과 같은 간결한 구문을 제공한다.
- 세미콜론(;)은 선택사항이다.
- 코틀린에서의 함수는 표현식 본문(expression body)과 블록 본문(block body)으로 나뉜다. 단일 표현식 함수는 = 기호를 통해 값을 직접 반환할 수 있으며, 블록 본문은 중괄호로 감싸고 명시적인 return 문을 사용한다.
- 표현식 본문 예시:

```kotlin
fun max(a: Int, b: Int): Int = if (a > b) a else b
```

- 블록 본문은 복잡한 로직에 적합하며, 가독성을 높이기 위해 반환 타입을 명시할 수 있다.
- if와 같은 제어 구조는 표현식으로 사용할 수 있어 삼항 연산자 없이 간결하게 조건문을 작성할 수 있다.

Variables

- val(불변 참조)과 var(가변 참조) 키워드를 사용해 변수를 선언하며, 타입은 필요에 따라 명시적으로 지정할 수 있다.
- 초기화 표현식을 통해 타입이 추론된다.
- val은 값이 재할당되지 않는 불변 참조를 나타내며, Java의 final과 유사하다. 하지만 참조하는 객체 자체는 가변적일 수 있어, 예를 들어 리스트에 요소 추가가 가능하다.
- val과 var 사용 관련해서는 val로 선언하여 불변성을 유지하고 필요한 경우에만 var를 사용하는 것이 권장된다.
- 예시:

```kotlin
val languages = arrayListOf("Java")
languages.add("Kotlin") // 참조는 불변이지만, 리스트의 상태는 가변적
```

- var은 값이 변경 가능한 가변 참조를 나타내며, 타입은 초기화 시에 고정된다. 이후 다른 타입의 값을 할당하려고 시도하면 컴파일 오류가 발생한다.
- 예시:

```kotlin
var answer = 42
answer = "no answer" // 컴파일 오류 발생 (타입 불일치)
```

- val 키워드로 변수를 선언한 경우, 초기화는 반드시 한 번만 이루어져야 하며, 조건에 따라 서로 다른 값을 할당할 수 있다.
- 예시:

```kotlin
val message: String
if (someCondition) {
    message = "Success"
} else {
    message = "Failed"
}
```

String Templates

- 코틀린은 문자열 템플릿을 통해 $ 기호를 사용하여 문자열 내에 변수를 삽입할 수 있다.
- 복잡한 표현식은 ${}로 감싸서 사용할 수 있으며, $ 문자는 이스케이프 처리($)로 출력한다.
- 문자열 템플릿은 코드를 간결하게 유지하면서도 읽기 쉽도록 돕는다. Java의 문자열 연결 방식에 비해 직관적이며 효율적이다.
- 예시:

```kotlin
val name = "Kotlin"
println("Hello, $name!") // 출력: Hello, Kotlin
```

- 복잡한 조건문이나 표현식을 문자열에 포함할 때는 ${}를 사용하여 명확하게 처리할 수 있다.
- 예시:

```kotlin
println("Hello, ${if (args.isNotEmpty()) args[0] else "someone"}!")
```

2.2 Classes and Properties

Class Declaration

- class 키워드를 사용하여 클래스 선언이 가능하며, 기본 생성자와 프로퍼티를 함께 선언할 수 있다.
- val은 읽기 전용 프로퍼티를, var은 읽기와 쓰기가 모두 가능한 프로퍼티를 선언한다.
- 클래스는 생성자와 함께 프로퍼티를 선언하여 간결하고 명료한 코드 작성을 지원한다. 기본 생성자는 클래스 이름 뒤에 괄호로 선언되며, 이는 자동으로 프로퍼티 초기화를 지원한다.
- 예시:

```kotlin
class Rectangle(val height: Int, val width: Int) {
    val isSquare: Boolean
    get() = height == width
}
```

- 커스텀 접근자(getter 및 setter)는 프로퍼티의 동적 값을 계산하거나 부가적인 로직을 수행할 수 있도록 지원한다.

2.3 Enums and “When” Expressions

Enum Classes

- enum class를 사용하여 열거형을 정의할 수 있으며, 각 열거형에 프로퍼티와 메서드를 추가할 수 있다.
- 열거형은 상태와 관련된 값을 표현하며, 강력한 타입 안전성을 제공한다. 메서드와 프로퍼티를 추가하여 다채로운 동작을 구현할 수 있다.
- 예시:

```kotlin
enum class Color(val r: Int, val g: Int, val b: Int) {
    RED(255, 0, 0), ORANGE(255, 165, 0), YELLOW(255, 255, 0);
    fun rgb() = (r * 256 + g) * 256 + b
}
```

When Expression

- when은 Java의 switch보다 강력하며, 다양한 조건을 표현할 수 있다.
- when은 표현식으로 사용할 수 있어, 값을 반환하거나 복잡한 분기 처리를 간결하게 표현할 수 있다.
- 예시:

```kotlin
fun getDescription(color: Color) = when(color) {
    Color.RED -> "Warm color"
    Color.ORANGE -> "Vibrant color"
    else -> "Unknown color"
}
```

2.4 Loops and Iteration

- for, while, do-while 루프를 사용해 반복 처리를 지원한다.
- for 루프는 범위(.. 연산자)와 컬렉션을 기반으로 반복을 수행한다. 제어 구문으로 step, downTo 등을 사용해 범위를 제어할 수 있다.
- 예시:

```kotlin
for (i in 1..10 step 2) {
    println(i)
}
```

2.5 Exception Handling

- try, catch, finally를 사용해 예외 처리를 지원하며, try는 표현식으로도 사용할 수 있다.
- 코틀린의 예외 처리 방식은 Java와 유사하지만 throws 선언이 필요하지 않으며, 함수가 예외를 던지는 경우에도 이를 명시적으로 선언할 필요가 없다.
- 예시:

```kotlin
fun parseInput(input: String): Int? {
    return try {
        input.toInt()
    } catch (e: NumberFormatException) {
        null
    }
}
```