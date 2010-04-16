package org.hydracache.console

tabbedPane(tabGroup, selectedIndex: tabGroup.tabCount) {
    panel(title: messageSource.getMessage('spaceDashboard.title'), id: "tab") {
        borderLayout()
        panel(constraints: CENTER) {
            label(text:messageSource.getMessage('nodePane.maxMemory.label'))
            label(text: bind{model.totalMemory})
            label(text:messageSource.getMessage('nodePane.usedMemory.label'))
            label(text: bind{model.usedMemory})
        }
    }
}
