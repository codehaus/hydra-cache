package org.hydracache.console

tabbedPane(tabGroup, selectedIndex: tabGroup.tabCount) {
    panel(title: messageSource.getMessage('spaceDashboard.title'), id: "tab") {
        borderLayout()
        panel(constraints: CENTER) {
            label(text:messageSource.getMessage('spaceDashboard.numberOfNodes.label'))
            label(text: bind{model.numberOfNodes})
            label(text:messageSource.getMessage('spaceDashboard.maxMemory.label'))
            label(text: bind{model.totalMemory})
            label(text:messageSource.getMessage('spaceDashboard.usedMemory.label'))
            label(text: bind{model.usedMemory})
        }
    }
}