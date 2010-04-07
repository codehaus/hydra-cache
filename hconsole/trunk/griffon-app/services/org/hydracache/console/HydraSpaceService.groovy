package org.hydracache.console

import org.hydracache.client.HydraCacheAdminClient
import org.hydracache.client.HydraCacheClientFactory
import org.hydracache.server.Identity
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.math.NumberUtils
import org.apache.log4j.spi.LoggerFactory
import org.apache.log4j.Logger

class HydraSpaceService {
    public static final String HYDRA_SPACE_CONNECTED_EVENT = "HydraSpaceConnected"

    def hydraCacheClientFactory = new HydraCacheClientFactory()

    def connect(server, port) {
        if (!server)
            return false

        log.debug "Connecting to ${server}:${port} ..."

        try {
            def serverAddress = Inet4Address.getByName(server)

            HydraCacheAdminClient adminClient = hydraCacheClientFactory.createAdminClient(
                    [new Identity(serverAddress, port)]
            )

            def nodes = adminClient.listNodes()

            if (!nodes)
                return false

            app.event(HYDRA_SPACE_CONNECTED_EVENT, nodes)
            log.debug "[HydraSpaceConnected] event sent"

            return true
        } catch (UnknownHostException uhex) {
            log.debug("Unknown host", uhex)            
            return false;
        }
    }

}