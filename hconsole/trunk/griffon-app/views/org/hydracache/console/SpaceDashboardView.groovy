package org.hydracache.console

tabbedPane(tabGroup, selectedIndex: tabGroup.tabCount) {
    panel(title: messageSource.getMessage('spaceDashboard.title'), id: "tab") {
        borderLayout()
        scrollPane(constraints: CENTER) {
            textArea(text: bind{model.overview})
        }
    }
}