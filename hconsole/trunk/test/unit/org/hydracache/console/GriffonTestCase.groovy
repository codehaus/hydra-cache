package org.hydracache.console

import org.apache.log4j.Logger

/**
 * Created by nick.zhu
 */
class GriffonTestCase extends GroovyTestCase {

    def mockLogging(obj){
        obj.metaClass.log = Logger.getLogger(HydraSpaceService)
    }

    def mockApp(obj){
        obj.metaClass.app = [event: {event, args = [] -> appEvents[event] = args}]
    }

    def mockController(AddressBarController controller) {
        controller.metaClass.doLater = {closure -> closure.call() }
        controller.metaClass.doOutside = {closure -> closure.call() }
    }

}
