package org.hydracache.console

import org.apache.log4j.Logger
import org.codehaus.griffon.commons.GriffonContext

/**
 * Created by nick.zhu
 */
class GriffonTestCase extends GroovyTestCase {

    def mockLogging(obj){
        obj.metaClass.log = Logger.getLogger(HydraSpaceService)
    }

    def mockApp(obj){
        obj.metaClass.app = [event: {events[it] = []}]
    }

    def mockController(AddressBarController controller) {
        controller.metaClass.doLater = {closure -> closure.call() }
        controller.metaClass.doOutside = {closure -> closure.call() }
    }

}
