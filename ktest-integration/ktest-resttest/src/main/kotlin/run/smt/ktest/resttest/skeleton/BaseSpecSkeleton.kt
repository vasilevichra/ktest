package run.smt.ktest.resttest.skeleton

import run.smt.ktest.api.BaseSpec
import run.smt.ktest.api.internal.SpecBuilder
import run.smt.ktest.resttest.api.RestTestSpecSkeleton
import run.smt.ktest.resttest.api.TestSpecProvider

class BaseSpecSkeleton : RestTestSpecSkeleton<BaseSpec> {
    override fun BaseSpec.execRestTest(restTestTemplate: TestSpecProvider) {
        restTestTemplate { annotations, name, body ->
            SpecBuilder.case(name, annotations, body)
        }
    }
}
