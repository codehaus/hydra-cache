package org.hydracache.console.validate

/**
 * Created by nick.zhu
 */
class ValidationEnhancerTest extends GroovyTestCase {

    public void testModelConstraintInvocation() {
        ModelBean model = new ModelBean()

        ValidationEnhancer validator = new ValidationEnhancer(model)

        boolean result = model.validate()

        assertFalse("Validation result should be false", result)
        assertTrue("Model should have error", model.hasErrors())
        assertTrue("id field should have error", model.errors.hasFieldErrors('id'))

        def fieldError = model.errors.getFieldError('id')

        assertEquals("Error code is not correct", "modelBean.id.nullable.message", fieldError.errorCode)
        assertEquals("Error arg is not correct", "id", fieldError.arguments[0])
        assertEquals("Error arg is not correct", "ModelBean", fieldError.arguments[1])
        assertEquals("Error arg is not correct", "null", fieldError.arguments[2])
    }

    public void testValidationWithNoConstraint() {
        NoConstraintModelBean model = new NoConstraintModelBean()

        ValidationEnhancer validator = new ValidationEnhancer(model)

        boolean result = model.validate()

        assertTrue("Validation result should be true", result)
    }

    public void testValidationWithInvalidConstraint() {
        InvalidConstraintModelBean model = new InvalidConstraintModelBean()

        ValidationEnhancer validator = new ValidationEnhancer(model)

        try {
            boolean result = model.validate()
            fail("Exception should be thrown with invalid constraint configuration")
        } catch (IllegalStateException ex) {
            // good
        }
    }

    public void testUnknownConstraintIsIgnored() {
        UnknownConstraintModelBean model = new UnknownConstraintModelBean()

        ValidationEnhancer validator = new ValidationEnhancer(model)

        boolean result = model.validate()

        assertTrue("Validation result should be true", result)
    }

}


class ModelBean {
    String id

    static constraints = {
        id(nullable: false)
    }
}

class NoConstraintModelBean {
}

class InvalidConstraintModelBean {
    String id

    static constraints = {
        missing(nullable: false)
    }
}

class UnknownConstraintModelBean {
    String id

    static constraints = {
        id(missing: false)
    }
}