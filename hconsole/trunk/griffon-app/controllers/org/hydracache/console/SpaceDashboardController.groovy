package org.hydracache.console

class SpaceDashboardController {
    // these will be injected by Griffon
    def model
    def view
    def hydraSpaceService

    void mvcGroupInit(Map args) {
        log.debug "Initializing SpaceDashboard MVC ..."
    }

    def update(nodes, storageInfo) {
        doLater {
            log.debug "Updating SpaceDashboard ..."

            model.serverNodes = nodes
            model.storageInfo = storageInfo

            model.updateOverview()
        }
    }

    def onHydraSpaceUpdated = { nodes, storageInfo ->
        log.debug "Event [HydraSpaceUpdated] received ..."

        update(nodes, storageInfo)

        log.debug "Event [HydraSpaceUpdated] processd"
    }
}