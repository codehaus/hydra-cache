package org.hydracache.console

import org.apache.log4j.Logger
import org.hydracache.client.HydraCacheAdminClient
import org.hydracache.server.Identity

class HydraSpaceServiceTests extends GroovyTestCase {
    HydraSpaceService service = new HydraSpaceService()
    def events = [:]

    void setUp() {
        service.metaClass.log = Logger.getLogger(HydraSpaceService)
        service.metaClass.app = [event: {events[it] = []}]
    }

    void tearDown() {
        events.clear()
    }

    void testSuccessfulConnection() {
        def stubAdminClient = [listNodes: {[new Identity(80)]}] as HydraCacheAdminClient

        service.hydraCacheClientFactory = [createAdminClient: {stubAdminClient}]

        def result = service.connect("localhost", "8888")

        assertTrue "Connection should be successful", result
        assertTrue "Event ${HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT} should be sent", events.containsKey(HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT)
    }

    void testServerNullHandlingInConnect() {
        def result = service.connect(null, "8888")

        assertFalse "Connection should not be successful", result
        assertFalse "Event ${HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT} should not be sent", events.containsKey(HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT)
    }

    void testPortNullHandlingInConnect() {
        def result = service.connect("server", null)

        assertFalse "Connection should not be successful", result
        assertFalse "Event ${HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT} should not be sent", events.containsKey(HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT)
    }

    void testConnectHandleInvalidServerName(){
        def result = service.connect("invalid name", "8080")

        assertFalse "Connection should not be successful", result
        assertFalse "Event ${HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT} should not be sent", events.containsKey(HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT)
    }

    void testConnectHandleInvalidPortNumber(){
        def result = service.connect("localhost", "port")

        assertFalse "Connection should not be successful", result
        assertFalse "Event ${HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT} should not be sent", events.containsKey(HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT)
    }
}
