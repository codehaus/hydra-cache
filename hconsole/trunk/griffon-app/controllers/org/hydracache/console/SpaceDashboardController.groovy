package org.hydracache.console

class SpaceDashboardController {
    // these will be injected by Griffon
    def model
    def view
    def hydraSpaceService

    void mvcGroupInit(Map args) {
        model.serverNodes = args.nodes

        model.storageInfo = args.storageInfo

        model.updateOverview()
    }
}