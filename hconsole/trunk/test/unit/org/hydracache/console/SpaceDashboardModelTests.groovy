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

    public void testMaxMemoryCalculation() {
        def model = new SpaceDashboardModel()

        model.serverNodes = [1, 2, 3]
        model.storageInfo = ['maxMemory': '259522560', 'totalMemory': '16252928', 'freeMemory':'13761624','N': '2']

        model.updateOverview()

        assertEquals '371 MB', model.totalMemory
    }

    public void testUsedMemoryCalculation() {
        def model = new SpaceDashboardModel()

        model.serverNodes = [1, 2, 3]
        model.storageInfo = ['maxMemory': '259522560', 'totalMemory': '16252928', 'freeMemory':'13761624','N': '2']

        model.updateOverview()

        assertEquals '1 MB', model.usedMemory
    }

}
