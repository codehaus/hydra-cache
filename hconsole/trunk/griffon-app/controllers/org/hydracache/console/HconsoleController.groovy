package org.hydracache.console
class HconsoleController {
    def model
    def view

    void mvcGroupInit(Map args) {
        createMVCGroup("NavigationPane", [navigationPane: view.navigationPane])
        createMVCGroup("AddressBar", [addressBar: view.addressBar])
    }

    def quit = {evt = null ->
        app.shutdown()
    }

}