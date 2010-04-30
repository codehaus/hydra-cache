package org.hydracache.console

import org.apache.commons.io.FileUtils

tabbedPane(tabGroup, selectedIndex: tabGroup.tabCount) {
    panel(id: "tab", title: "$model.server") {
        borderLayout()
        panel(constraints: CENTER) {
            label(text:messageSource.getMessage('nodePane.maxMemory.label'))
            label(text: bind{FileUtils.byteCountToDisplaySize(model.maxMemory)})
            label(text:messageSource.getMessage('nodePane.heapMemory.label'))
            label(text: bind{FileUtils.byteCountToDisplaySize(model.heapMemory)})
            label(text:messageSource.getMessage('nodePane.usedMemory.label'))
            label(text: bind{FileUtils.byteCountToDisplaySize(model.usedMemory)})
            label(text:messageSource.getMessage('nodePane.freeMemory.label'))
            label(text: bind{FileUtils.byteCountToDisplaySize(model.freeMemory)})
        }
    }
}
