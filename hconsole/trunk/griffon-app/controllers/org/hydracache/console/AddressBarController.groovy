package org.hydracache.console

import javax.swing.JOptionPane

class AddressBarController {
    def model
    def view

    def hydraSpaceService

    void mvcGroupInit(Map args) {
        // this method is called after model and view are injected
    }

    def connect = {evt = null ->
        if (!model.validate()) {
            doLater {
                view.errorMessagePanel.errors = model.errors
            }
        } else {
            doLater {
                view.errorMessagePanel.errors = null
            }

            doOutside {
                hydraSpaceService.connect(model.server, model.port)
            }
        }
    }

    def disConnect = {evt=null ->
        doOutside{
            hydraSpaceService.disConnect()
        }
    }

    def onHydraSpaceConnected = {nodes->
        log.debug "Event [HydraSpaceConnected] received ..."

        doLater{
            model.connected = true
        }
    }

    def onHydraSpaceDisConnected = {nodes->
        log.debug "Event [HydraSpaceDisConnected] received ..."

        doLater{
            model.connected = false
        }
    }
}