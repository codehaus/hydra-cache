package org.hydracache.console

import org.hydracache.client.HydraCacheAdminClient
import org.hydracache.client.HydraCacheClientFactory
import org.hydracache.server.Identity

class HydraSpaceService {
    public static final String HYDRA_SPACE_CONNECTED_EVENT = "HydraSpaceConnected"

    def hydraCacheClientFactory = new HydraCacheClientFactory()

    def connect(server, port) {
        if (!server || !port)
            return false

        log.debug "Connecting to ${server}:${port} ..."

        HydraCacheAdminClient adminClient = hydraCacheClientFactory.createAdminClient(
                [new Identity(Inet4Address.getByName(server), Integer.valueOf(port))]
        )

        def nodes = adminClient.listNodes()

        if (!nodes)
            return false

        app.event(HYDRA_SPACE_CONNECTED_EVENT)

        log.debug "[HydraSpaceConnected] event sent"

        return true
    }

}