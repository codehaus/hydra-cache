import org.apache.log4j.Logger
import org.hydracache.console.validate.ValidationEnhancer

onNewInstance = {klass, type, instance ->
    injectLog(klass, instance)

    if (type == "model") {
        enhanceModelWithValidationSupport(instance)
    }
}

private def injectLog(klass, instance) {
    instance.metaClass.log = Logger.getLogger(klass)
}

private def enhanceModelWithValidationSupport(instance) {
    new ValidationEnhancer(instance)
}

