package org.hydracache.testkit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.RandomStringUtils;
import org.hydracache.testkit.model.TestPod;
import org.hydracache.testkit.model.TestPodDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestPodDaoTest extends AbstractSpringTestCase {

    @Autowired
    private TestPodDao testPodDao;

    @Test
    public void ensureCreationIsCorrect() {
        TestPod pod = new TestPod();

        pod.setDescription(RandomStringUtils.randomAlphanumeric(20));

        testPodDao.save(pod);

        assertTrue("Pod id is invalid", pod.getId() > 0);

        TestPod newPod = testPodDao.get(pod.getId());

        assertEquals("TestPod is not created correctly", pod, newPod);
    }

}
