package org.hydracache.console

/**
 * Created by nick.zhu
 */
class SpaceDashboardModelTests extends GriffonTestCase {

    public void testTotalNodesCalculation() {
        def model = new SpaceDashboardModel()

        mockLogging(model)

        model.serverNodes = [1, 2, 3]

        model.updateOverview()

        assertEquals 3, model.numberOfNodes
    }

    public void testMemoryCalculation() {
        def model = new SpaceDashboardModel()

        mockLogging(model)

        model.serverNodes = [1, 2, 3]
        model.storageInfo = ['maxMemory': '259522560', 'totalMemory': '16252928', 'freeMemory':'13761624','N': '2']

        model.updateOverview()

        assertEquals 389283840, model.totalMemory
        assertEquals 1245652, model.usedMemory
        assertEquals 389283840 - 1245652, model.freeMemory
        assertEquals 0.0031998553f, model.usedMemoryPercentage, 0f
        assertEquals 0.9968001f, model.freeMemoryPercentage, 0f
    }

}
