package org.hydracache.console

scrollPane(navigationPane) {
    list(id:'nodeList', model: model.serverListModel)
}

nodeList.mousePressed = controller.openNodeDetailPane
