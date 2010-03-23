package org.hydracache.console

class AddressBarController {
    def model
    def view

    def hydraSpaceService

    void mvcGroupInit(Map args) {
        // this method is called after model and view are injected
    }

    def connect = { evt = null ->
        hydraSpaceService.connect(model.server, model.port)
    }

    def onHydraSpaceConnected = {
        log.debug "Event [HydraSpaceConnected] received"
    }
}