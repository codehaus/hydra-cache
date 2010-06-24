package org.hydracache.console

import static javax.swing.JSplitPane.*
import java.awt.Dimension

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
    panel(id: 'addressBar', constraints: NORTH)

    splitPane(id: 'splitPane', resizeWeight: 0.70f,
            oneTouchExpandable: true, constraints: CENTER,
            orientation: VERTICAL_SPLIT) {
        splitPane(id: 'splitPaneInner', resizeWeight: 0.45f, dividerLocation: 200,
                oneTouchExpandable: true,
                orientation: HORIZONTAL_SPLIT) {
            scrollPane(id: 'navigationPane')
            tabbedPane(id: "tabGroup")
        }
        panel(id: 'playgroundPane')
    }


}
