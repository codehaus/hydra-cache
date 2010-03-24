package org.hydracache.console.validate

/**
 * Created by nick.zhu
 */
class ErrorsTest extends GroovyTestCase {

    public void testRejectValue(){
        Errors errors = new Errors()

        errors.rejectValue("field", "errorCode")

        assertTrue "Should have field error", errors.hasFieldErrors()
        assertTrue "Field should have error", errors.hasFieldErrors("field")
    }

    public void testRejectValueWithArgs(){
        Errors errors = new Errors()

        errors.rejectValue("field", "errorCode", [10, "arg2"])

        FieldError fieldError = errors.getFieldError("field")

        assertEquals "Field error field value is incorrect", fieldError.field, "field"
        assertEquals "Error code is incorrect", fieldError.errorCode, "errorCode"
        assertEquals "Field error args is incorrect", fieldError.arguments[0], 10
        assertEquals "Field error args is incorrect", fieldError.arguments[1], "arg2"
    }

}
