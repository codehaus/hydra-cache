package org.hydracache.console

import org.hydracache.client.HydraCacheAdminClient
import org.hydracache.server.Identity
import org.hydracache.client.HydraCacheClient

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
        def stubAdminClient = [listNodes: {[new Identity(80)]}, getStorageInfo: {[:]}] as HydraCacheAdminClient

        service.hydraCacheClientFactory = [createAdminClient: {stubAdminClient}, createClient: {stubAdminClient}]

        def result = service.connect("localhost", 8888)

        assertTrue "Connection should be successful", result
        assertTrue "Event ${HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT} should be sent", appEvents.containsKey(HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT)
    }

    void testServerNullHandlingInConnect() {
        def result = service.connect(null, 8888)

        assertFalse "Connection should not be successful", result
        assertFalse "Event ${HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT} should not be sent", appEvents.containsKey(HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT)
    }

    void testConnectHandleInvalidServerName() {
        def result = service.connect("invalid name", 8080)

        assertFalse "Connection should not be successful", result
        assertFalse "Event ${HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT} should not be sent", appEvents.containsKey(HydraSpaceService.HYDRA_SPACE_CONNECTED_EVENT)
    }

    void testDisconnectShutdownClient() {
        boolean shutdown = false

        service.hydraCacheClient = [shutdown: {shutdown = true}]

        service.disconnect()

        assertTrue "Client should have been stoppped", shutdown
        assertTrue "Event ${HydraSpaceService.HYDRA_SPACE_DISCONNECTED_EVENT} should be sent", appEvents.containsKey(HydraSpaceService.HYDRA_SPACE_DISCONNECTED_EVENT)
    }

    void testQueryStorageInfo() {
        def data = ['maxMemroy': '250 MB']

        def info = service.queryStorageInfo([getStorageInfo: {data}])

        assertEquals "Result is incorrect", data, info
    }

    void testQueryServerDetails() {
        def shutdown = false
        def adminClient = [getStorageInfo: {['maxMemory': '250 MB']}, shutdown: {shutdown = true}]
        def clientFactory = [createAdminClient: {id -> adminClient}]

        service.hydraCacheClientFactory = clientFactory

        def info = service.queryServerDetails(new Identity(80))

        assertEquals "Server info is incorrect", '250 MB', info.maxMemory
        assertTrue 'Client should be shutdown once its done', shutdown
    }

    void testQueryServerDetailsWithException() {
        def shutdown = false
        def adminClient = [getStorageInfo: {throw new RuntimeException()}, shutdown: {shutdown = true}]
        def clientFactory = [createAdminClient: {id -> adminClient}]

        service.hydraCacheClientFactory = clientFactory

        try {
            service.queryServerDetails(new Identity(80))
        } catch (Exception ex) {
            // ignore
        }

        assertTrue 'Client should be shutdown once its done', shutdown
    }

    void testGet() {
        def stubClient = [get: {ctx, key -> "result"}] as HydraCacheClient

        service.hydraCacheClient = stubClient

        assertEquals "Get result is incorrect", "result", service.get("context", "something")
    }

    void testGetWithNoClient() {
        assertNull "Get result is incorrect", service.get("context", "something")
    }

    void testPut() {
        def value
        def stubClient = [get: {ctx, key -> value}, put: {ctx, key, v -> value = v}] as HydraCacheClient

        service.hydraCacheClient = stubClient

        service.put("context", "key", "something")

        assertEquals "Value is not put correctly", "something", service.get("context", "key")
    }

    void testPutWithNoClient() {
        service.put("context", "key", "something")
    }
}
