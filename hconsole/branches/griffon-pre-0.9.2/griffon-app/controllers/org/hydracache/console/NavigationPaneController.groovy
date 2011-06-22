package org.hydracache.console

class NavigationPaneController {
    // these will be injected by Griffon
    HydraSpaceService hydraSpaceService
    def model
    def view

    void mvcGroupInit(Map args) {
        // this method is called after model and view are injected
    }

    def onHydraSpaceConnected = {nodes, storageInfo ->
        log.debug "Event [HydraSpaceConnected] received ..."

        doLater {
            log.debug "Setting up navigation pane"
            model.updateServerList(nodes)
        }

        log.debug "Event [HydraSpaceConnected] processed"
    }

    def onHydraSpaceUpdated = {nodes, storageInfo ->
        log.debug "Event [HydraSpaceUpdated] received ..."

        doLater {
            log.debug "Refreshing navigation pane"
            model.updateServerList(nodes)
        }

        log.debug "Event [HydraSpaceUpdated] processed"
    }

    def openNodeDetailPane = {evt = null ->
        def nodeId = view.nodeList.getSelectedValue()

        def info = hydraSpaceService.queryServerDetails(nodeId)

        def mvcId = "$nodeId"
        def nodeDetailView = app.views[mvcId]
        def tabbedPane = app.views['Hconsole'].tabGroup

        if (nodeDetailView) {
            tabbedPane.selectedComponent = nodeDetailView.tab
        } else {
            doLater {
                createMVCGroup('NodeDetailPane', mvcId, [tabGroup: tabbedPane, storageInfo: info, server: nodeId])
            }
        }
    }

    def onHydraSpaceDisConnected = {
        log.debug "Event [HydraSpaceDisConnected] received ..."

        doLater {
            model.updateServerList([])
        }

        log.debug "Event [HydraSpaceDisConnected] processed"
    }
}