package org.hydracache.console

import org.hydracache.console.validate.Errors

/**
 * Created by nick.zhu
 */
class AddressBarControllerTests extends GriffonTestCase {

    public void testConnectValidationFailure(){
        def controller = new AddressBarController()

        def expectedErrors = new Errors()
        def errorMsgPanel = [:]

        mockController(controller)

        controller.model = [validate:{false}, errors:expectedErrors]
        controller.view = [errorMessagePanel:errorMsgPanel]

        controller.connect()

        assertEquals "Errors were not generated", expectedErrors, errorMsgPanel.errors
    }

}
