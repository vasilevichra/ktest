# [kTest](../README.md) :: [Core](README.md) :: API

## Download

### Gradle

```groovy
'run.smt.ktest:ktest-api'
```

### Maven

```xml
<dependency>
    <groupId>run.smt.ktest</groupId>
    <artifactId>ktest-api</artifactId>
</dependency>
```

## Description

This module contains base code of kTest (and actually it is kTest) providing ability to write specs like this:

```kotlin
import org.junit.Assert.*
import run.smt.ktest.specs.BehaviorSpec

object MySpec1 : BehaviorSpec({
    val thisIsAwesome = true

    given("kotlin") {
        `when`("need integration/acceptance testing framework") {
            then("use kTest!") {
                assertTrue(thisIsAwesome)
            }
        }
    }
})
```

There are no matchers, helpful methods, etc. Just the runner and the specs. You are free to use whatever matcher library
you want. Though recommended library is [hamkrest](https://github.com/npryce/hamkrest)

### Available spec styles

#### Behavior spec

```kotlin
import org.junit.Assert.*
import run.smt.ktest.specs.BehaviorSpec

object MySpec2 : BehaviorSpec({
    val thisIsAwesome = true

    given("kotlin") {
        `when`("need integration/acceptance testing framework") {
            then("use kTest!") {
                assertTrue(thisIsAwesome)
            }
        }
    }
})
```

#### Feature spec

```kotlin
import org.junit.Assert.*
import run.smt.ktest.specs.FeatureSpec

object MySpec3 : FeatureSpec({
    feature("usage of kTest") {
        scenario("awesomeness") {
            assertTrue(true)
        }
    }
})
```

#### Free spec

```kotlin
import org.junit.Assert.*
import run.smt.ktest.specs.FreeSpec

object MySpec4 : FreeSpec({
    "kTest" - {
        "is awesome" {
            assertTrue(true)
        }
    }
})
```

#### Simple spec

```kotlin
import org.junit.Assert.*
import run.smt.ktest.specs.SimpleSpec

object MySpec5 : SimpleSpec({
    suite("kTest") {
        test("awesomeness test") {
            assertTrue(true)
        }
    }
})
```

#### Word spec

```kotlin
import org.junit.Assert.*
import run.smt.ktest.specs.WordSpec

object MySpec6 : WordSpec({
    "kTest" should {
        "provide awesomeness" {
            assertTrue(true)
        }
    }
})
```

## Providing meta information

```kotlin
import run.smt.ktest.specs.BehaviorSpec

object MySpec7 : BehaviorSpec({
    given("kTest with meta info", {
        disable("disabling reason")
    }) {
        `when`("a test", {
            invocations(4)
        }) {
            then("it should work", {
                threads(4)
                
                a<SomeAnnotation>("someAnnotationProperty" to value)
            }) {
                // some code...
            }
        }
    }
})
```

There is also similar way to define metadata for other specs, just look into signatures! :)

## Table testing

Imagine you have some scenario of test that should be used with different test data. There is a solution for such issues:

```kotlin
import run.smt.ktest.table.*
import run.smt.ktest.specs.BehaviorSpec

object MySumSpec : BehaviorSpec({
    given("two numbers") {
        // you can use up to 22 columns!
        val myTestData = table(
            headers("my test parameter 1", "my test parameter 2", "my expected result"),
            row(1, 2, 3),
            row(3, 2, 5),
            row(-4, 2, -2)
        )
        `when`("called sum on them") {
            // for negative tables there is also `forNone`
            forAll(myTestData) { a, b, sum ->
                then("sum should be correct [$a, $b]") {
                    assert((a + b) == sum)
                }
            }
        }
    }
})
```

## Extending

### Writing you own spec style

1. Define your spec style:
    ```kotlin
    import run.smt.ktest.api.BaseSpec
    import run.smt.ktest.api.internal.SpecBuilder
    import run.smt.ktest.api.MetaInfoDSL
    
    abstract class SuiteAndCheckSpec(body: SuiteAndCheckSpec.() -> Unit) : BaseSpec() {
        init { body() }
    
        infix fun String.suite(body: () -> Unit) = SpecBuilder.suite(this, body)
        fun String.suite(metaInfo: MetaInfoDSL, body: () -> Unit) = SpecBuilder.suite(this, metaInfo, body)
        infix fun String.check(body: () -> Unit) = SpecBuilder.case(this, body)
        fun String.check(metaInfo: MetaInfoDSL, body: () -> Unit) = SpecBuilder.case(this, metaInfo, body)
    }
    ```

2. Use your brand new spec style:
    ```kotlin
    import org.junit.Assert.*
    import SuiteAndCheckSpec
    
    object MySpec8 : SuiteAndCheckSpec({
        "kTest even supports" suite {
            "my custom spec styles" check {
                assertTrue(true)
            }
        }
    })
    ```

3. PROFIT!
