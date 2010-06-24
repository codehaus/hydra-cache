package org.hydracache.console

import net.sourceforge.gvalidation.swing.ErrorMessagePanel
import java.awt.GridBagConstraints

def labelGbc = gbc(weightx: 1)
def inputGbc = gbc(weightx: 5,
        fill: GridBagConstraints.HORIZONTAL,
        gridwidth: GridBagConstraints.REMAINDER)
def textAreaGbc = gbc(weightx: 5,
                        fill: GridBagConstraints.BOTH,
                        gridwidth: GridBagConstraints.REMAINDER,
                        weighty: 3)
def buttonGbc = gbc(weightx: 1, gridx: 0, weighty: 1)
def fileChooserFieldGbc = gbc(weightx: 6,
                                    gridwidth: GridBagConstraints.RELATIVE,
                                    fill: GridBagConstraints.HORIZONTAL)
def fileChooserBtnGbc = gbc(weightx: 1, gridwidth: GridBagConstraints.REMAINDER)

actions {
    action(id: "putAction",
            name: messageSource.getMessage('playGround.put.label'),
            closure: controller.put)
    action(id: "getAction",
            name: messageSource.getMessage('playGround.get.label'),
            closure: controller.get)
    action(id: 'selectSourceFileAction',
            name: '...',
            closure: controller.selectSourceFile)
    action(id: 'selectTargetFileAction',
            name: '...',
            closure: controller.selectTargetFile)
    action(id: "putFileAction",
            name: messageSource.getMessage('playGround.putFile.label'),
            closure: controller.putFile)
    action(id: "getFileAction",
            name: messageSource.getMessage('playGround.getFile.label'),
            closure: controller.getFile)
}

panel(playgroundPane, border: titledBorder(messageSource.getMessage('playGround.title'))) {
    borderLayout()

    container(new ErrorMessagePanel(messageSource),
            id: 'errorMessagePanel', constraints: NORTH,
            errors: bind(source: model, 'errors'))

    tabbedPane(constraints: CENTER) {
        panel(title: messageSource.getMessage('playGround.textTab.title')) {
            gridLayout(columns: 2, rows: 1, hgap: 5)
            panel(border: titledBorder(title: messageSource.getMessage('playGround.put.label'))) {
                gridBagLayout()
                gbc(fill: GridBagConstraints.BOTH)

                label(messageSource.getMessage('playGround.storageContext.label') + "*",
                        constraints: labelGbc)
                textField(text: bind(target: model, 'storageContextToPut'),
                        constraints: inputGbc)

                label(messageSource.getMessage('playGround.storageKey.label') + "*",
                        constraints: labelGbc)
                textField(text: bind(target: model, 'storageKeyToPut'),
                        constraints: inputGbc)

                label(messageSource.getMessage('playGround.storageValue.label') + "*",
                        constraints: labelGbc)
                scrollPane(constraints: textAreaGbc) {
                    textArea(text: bind(target: model, 'storageValueToPut'))
                }

                button(putAction,
                        constraints: buttonGbc)
            }
            panel(border: titledBorder(title: messageSource.getMessage('playGround.get.label'))) {
                gridBagLayout()
                gbc(fill: GridBagConstraints.BOTH)

                label(messageSource.getMessage('playGround.storageContext.label') + "*",
                        constraints: labelGbc)
                textField(text: bind(target: model, 'storageContextToGet'),
                        constraints: inputGbc)

                label(messageSource.getMessage('playGround.storageKey.label') + "*",
                        constraints: labelGbc)
                textField(text: bind(target: model, 'storageKeyToGet'),
                        constraints: inputGbc)

                label(messageSource.getMessage('playGround.storageValue.label'),
                        constraints: labelGbc)
                scrollPane(constraints: textAreaGbc) {
                    textArea(text: bind(source: model, 'retrievedStorageValue'), editable: false)
                }

                button(getAction,
                        constraints: buttonGbc)
            }
        }

        panel(title: messageSource.getMessage('playGround.binaryTab.title')) {
            gridLayout(columns: 2, rows: 1, hgap: 5)
            panel(border: titledBorder(title: messageSource.getMessage('playGround.put.label'))) {
                gridBagLayout()
                gbc(fill: GridBagConstraints.BOTH)

                label(messageSource.getMessage('playGround.storageContext.label') + "*",
                        constraints: labelGbc)
                textField(text: bind(target: model, 'storageContextToPut'),
                        constraints: inputGbc)

                label(messageSource.getMessage('playGround.storageKey.label') + "*",
                        constraints: labelGbc)
                textField(text: bind(target: model, 'storageKeyToPut'),
                        constraints: inputGbc)

                label(messageSource.getMessage('playGround.fileToPut.label') + "*",
                        constraints: labelGbc)
                panel(constraints: inputGbc) {
                    gridBagLayout()
                    textField(text: bind(source: model, 'sourceFile'),
                            constraints: fileChooserFieldGbc)
                    button(selectSourceFileAction,
                            constraints: fileChooserBtnGbc)
                }

                button(putFileAction,
                        constraints: buttonGbc)
            }
            panel(border: titledBorder(title: messageSource.getMessage('playGround.get.label'))) {
                gridBagLayout()
                gbc(fill: GridBagConstraints.BOTH)

                label(messageSource.getMessage('playGround.storageContext.label') + "*",
                        constraints: labelGbc)
                textField(text: bind(target: model, 'storageContextToGet'),
                        constraints: inputGbc)

                label(messageSource.getMessage('playGround.storageKey.label') + "*",
                        constraints: labelGbc)
                textField(text: bind(target: model, 'storageKeyToGet'),
                        constraints: inputGbc)

                label(messageSource.getMessage('playGround.retrievedFile.label') + "*",
                        constraints: labelGbc)
                panel(constraints: inputGbc) {
                    gridBagLayout()
                    textField(text: bind(source: model, 'targetFile'),
                            constraints: fileChooserFieldGbc)
                    button(selectTargetFileAction,
                            constraints: fileChooserBtnGbc)
                }

                button(getFileAction,
                        constraints: buttonGbc)
            }
        }
    }
}
