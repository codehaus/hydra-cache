import org.apache.log4j.Logger

onNewInstance = {klass, type, instance ->
    injectLog(klass, instance)
}

private def injectLog(klass, instance) {
    instance.metaClass.log = Logger.getLogger(klass)
}