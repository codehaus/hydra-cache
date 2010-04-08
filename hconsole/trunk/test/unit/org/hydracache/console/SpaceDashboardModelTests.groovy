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
        model.storageInfo = ['maxMemory': '247 MB']

        model.updateOverview()

        assertEquals 247 * 3, model.totalMemory
    }

}
