package org.hydracache.console

class SpaceDashboardController {
    // these will be injected by Griffon
    def model
    def view
    def hydraSpaceService

    void mvcGroupInit(Map args) {
        model.serverNodes = args.nodes

        // Use passed in hydraSpaceService instance since Griffon inject
        // fails when mvcGroupInit is called in a app event thread with doLater{}
        model.storageInfo = args.hydraSpaceService.queryStorageInfo()

        model.updateOverview()
    }
}