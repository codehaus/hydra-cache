package org.hydracache.console

scrollPane(navigationPane) {
    list(id: 'nodeList', model: model.serverListModel,
            mouseClicked: {evt -> if (evt.clickCount >= 2) controller.openNodeDetailPane(evt)})
} 
