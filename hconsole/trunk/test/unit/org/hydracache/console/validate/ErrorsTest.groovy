package org.hydracache.console.validate

/**
 * Created by nick.zhu
 */
class ErrorsTest extends GroovyTestCase {

    public void testHasErrors(){
        Errors errors = new Errors()

        errors.reject("errorCode")

        assertTrue "Should have error", errors.hasErrors()

        errors.clear()

        assertFalse "Should not have any error", errors.hasErrors()

        errors.rejectValue("field", "errorCode")

        assertTrue "Should have error", errors.hasErrors()
    }

    public void testReject(){
        Errors errors = new Errors()

        errors.reject("errorCode")

        assertTrue "Should have global error", errors.hasGlobalErrors()
    }

    public void testRejectWithArgs(){
        Errors errors = new Errors()

        errors.reject("errorCode", [10, "arg2"])

        def globalErrors = errors.getGlobalErrors()

        assertEquals "Error code is incorrect", globalErrors.first().errorCode, "errorCode"
        assertEquals "Error args is incorrect", globalErrors.first().arguments[0], 10
        assertEquals "Error args is incorrect", globalErrors.first().arguments[1], "arg2"
    }

    public void testRejectValue(){
        Errors errors = new Errors()

        errors.rejectValue("field", "errorCode")

        assertTrue "Should have field error", errors.hasFieldErrors()
        assertTrue "Field should have error", errors.hasFieldErrors("field")
    }

    public void testRejectValueWithArgs(){
        Errors errors = new Errors()

        errors.rejectValue("field", "errorCode", [10, "arg2"])

        def fieldError = errors.getFieldError("field")

        assertEquals "Field error field value is incorrect", fieldError.field, "field"
        assertEquals "Error code is incorrect", fieldError.errorCode, "errorCode"
        assertEquals "Field error args is incorrect", fieldError.arguments[0], 10
        assertEquals "Field error args is incorrect", fieldError.arguments[1], "arg2"
    }

}
