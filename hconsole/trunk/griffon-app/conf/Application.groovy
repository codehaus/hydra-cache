application {
    title = 'Hconsole'
    startupGroups = ['Hconsole']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
	PlayGround {
		model="org.hydracache.console.PlayGroundModel"
		controller="org.hydracache.console.PlayGroundController"
		view="org.hydracache.console.PlayGroundView"
	}
	NodeDetailPane {
		model="org.hydracache.console.NodeDetailPaneModel"
		controller="org.hydracache.console.NodeDetailPaneController"
		view="org.hydracache.console.NodeDetailPaneView"
	}
	SpaceDashboard {
		model="org.hydracache.console.SpaceDashboardModel"
		view="org.hydracache.console.SpaceDashboardView"
		controller="org.hydracache.console.SpaceDashboardController"
	}
	AddressBar {
		model="org.hydracache.console.AddressBarModel"
		controller="org.hydracache.console.AddressBarController"
		view="org.hydracache.console.AddressBarView"
	}
	NavigationPane {
		model="org.hydracache.console.NavigationPaneModel"
		controller="org.hydracache.console.NavigationPaneController"
		view="org.hydracache.console.NavigationPaneView"
	}
	Hconsole {
		model="org.hydracache.console.HconsoleModel"
		view="org.hydracache.console.HconsoleView"
		controller="org.hydracache.console.HconsoleController"
	}
}
