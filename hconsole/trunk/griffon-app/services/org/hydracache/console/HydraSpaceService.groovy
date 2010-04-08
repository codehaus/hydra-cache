package org.hydracache.console

import org.hydracache.client.HydraCacheClientFactory
import org.hydracache.server.Identity

class HydraSpaceService {
    public static final String HYDRA_SPACE_CONNECTED_EVENT = "HydraSpaceConnected"
    public static final String HYDRA_SPACE_DISCONNECTED_EVENT = "HydraSpaceDisConnected"

    def hydraCacheClientFactory = new HydraCacheClientFactory()
    def hydraCacheAdminClient
    def hydraCacheClient

    def connect(server, port) {
        if (!server)
            return false

        log.debug "Connecting to ${server}:${port} ..."

        try {
            List<Identity> nodes = connectToHydraSpace(server, port)

            if (!nodes)
                return false

            def storageInfo = queryStorageInfo()

            app.event(HYDRA_SPACE_CONNECTED_EVENT, [nodes, storageInfo])
            
            log.debug "[${HYDRA_SPACE_CONNECTED_EVENT}] event sent"

            return true
        } catch (UnknownHostException ex) {
            log.debug("Unknown host", ex)
            return false;
        }
    }

    private List<Identity> connectToHydraSpace(server, port) {
        def serverAddress = Inet4Address.getByName(server)

        hydraCacheClient = hydraCacheClientFactory.createClient(
                [new Identity(serverAddress, port)]
        )

        hydraCacheAdminClient = hydraCacheClientFactory.createAdminClient(
                [new Identity(serverAddress, port)]
        )

        def nodes = hydraCacheAdminClient.listNodes()

        return nodes
    }

    def disConnect() {
        hydraCacheClient?.shutdown()
        hydraCacheAdminClient?.shutdown()
        
        app.event(HYDRA_SPACE_DISCONNECTED_EVENT)

        log.debug "[${HYDRA_SPACE_DISCONNECTED_EVENT}] event sent"
    }

    def queryStorageInfo(){
        return hydraCacheAdminClient?.getStorageInfo()
    }

}