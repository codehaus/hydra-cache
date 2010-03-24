package org.hydracache.console.validate

import org.apache.log4j.Logger

/**
 * Created by nick.zhu
 */
class ValidationEnhancer {
    static def log = Logger.getLogger(ValidationEnhancer)

    def validators = [
            nullable: {value, delegate, nullable ->
                if(nullable)
                    return true
                else
                    return value != null
            }
    ]

    def delegate

    public ValidationEnhancer(bean) {
        delegate = bean
        
        bean.metaClass.validate = {
            validate(bean)
        }

        bean.metaClass.errors = []
        bean.metaClass.hasErrors = {delegate.errors && delegate.errors.size() > 0}
    }

    def validate(bean) {
        Closure constraints = bean.getProperty("constraints")

        constraints.delegate = this

        constraints.call()

        return delegate.hasErrors()
    }

    def methodMissing(String name, args) {
        def propertyValue = delegate.getProperty(name)

        def constraintsMap = args[0]

        boolean valid = true

        constraintsMap.each{constraint, config->
            log.debug "Executing validation constraint[${constraint}] with input[${config}]"

            def validator = validators[constraint]

            def result = validator.call(propertyValue, delegate, config)

            valid |= result
        }

        return valid
    }


}
