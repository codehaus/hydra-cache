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
            nullable: new NullableValidator(this),
            blank: new BlankValidator(this)
    ]

    def model

    public ValidationEnhancer(bean) {
        model = bean

        bean.metaClass.validate = {
            validate()
        }

        bean.metaClass.errors = new Errors()
        bean.metaClass.hasErrors = { model.errors.hasErrors() }
    }

    def validate() {
        model.errors.clear()

        if (!model.hasProperty(CONSTRAINT_PROPERTY_NAME))
            return true

        Closure constraints = model.getProperty(CONSTRAINT_PROPERTY_NAME)

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

        boolean valid = processConstraints(args, propertyValue, name)

        return valid
    }

    private boolean processConstraints(args, propertyValue, String name) {
        boolean valid = true

        def constraintsMap = args[0]

        constraintsMap.each {constraint, config ->
            valid = validate(constraint, propertyValue, config, name)
        }

        return valid
    }

    private def validate(constraint, propertyValue, config, name) {
        log.debug "Executing validation constraint[${constraint}] with input[${config}]"

        def validator = validators[constraint]

        if (validator) {
            return executeValidator(validator, propertyValue, config, name, constraint)
        } else {
            return handleMissingValidator(constraint)
        }
    }

    private def executeValidator(validator, propertyValue, config, name, constraint) {
        def success = validator.call(propertyValue, model, config)

        if (success)
            return true

        log.debug "Rejecting property ${name} by constriant ${constraint}"

        model.errors.rejectValue(name,
                buildErrorCode(name, constraint), buildErrorArguments(name, model, propertyValue))

        return false
    }

    private def handleMissingValidator(constraint) {
        log.warn """Ignoring unknown validator[${constraint}], please check your constraint configuration"""
        return true
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
