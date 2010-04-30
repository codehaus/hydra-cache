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
        log.debug "Updating SpaceDashboard ..."

        model.serverNodes = nodes
        model.storageInfo = storageInfo

        model.updateOverview()
    }
}