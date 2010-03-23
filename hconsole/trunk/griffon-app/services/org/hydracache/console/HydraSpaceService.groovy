package org.hydracache.console

class HydraSpaceService {
    def connect(server, port) {
        log.debug "Connecting to ${server}:${port} ..."

        app.event("HydraSpaceConnected")

        log.debug "[HydraSpaceConnected] event sent"
    }

}