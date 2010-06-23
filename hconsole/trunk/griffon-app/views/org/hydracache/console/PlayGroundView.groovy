package org.hydracache.console

import net.sourceforge.gvalidation.swing.ErrorMessagePanel

actions {
    action(id: "putAction",
            name: messageSource.getMessage('playGround.put.label'),
            closure: controller.put)
    action(id: "getAction",
            name: messageSource.getMessage('playGround.get.label'),
            closure: controller.get)
}

panel(playgroundPane, border: titledBorder(messageSource.getMessage('playGround.title'))) {
    borderLayout()
    
    container(new ErrorMessagePanel(messageSource),
                        id: 'errorMessagePanel', constraints: NORTH,
                        errors: bind(source: model, 'errors'))

    panel(constraints: CENTER) {
        gridLayout(columns: 2, rows: 1, hgap: 5)
        panel(border: titledBorder(title: messageSource.getMessage('playGround.put.label'))) {
            gridLayout(columns: 2, rows: 4)
            label(messageSource.getMessage('playGround.storageContext.label') + "*")
            textField(text: bind(target: model, 'storageContextToPut'))
            label(messageSource.getMessage('playGround.storageKey.label') + "*")
            textField(text: bind(target: model, 'storageKeyToPut'))
            label(messageSource.getMessage('playGround.storageValue.label') + "*")
            textField(text: bind(target: model, 'storageValueToPut'))
            button(putAction)
        }
        panel(border: titledBorder(title: messageSource.getMessage('playGround.get.label'))) {
            gridLayout(columns: 2, rows: 4)
            label(messageSource.getMessage('playGround.storageContext.label') + "*")
            textField(text: bind(target: model, 'storageContextToGet'))
            label(messageSource.getMessage('playGround.storageKey.label') + "*")
            textField(text: bind(target: model, 'storageKeyToGet'))
            label(messageSource.getMessage('playGround.storageValue.label'))
            textField(text: bind(source: model, 'retrievedStorageValue'), editable: false)
            button(getAction)
        }
    }
}
