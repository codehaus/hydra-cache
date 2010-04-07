package org.hydracache.console

import org.hydracache.client.HydraCacheAdminClient
import org.hydracache.server.Identity

class HydraSpaceServiceTests extends GriffonTestCase {
    HydraSpaceService service = new HydraSpaceService()
    def appEvents = [:]

    void setUp() {
        mockLogging(service)
        mockApp(service)
    }

    void tearDown() {
        appEvents.clear()
    }

    void testSuccessfulConnection() {
        def stubAdminClient = [listNodes: {[new Identity(80)]}] as HydraCacheAdminClient

        service.hydraCacheClientFactory = [createAdminClient: {stubAdminClient}, createClient:{stubAdminClient}]

        def result = service.connect("localhost", 8888)

        assertTrue "Connection should be successful", result
        assertTrue "Event ${HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT} should be sent", appEvents.containsKey(HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT)
    }

    void testServerNullHandlingInConnect() {
        def result = service.connect(null, 8888)

        assertFalse "Connection should not be successful", result
        assertFalse "Event ${HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT} should not be sent", appEvents.containsKey(HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT)
    }

    void testConnectHandleInvalidServerName(){
        def result = service.connect("invalid name", 8080)

        assertFalse "Connection should not be successful", result
        assertFalse "Event ${HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT} should not be sent", appEvents.containsKey(HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT)
    }

    void testDisconnectShutdownClient(){
        boolean shutdown = false

        service.hydraCacheClient = [shutdown:{shutdown=true}]

        service.disConnect()

        assertTrue "Client should have been stoppped", shutdown
        assertTrue "Event ${HydraSpaceService.HYDRA_SPACE_DISCONNECTED_EVENT} should be sent", appEvents.containsKey(HydraSpaceService.HYDRA_SPACE_DISCONNECTED_EVENT)
    }
}
