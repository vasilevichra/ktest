# kTest

[![CircleCI](https://circleci.com/gh/saksmt/ktest.svg?style=shield)](https://circleci.com/gh/saksmt/ktest)

kTest is integration/acceptance/system/any other non-unit test oriented modular test framework in Kotlin.
Inspired by [kotlintest](https://github.com/kotlintest/kotlintest), [specs2](https://github.com/etorreborre/specs2) and martians. 

## Example (with usage of all available modules)

```kotlin
object MySpec : AllureSpec({
    beforeAll {
        rest["backend"] {
            url {
                internal / caches
            }.DELETE(queryParam("force", "true"))
        }
    }

    epic("Search") {
        feature("Account by customer search") {
            story("Single criteria search") {
                val testTable = table(
                    header("criteriaName", "criteriaValue", "expectedJsonName"),
                    row("billing", ">100", "richAccounts.json"),
                    row("region", "Central", "centralRegionAccounts.json"),
                    row("validTill", ">${LocalDate.now().format(DateTimeFormatter.ISO_DATE)}", "activeAccounts.json")
                )
                // should be generated right before test
                val myGeneratedCustomer: Customer = testData["customer.json"]
                
                forAll(testTable) { criteriaName, criteriaValue, expectedJsonName ->
                    val criteria = mapOf<String, String>(
                        criteriaName, criteriaValue
                    )
                    
                    restTest(name = { "Search account by \"$criteriaName\": ${it.method}" }, metaData = {
                        category<Complex>()
                        flaky()
                    }) {
                        url { customers / param("customerId") / accounts }
                        
                        GET(queryParams(criteria), pathParam("customerId", myGeneratedCustomer.id))
                        POST(body(criteria), pathParam("customerId", myGeneratedCustomer.id))
                        
                        expect { response: DocumentContext ->
                            with(DocumentContextMatchers) {
                                assertThat(response, matches(expectedJsonName.loadAsJsonPath()).afterRemovalOfSubtree {
                                    "account[].metaData" {
                                        + "date"
                                        + "IP"
                                    }
                                })
                            }
                        }
                    }
                }
            }
        }
    }
})
```

For more see [docs](doc/README.md)

## License

All source code is licensed under [MIT license](LICENSE)
