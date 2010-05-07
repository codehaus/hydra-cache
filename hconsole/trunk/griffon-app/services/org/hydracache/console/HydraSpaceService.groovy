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
            connectToHydraSpace(server, port)

            List<Identity> nodes = listAllNodesInSpace(hydraCacheAdminClient)

            if (!nodes)
                return false

            def storageInfo = queryStorageInfo(hydraCacheAdminClient)

            app.event(HYDRA_SPACE_CONNECTED_EVENT, [nodes, storageInfo])

            log.debug "[${HYDRA_SPACE_CONNECTED_EVENT}] event sent"

            return true
        } catch (UnknownHostException ex) {
            log.debug("Unknown host", ex)
            return false;
        }
    }

    private List<Identity> listAllNodesInSpace(hydraCacheAdminClient) {
        List<Identity> nodes = hydraCacheAdminClient.listNodes()
        return nodes
    }

    private void connectToHydraSpace(server, port) {
        def serverAddress = Inet4Address.getByName(server)

        hydraCacheClient = hydraCacheClientFactory.createClient(
                [new Identity(serverAddress, port)]
        )

        hydraCacheAdminClient = hydraCacheClientFactory.createAdminClient(
                [new Identity(serverAddress, port)]
        )
    }

    def disconnect() {
        hydraCacheClient?.shutdown()
        hydraCacheAdminClient?.shutdown()

        app.event(HYDRA_SPACE_DISCONNECTED_EVENT)

        log.debug "[${HYDRA_SPACE_DISCONNECTED_EVENT}] event sent"
    }

    def queryStorageInfo(adminClient) {
        return adminClient?.getStorageInfo()
    }

    def queryServerDetails(serverIdentity) {
        def adminClient = hydraCacheClientFactory.createAdminClient(
                [serverIdentity]
        )

        try {
            return queryStorageInfo(adminClient)
        } finally {
            adminClient.shutdown()
        }
    }

    def get(context, key) {
        if (!hydraCacheClient)
            return null

        return hydraCacheClient.get(context, key)
    }

    def put(context, key, value) {
        if (hydraCacheClient)
            hydraCacheClient.put(context, key, value)
    }

}