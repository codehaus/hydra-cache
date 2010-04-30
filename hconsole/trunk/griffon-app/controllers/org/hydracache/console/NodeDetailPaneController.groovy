package org.hydracache.console

class NodeDetailPaneController {
    // these will be injected by Griffon
    def model
    def view

    void mvcGroupInit(Map args) {
        model.storageInfo = args.storageInfo
        model.server = args.server

        doLater {
            model.updateDetails()
        }
    }
}