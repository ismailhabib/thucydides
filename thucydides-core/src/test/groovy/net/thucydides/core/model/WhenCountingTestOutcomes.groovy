package net.thucydides.core.model

import net.thucydides.core.reports.TestOutcomes
import spock.lang.Shared
import spock.lang.Specification

/**
 * A description goes here.
 * User: john
 * Date: 26/03/2014
 * Time: 7:39 AM
 */
class WhenCountingTestOutcomes extends Specification {

    class SomeTest {}


    List<TestOutcome> outcomes

    def setup() {
        def outcome1 = TestOutcome.forTest("test1",SomeTest).recordStep(TestStep.forStepCalled("step1").withResult(TestResult.SUCCESS))
        def outcome2 = TestOutcome.forTest("test2",SomeTest).recordStep(TestStep.forStepCalled("step1").withResult(TestResult.SUCCESS))
        def outcome3 = TestOutcome.forTest("test3",SomeTest).recordStep(TestStep.forStepCalled("step1").withResult(TestResult.SUCCESS))
        def outcome4 = TestOutcome.forTest("test3",SomeTest).recordStep(TestStep.forStepCalled("step1").withResult(TestResult.SUCCESS)).asManualTest()
        def outcome5 = TestOutcome.forTest("test4",SomeTest).recordStep(TestStep.forStepCalled("step1").withResult(TestResult.PENDING))
        def outcome6 = TestOutcome.forTest("test5",SomeTest).recordStep(TestStep.forStepCalled("step1").withResult(TestResult.PENDING))
        def outcome7 = TestOutcome.forTest("test5",SomeTest).recordStep(TestStep.forStepCalled("step1").withResult(TestResult.PENDING)).asManualTest()
        def outcome8 = TestOutcome.forTest("test5",SomeTest).recordStep(TestStep.forStepCalled("step1").withResult(TestResult.FAILURE))
        def outcome9 = TestOutcome.forTest("test5",SomeTest).recordStep(TestStep.forStepCalled("step1").withResult(TestResult.FAILURE)).asManualTest()
        def outcome10 = TestOutcome.forTest("test5",SomeTest).recordStep(TestStep.forStepCalled("step1").withResult(TestResult.ERROR))

        outcomes = [outcome1,outcome2,outcome3,outcome4,outcome5,outcome6,outcome7,outcome8,outcome9,outcome10]
    }

    def "should count proportion of test outcomes"() {
        expect:
        TestOutcomes.of(outcomes).proportionOf(TestType.ANY).withResult("success") == 0.4
        TestOutcomes.of(outcomes).proportionOf(TestType.ANY).withResult("pending") == 0.3
        TestOutcomes.of(outcomes).proportionOf(TestType.ANY).withIndeterminateResult() == 0.3
        TestOutcomes.of(outcomes).proportionOf(TestType.ANY).withResult("failure") == 0.2
        TestOutcomes.of(outcomes).proportionOf(TestType.ANY).withResult("error") == 0.1
    }

    def "should count proportion of automated test outcomes"() {
        expect:
        TestOutcomes.of(outcomes).proportionOf("automated").withResult("success") == 0.3
        TestOutcomes.of(outcomes).proportionOf("automated").withResult("pending") == 0.2
        TestOutcomes.of(outcomes).proportionOf("automated").withIndeterminateResult() == 0.2
        TestOutcomes.of(outcomes).proportionOf("automated").withResult("failure") == 0.1
    }

    def "should count proportion of manual test outcomes"() {
        expect:
        TestOutcomes.of(outcomes).proportionOf("manual").withResult("success") == 0.1
        TestOutcomes.of(outcomes).proportionOf("manual").withResult("pending") == 0.1
        TestOutcomes.of(outcomes).proportionOf("manual").withResult("failure") == 0.1
    }
}
