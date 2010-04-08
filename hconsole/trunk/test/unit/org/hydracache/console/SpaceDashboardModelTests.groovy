package org.hydracache.console

/**
 * Created by nick.zhu
 */
class SpaceDashboardModelTests extends GriffonTestCase {

    public void testTotalNodesCalculation() {
        def model = new SpaceDashboardModel()

        model.serverNodes = [1, 2, 3]

        model.updateOverview()

        assertEquals 3, model.numberOfNodes
    }

    public void testMaxMemoryMBCalculation() {
        def model = new SpaceDashboardModel()

        model.serverNodes = [1, 2, 3]
        model.storageInfo = ['maxMemory': 259522560, 'N': 2]

        model.updateOverview()

        assertEquals '371 MB', model.totalMemory
    }

}
