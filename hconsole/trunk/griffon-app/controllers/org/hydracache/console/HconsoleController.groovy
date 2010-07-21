package org.hydracache.console

import org.hydracache.console.util.ClosureTimerTask

class HconsoleController {
    static final String SPACE_DASHBOARD = "SpaceDashboard"

    def model
    def view
    def hydraSpaceService

    void mvcGroupInit(Map args) {
        createMVCGroup("NavigationPane", [navigationPane: view.navigationPane])
        createMVCGroup("AddressBar", [addressBar: view.addressBar])
        createMVCGroup("PlayGround", [playgroundPane: view.playgroundPane])
    }

    def quit = {evt = null ->
        app.shutdown()
    }

    def onHydraSpaceConnected = {nodes, storageInfo ->
        log.debug "Event [HydraSpaceConnected] received ..."

        doOutside {
            log.debug "Creating SpaceDashboard ..."

            doLater {
                def mainTabGroup = view.tabGroup

                createMVCGroup(SPACE_DASHBOARD,
                        SPACE_DASHBOARD,
                        ['tabGroup': mainTabGroup])

                app.controllers[SPACE_DASHBOARD].update(nodes, storageInfo)
            }
        }

        log.debug "Event [HydraSpaceConnected] processd"
    }

    def onHydraSpaceDisConnected = {
        log.debug "Event [HydraSpaceDisConnected] received ..."

        doLater {
            view.tabGroup.removeAll()
        }

        log.debug "Event [HydraSpaceDisConnected] processd"
    }

}