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
    }

}


class ModelBean {
    String id

    static constraints = {
        id(nullable: false)
    }
}