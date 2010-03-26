package org.hydracache.console.validate

import org.apache.log4j.Logger
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.ClassUtils

/**
 * Created by nick.zhu
 */
class ValidationEnhancer {
    static final def CONSTRAINT_PROPERTY_NAME = "constraints"

    static def log = Logger.getLogger(ValidationEnhancer)

    def validators = [
            nullable: new NullableValidator(this)
    ]

    def model

    public ValidationEnhancer(bean) {
        model = bean

        bean.metaClass.validate = {
            validate(bean)
        }

        bean.metaClass.errors = new Errors()
        bean.metaClass.hasErrors = { model.errors.hasErrors() }
    }

    def validate(bean) {
        if (!bean.hasProperty(CONSTRAINT_PROPERTY_NAME))
            return true

        Closure constraints = bean.getProperty(CONSTRAINT_PROPERTY_NAME)

        constraints.delegate = this

        constraints.call()

        return !model.hasErrors()
    }

    def methodMissing(String name, args) {
        if (!model.hasProperty(name)) {
            throw new IllegalStateException("""Invalid constraint configuration detected.
                    Property [${name}] with constraint configured is missing.""")
        }

        def propertyValue = model.getProperty(name)

        def constraintsMap = args[0]

        boolean valid = true

        constraintsMap.each {constraint, config ->
            log.debug "Executing validation constraint[${constraint}] with input[${config}]"

            def validator = validators[constraint]

            if (validator) {
                def success = validator.call(propertyValue, delegate, config)

                if (!success) {
                    log.debug "Rejecting property ${name} by constriant ${constraint}"
                    model.errors.rejectValue(name,
                            buildErrorCode(name, constraint), buildErrorArguments(name, model, propertyValue))
                    valid = false
                }
            }else{
                log.warn """Ignoring unknown validator[${constraint}], please check your constraint configuration"""
            }
        }

        return valid
    }

    private GString buildErrorCode(fieldName, constraint) {
        def className = StringUtils.uncapitalize(
                ClassUtils.getShortClassName(model.getClass()))

        return "${className}.${fieldName}.${constraint}.message"
    }

    private List buildErrorArguments(String name, model, propertyValue) {
        return [name, ClassUtils.getShortClassName(model.getClass()), "${propertyValue}"]
    }

}
