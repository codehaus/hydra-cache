package org.hydracache.testkit.model;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TestPodDao {

    private HibernateTemplate hibernateTemplate;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public void save(TestPod pod) {
        hibernateTemplate.saveOrUpdate(pod);
    }

    public TestPod get(long id) {
        return (TestPod) hibernateTemplate.get(TestPod.class, id);
    }

}
