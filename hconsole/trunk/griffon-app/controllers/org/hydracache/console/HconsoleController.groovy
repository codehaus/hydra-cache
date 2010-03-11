package org.hydracache.console
class HconsoleController {
    def model
    def view

    void mvcGroupInit(Map args) {
        createMVCGroup("NavigationPane", [navigationPane: view.navigationPane])
    }

    def quit = {evt = null ->
        app.shutdown()
    }

}