import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator

onBootstrapEnd = {
    // TODO: hack to make log4j work, investigate why log4j configuration does not work in Config.groovy
    PropertyConfigurator.configure(getClass().getResource('/log4j.properties'))
}

onNewInstance = {klass, type, instance ->
    injectLog(klass, instance)
}

private def injectLog(klass, instance) {
    instance.metaClass.log = Logger.getLogger(klass)
}

