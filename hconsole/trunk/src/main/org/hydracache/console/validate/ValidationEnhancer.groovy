package org.hydracache.console.validate

import org.apache.log4j.Logger
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.ClassUtils

/**
 * Created by nick.zhu
 */
class ValidationEnhancer {
    static def log = Logger.getLogger(ValidationEnhancer)

    def validators = [
            nullable: {value, delegate, nullable ->
                if(nullable)
                    return true

                return value != null
            }
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
        Closure constraints = bean.getProperty("constraints")

        constraints.delegate = this

        constraints.call()

        return !model.hasErrors()
    }

    def methodMissing(String name, args) {
        def propertyValue = model.getProperty(name)

        def constraintsMap = args[0]

        boolean valid = true

        constraintsMap.each{constraint, config->
            log.debug "Executing validation constraint[${constraint}] with input[${config}]"

            def validator = validators[constraint]

            def success = validator.call(propertyValue, delegate, config)

            if(!success){
                log.debug "Rejecting property ${name} by constriant ${constraint}"
                model.errors.rejectValue(name,
                        buildErrorCode(name, constraint))
                valid = false
            }
        }

        return valid
    }

    private GString buildErrorCode(fieldName, constraint) {
        def className = StringUtils.uncapitalize(
                ClassUtils.getShortClassName(model.getClass()))

        return "${className}.${fieldName}.${constraint}.message"
    }


}