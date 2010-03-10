package org.hydracache.testkit;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testkit-context.xml", "/test-context.xml" })
public class AbstractSpringTestCase {

    public AbstractSpringTestCase() {
        super();
    }

}