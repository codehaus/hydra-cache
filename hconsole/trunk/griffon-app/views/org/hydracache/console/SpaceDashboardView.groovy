package org.hydracache.console

import org.apache.commons.io.FileUtils

tabbedPane(tabGroup, selectedIndex: tabGroup.tabCount) {
    panel(title: messageSource.getMessage('spaceDashboard.title'), id: "spaceDashboardTab") {
        borderLayout()
        panel(constraints: CENTER) {
            gridLayout(columns: 2, rows: 3)
            label(text:messageSource.getMessage('spaceDashboard.numberOfNodes.label'))
            label(text: bind{model.numberOfNodes})
            label(text:messageSource.getMessage('spaceDashboard.maxMemory.label'))
            label(text: bind{FileUtils.byteCountToDisplaySize(model.totalMemory)})
            label(text:messageSource.getMessage('spaceDashboard.usedMemory.label'))
            label(text: bind{FileUtils.byteCountToDisplaySize(model.usedMemory)})
        }
    }
}