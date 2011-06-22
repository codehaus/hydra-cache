package org.hydracache.console

import org.hydracache.console.util.ClosureTimerTask
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper

class NodeDetailPaneController {
    static final long UPDATER_INIT_DELAY = 5000
    static final long UPDATER_INTERVAL = 10000

    // these will be injected by Griffon
    def model
    def view

    def hydraSpaceService

    def nodeUpdater = new Timer()

    void mvcGroupInit(Map args) {
        model.storageInfo = args.storageInfo
        model.server = args.server

        /* TODO: sometimes log instance is not yet injected might be due to
         some threading or timing issue in Griffon, further investigation
         required */
        if(metaClass.hasMetaProperty('log'))
            log.debug "Creating node detail pane for ${model.server}"

        nodeUpdater.schedule(new ClosureTimerTask(closure: {
            log.debug "Updating node[${model.server}] detail..."

            try {
                def info = hydraSpaceService.queryServerDetails(model.server)
                model.storageInfo = info
                updateNodeDetail()
                log.debug "Node[${model.server}] detail updated"
            } catch (Exception ex) {
                log.debug "Failed to contact node[${model.server}] ...", ex
                destroyMvcGroup()
            }
        }), UPDATER_INIT_DELAY, UPDATER_INTERVAL)

        updateNodeDetail()
    }

    def updateNodeDetail() {
        doLater {
            model.updateDetails()
        }
    }

    def onHydraSpaceDisConnected = {
        log.debug "Event [HydraSpaceDisConnected] received ..."

        destroyMvcGroup()

        log.debug "Event [HydraSpaceDisConnected] processed"
    }

    private def destroyMvcGroup() {
        log.debug "Destroying MVC group [${model.server}]"

        nodeUpdater.cancel()

        doLater {
            final def tab = view.tab

            tab.parent.remove(tab)

            GriffonApplicationHelper.destroyMVCGroup(app, "${model.server}")

            log.debug "MVC group [${model.server}] destroyed"
        }
    }
}