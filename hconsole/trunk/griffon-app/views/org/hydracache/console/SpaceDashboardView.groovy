package org.hydracache.console

import org.apache.commons.io.FileUtils
import org.hydracache.console.chart.*

tabbedPane(tabGroup) {
    panel(title: messageSource.getMessage('spaceDashboard.title'), id: "spaceDashboardTab") {
        borderLayout()
        panel(id: 'mainPanel', constraints: CENTER) {
            borderLayout()

//            widget(id: 'chart', constraints: CENTER)

            panel(constraints: SOUTH) {
                migLayout()
                label(text: messageSource.getMessage('spaceDashboard.numberOfNodes.label'), constraints: 'left')
                label(text: bind {model.numberOfNodes}, constraints: 'growx, wrap')

                label(text: messageSource.getMessage('spaceDashboard.maxMemory.label'), constraints: 'left')
                label(text: bind {FileUtils.byteCountToDisplaySize(model.totalMemory)}, constraints: 'growx, wrap')

                label(text: messageSource.getMessage('spaceDashboard.freeMemory.label'), constraints: 'left')
                label(text: bind {FileUtils.byteCountToDisplaySize(model.freeMemory)}, constraints: 'growx, wrap')

                label(text: messageSource.getMessage('spaceDashboard.usedMemory.label'), constraints: 'left')
                label(text: bind {FileUtils.byteCountToDisplaySize(model.usedMemory)}, constraints: 'growx, wrap')
            }
        }
    }
}