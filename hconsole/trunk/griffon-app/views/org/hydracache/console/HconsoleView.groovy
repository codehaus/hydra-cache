package org.hydracache.console

import static javax.swing.JSplitPane.HORIZONTAL_SPLIT

actions {
    action(id: "quitAction",
            name: messageSource.getMessage('menuItem.exit.caption'),
            mnemonic: "x",
            closure: controller.quit)
}

mainWindow = application(title: 'hconsole',
        size: [720, 550],
        pack: false,
        locationByPlatform: true,
        iconImage: imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                imageIcon('/griffon-icon-32x32.png').image,
                imageIcon('/griffon-icon-16x16.png').image]
) {
    menuBar {
        menu(messageSource.getMessage('menu.file.caption')) {
            menuItem(quitAction)
        }
    }
    borderLayout()
    panel(id:'addressBar', constraints: NORTH)
    splitPane(id: 'splitPane', resizeWeight: 0.45f, constraints: CENTER,
            orientation: HORIZONTAL_SPLIT) {
         scrollPane(id:'navigationPane')
         scrollPane(id:'contentPane')
    }
}
