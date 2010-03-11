application {
    title='Hconsole'
    startupGroups = ['hconsole']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "org.hydracache.console.NavigationPane"
    'NavigationPane' {
        model = 'org.hydracache.console.NavigationPaneModel'
        controller = 'org.hydracache.console.NavigationPaneController'
        view = 'org.hydracache.console.NavigationPaneView'
    }

    // MVC Group for "hconsole"
    'hconsole' {
        model = 'org.hydracache.console.HconsoleModel'
        view = 'org.hydracache.console.HconsoleView'
        controller = 'org.hydracache.console.HconsoleController'
    }

}
