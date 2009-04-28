package org.hydracache.testkit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.apache.commons.lang.RandomStringUtils;
import org.hydracache.testkit.model.TestPod;
import org.hydracache.testkit.model.TestPodOperator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestPodOperatorTest extends AbstractSpringTestCase {

    @Autowired
    private TestPodOperator testPodDao;

    @Test
    public void ensureCreationIsCorrect() {
        TestPod pod = createRandomPod();

        assertTrue("Pod id is invalid", pod.getId() > 0);

        TestPod newPod = testPodDao.get(pod.getId());

        assertEquals("TestPod is not created correctly", pod, newPod);
    }

    private TestPod createRandomPod() {
        TestPod pod = new TestPod();

        pod.setDescription(RandomStringUtils.randomAlphanumeric(20));

        testPodDao.save(pod);
        return pod;
    }

    @Test
    public void ensureFindAllIsCorrect() {
        TestPod pod = createRandomPod();
        createRandomPod();
        createRandomPod();

        Collection<TestPod> allPods = testPodDao.findAll();

        assertTrue("Pod list size is incorrect", allPods.size() > 3);
        assertTrue("Pod list does not contain the pod", allPods.contains(pod));
    }

}
