package org.hydracache.console

import static org.hydracache.console.ConnectionState.*

class AddressBarController {
    def model
    def view

    def hydraSpaceService

    void mvcGroupInit(Map args) {
        // this method is called after model and view are injected
    }

    def connect = {evt = null ->
        if (!model.validate()) {
            view.errorMessagePanel.errors = model.errors
        } else {
            view.errorMessagePanel.errors = null
            model.connectionState = CONNECTING

            doOutside {
                try {
                    hydraSpaceService.connect(model.server, model.port)
                } catch (Exception ex) {
                    doLater{
                        model.connectionState = DIS_CONNECTED
                    }

                    throw ex
                }
            }
        }
    }

    def disconnect = {evt = null ->
        doOutside {
            hydraSpaceService.disconnect()
        }
    }

    def onHydraSpaceConnected = {nodes, storageInfo ->
        log.debug "Event [HydraSpaceConnected] received ..."

        doLater {
            model.connectionState = CONNECTED
        }
    }

    def onHydraSpaceDisConnected = {
        log.debug "Event [HydraSpaceDisConnected] received ..."

        doLater {
            model.connectionState = DIS_CONNECTED
        }
    }
}