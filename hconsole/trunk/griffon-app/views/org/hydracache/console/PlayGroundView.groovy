package org.hydracache.console

panel(playgroundPane, border: titledBorder(messageSource.getMessage('playGround.title'))) {
    gridLayout(columns: 2, rows: 1, hgap: 5)
    panel(border: titledBorder(title: messageSource.getMessage('playGround.put.label'))) {
        gridLayout(columns: 2, rows: 4)
        label(messageSource.getMessage('playGround.storageContext.label'))
        textField()
        label(messageSource.getMessage('playGround.storageKey.label'))
        textField()
        label(messageSource.getMessage('playGround.storageValue.label'))
        textField()
        button(messageSource.getMessage('playGround.put.label'))
    }
    panel(border: titledBorder(title: messageSource.getMessage('playGround.get.label'))) {
        gridLayout(columns: 2, rows: 4)
        label(messageSource.getMessage('playGround.storageContext.label'))
        textField()
        label(messageSource.getMessage('playGround.storageKey.label'))
        textField()
        label(messageSource.getMessage('playGround.storageValue.label'))
        textField(editable: false)
        button(messageSource.getMessage('playGround.get.label'))
    }
}
