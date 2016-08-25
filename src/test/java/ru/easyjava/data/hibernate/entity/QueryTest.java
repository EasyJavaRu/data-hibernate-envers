package ru.easyjava.data.hibernate.entity;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.Closeable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class QueryTest {
    private SessionFactory sessionFactory;

    @Before
    public void setUp() throws Exception {
        Passport p = new Passport();
        p.setSeries("AS");
        p.setNo("123456");
        p.setIssueDate(LocalDate.now());
        p.setValidity(Period.ofYears(20));

        Address a = new Address();
        a.setCity("Kickapoo");
        a.setStreet("Main street");
        a.setBuilding("1");

        Person person = new Person();
        person.setFirstName("Test");
        person.setLastName("Testoff");
        person.setDob(LocalDate.now());
        person.setPrimaryAddress(a);
        person.setPassport(p);

        Company c = new Company();
        c.setName("Acme Ltd");

        p.setOwner(person);
        person.setWorkingPlaces(Collections.singletonList(c));

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();

        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw e;
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.merge(person);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testGreeter() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        // Check creation
        Object[] result = (Object[]) AuditReaderFactory
                .get(session)
                .createQuery()
                .forRevisionsOfEntity(Person.class, false, true)
                .getSingleResult();
        System.out.println(result[0]);
        System.out.println(result[1]);
        System.out.println(result[2]);

        session.getTransaction().commit();
        session.close();

        // Set new name
        session = sessionFactory.openSession();
        session.beginTransaction();

        Person p = (Person) session
                .createCriteria(Person.class)
                .add(Restrictions.eq("firstName", "Test"))
                .uniqueResult();
        p.setFirstName("Johan");
        p.setLastName("von Testow");

        session.save(p);

        session.getTransaction().commit();
        session.close();

        // Find changes
        session = sessionFactory.openSession();
        session.beginTransaction();

        AuditReaderFactory
                .get(session)
                .createQuery()
                .forRevisionsOfEntity(Person.class, false, true)
                .getResultList()
                .forEach(r -> {
                    Object[] v = (Object[])r;
                    System.out.println(v[0]);
                    System.out.println(v[1]);
                    System.out.println(v[2]);
                });

        session.getTransaction().commit();
        session.close();

        //Create new person, modify and delete it
        session = sessionFactory.openSession();
        session.beginTransaction();

        Address address = p.getPrimaryAddress();
        session.detach(address);
        address.setId(null);

        Passport passport = new Passport();
        passport.setSeries("TT");
        passport.setNo("250816");
        passport.setIssueDate(LocalDate.now());
        passport.setValidity(Period.ofYears(20));

        Person person = new Person();
        person.setFirstName("Brunhild");
        person.setLastName("Testonkowski");
        person.setDob(LocalDate.now());
        person.setPrimaryAddress(p.getPrimaryAddress());
        person.setPassport(passport);
        passport.setOwner(person);

        session.save(person);

        session.getTransaction().commit();
        session.close();

        session = sessionFactory.openSession();
        session.beginTransaction();

        person.setLastName("von Testow");
        person.getPassport().setSeries("VT");
        person.getPassport().setNo("101731");

        session.merge(person);

        session.getTransaction().commit();
        session.close();

        session = sessionFactory.openSession();
        session.beginTransaction();

        session.delete(person);
        session.delete(passport);

        session.getTransaction().commit();
        session.close();

        //Check changes
        session = sessionFactory.openSession();
        session.beginTransaction();

        AuditReaderFactory
                .get(session)
                .createQuery()
                .forRevisionsOfEntity(Person.class, false, true)
                .add(AuditEntity.property("firstName").eq("Brunhild"))
                .getResultList()
                .forEach(r -> {
                    Object[] v = (Object[])r;
                    System.out.println(v[0]);
                    System.out.println(v[1]);
                    System.out.println(v[2]);
                });

        AuditReaderFactory
                .get(session)
                .createQuery()
                .forRevisionsOfEntity(Passport.class, false, true)
                .getResultList()
                .forEach(r -> {
                    Object[] v = (Object[])r;
                    System.out.println(v[0]);
                    System.out.println(v[1]);
                    System.out.println(v[2]);
                });

        session.getTransaction().commit();
        session.close();

        //Find specific revision
        session = sessionFactory.openSession();
        session.beginTransaction();

        System.out.println(
                session
                        .createCriteria(Person.class)
                        .add(Restrictions.eq("firstName", "Johan"))
                        .uniqueResult()
        );

        System.out.println(
                AuditReaderFactory
                        .get(session)
                        .createQuery()
                        .forEntitiesAtRevision(Person.class, 5)
                        .getSingleResult()
        );

        session.getTransaction().commit();
        session.close();
    }
}
