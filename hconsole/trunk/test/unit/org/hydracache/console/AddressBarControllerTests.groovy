package org.hydracache.console

import groovy.mock.interceptor.*
import net.sourceforge.gvalidation.Errors

/**
 * Created by nick.zhu
 */
class AddressBarControllerTests extends GriffonTestCase {

    public void testConnectValidationFailure() {
        def controller = new AddressBarController()

        def expectedErrors = new Errors()
        def errorMsgPanel = [:]

        mockController(controller)

        controller.model = [validate: {false}, errors: expectedErrors]
        controller.view = [errorMessagePanel: errorMsgPanel]

        controller.connect()

        assertEquals "Errors were not generated", expectedErrors, errorMsgPanel.errors
    }

    public void testConnectExceptionRollbackConnectionState() {
        def controller = new AddressBarController()

        def errorMsgPanel = [:]
        def mockHydraService = [connect:{server, port -> throw new RuntimeException() }]

        mockController(controller)

        controller.model = [validate: {true}, server: "server", port: "90"]
        controller.view = [errorMessagePanel: errorMsgPanel]
        controller.hydraSpaceService = mockHydraService

        try {
            controller.connect()
            fail('Should have exception thrown already')
        } catch (Exception ex) {
            assertEquals "Connection state should be restored", ConnectionState.DIS_CONNECTED, controller.model.connectionState
        }
    }

    public void testConnectSuccess() {
        def controller = new AddressBarController()

        def errorMsgPanel = [:]
        def mockHydraServiceContext = new MockFor(HydraSpaceService)
        mockHydraServiceContext.demand.connect(1) {
            server, port ->
            assertEquals("server", server)
            assertEquals("90", port)
            true
        }

        mockController(controller)

        controller.model = [validate: {true}, server: "server", port: "90"]
        controller.view = [errorMessagePanel: errorMsgPanel]
        controller.hydraSpaceService = mockHydraServiceContext.proxyInstance()

        controller.connect()

        mockHydraServiceContext.verify controller.hydraSpaceService
    }

}
