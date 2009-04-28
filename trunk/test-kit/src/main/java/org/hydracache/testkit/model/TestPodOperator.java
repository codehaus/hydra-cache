package org.hydracache.testkit.model;

import java.util.Collection;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class TestPodOperator {

    private HibernateTemplate hibernateTemplate;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Transactional(readOnly = false)
    public void save(TestPod pod) {
        hibernateTemplate.saveOrUpdate(pod);
    }

    public TestPod get(long id) {
        return (TestPod) hibernateTemplate.get(TestPod.class, id);
    }

    @SuppressWarnings("unchecked")
    public Collection<TestPod> findAll() {
        return hibernateTemplate.loadAll(TestPod.class);
    }

    @Transactional(readOnly = false)
    public TestPod activateTestPod(int i) {
        TestPod pod = get(i);

        if (pod == null) {
            pod = createNewTestPod(i);
        }

        pod.setNumberOfActivation(pod.getNumberOfActivation() + 1);
        pod.setEnabled(true);
        save(pod);

        return pod;
    }

    private TestPod createNewTestPod(int i) {
        TestPod pod;
        pod = new TestPod();
        pod.setId(i);
        pod.setDescription("TestPod [" + i + "]");
        pod.setEnabled(false);
        pod.setNumberOfActivation(0);

        save(pod);
        return pod;
    }

}
