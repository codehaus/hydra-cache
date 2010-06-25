package org.hydracache.console

import org.hydracache.client.HydraCacheClientFactory
import org.hydracache.server.Identity
import org.hydracache.console.util.ClosureTimerTask

class HydraSpaceService {
    public static final String HYDRA_SPACE_CONNECTED_EVENT = "HydraSpaceConnected"
    public static final String HYDRA_SPACE_DISCONNECTED_EVENT = "HydraSpaceDisConnected"
    public static final String HYDRA_SPACE_UPDATED_EVENT = "HydraSpaceUpdated"


    static final long UPDATER_INIT_DELAY = 5000
    static final long UPDATER_INTERVAL = 10000

    def hydraCacheClientFactory = new HydraCacheClientFactory()
    def hydraCacheAdminClient
    def hydraCacheClient

    def spaceUpdater = new Timer()

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

            log.debug "Creating space updater timer"

            spaceUpdater.schedule(new ClosureTimerTask(closure: {
                log.debug "Updating space overview..."

                def nds = listAllNodesInSpace()
                def sinfo = queryStorageInfo()

                app.event(HYDRA_SPACE_UPDATED_EVENT, [nds, sinfo])

                log.debug "Space overview updated"
            }), UPDATER_INIT_DELAY, UPDATER_INTERVAL)

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

    def listAllNodesInSpace() {
        return listAllNodesInSpace(hydraCacheAdminClient)
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

    def queryStorageInfo() {
        return queryStorageInfo(hydraCacheAdminClient)
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

        log.debug "Getting value with key[${key}] from context[${context}]"
        def value = hydraCacheClient.get(context, key)
        return value
    }

    def put(context, key, value) {
        if (hydraCacheClient) {
            log.debug "Putting value with key[${key}] in context[${context}]"
            hydraCacheClient.put(context, key, value)
        }
    }

}