package de.qaware.lombok.processor

import spock.lang.Specification
import spock.lang.Unroll

class HelloAnnotationHandlerSpec extends Specification {

    @Unroll
    def "Test #clazz"(String clazz) {
        expect:
        def given = TestCompiler.compileGiven(clazz)
        def expected = TestCompiler.compileExpected(clazz)
        given == expected

        where:
        clazz << [
            "AnnotatedMethod",
        ]
    }
}
