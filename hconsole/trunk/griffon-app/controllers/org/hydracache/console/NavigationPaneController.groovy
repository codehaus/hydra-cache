package org.hydracache.console

class NavigationPaneController {
    // these will be injected by Griffon
    HydraSpaceService hydraSpaceService
    def model
    def view

    void mvcGroupInit(Map args) {
        // this method is called after model and view are injected
    }

    /*
    def action = { evt = null ->
    }
    */

    def onHydraSpaceConnected = {nodes, storageInfo ->
        log.debug "Event [HydraSpaceConnected] received ..."

        doLater {
            log.debug "Refreshing navigation pane"
            model.updateServerList(nodes)
        }
    }

    def openNodeDetailPane = {evt = null ->
        def nodeId = view.nodeList.getSelectedValue()

        def info = hydraSpaceService.queryServerDetails(nodeId)

        doLater{
            createMVCGroup('NodeDetailPane', 'nodeDetailPane', [tabGroup:app.views['Hconsole'].tabGroup, storageInfo: info, server: nodeId])
        }
    }
}