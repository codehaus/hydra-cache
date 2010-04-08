package org.hydracache.console
class HconsoleController {
    def model
    def view
    def hydraSpaceService

    void mvcGroupInit(Map args) {
        createMVCGroup("NavigationPane", [navigationPane: view.navigationPane])
        createMVCGroup("AddressBar", [addressBar: view.addressBar])
    }

    def quit = {evt = null ->
        app.shutdown()
    }

    def onHydraSpaceConnected = {nodes, storageInfo ->
        log.debug "Event [HydraSpaceConnected] received ..."
        
        doLater {
            log.debug "Creating SpaceDashboard ..."
            createMVCGroup('SpaceDashboard',
                    'spaceDashboard',
                    [tabGroup: view.tabGroup, nodes: nodes, storageInfo: storageInfo])
        }
    }

}