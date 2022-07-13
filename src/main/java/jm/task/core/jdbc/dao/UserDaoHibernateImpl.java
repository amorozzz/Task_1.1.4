package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    private SessionFactory sessionFactory = Util.getSessionFactory();

    public UserDaoHibernateImpl() {

    }


    @Override
    public void createUsersTable() {
       try (Session session = sessionFactory.openSession()) {
           session.beginTransaction();
           session.createSQLQuery(
                           "CREATE TABLE IF NOT EXISTS users" +
                           "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                           " name VARCHAR(50), " +
                           " lastName VARCHAR (50), " +
                           " age INT)")
                   .addEntity(User.class)
                   .executeUpdate();
       }
    }

    @Override
    public void dropUsersTable() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createSQLQuery("DROP TABLE IF EXISTS users")
                    .addEntity(User.class)
                    .executeUpdate();
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(new User(name,lastName,age));
            System.out.println(String.format("User с именем – %s добавлен в базу данных", name));
        }
    }

    @Override
    public void removeUserById(long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                User user = session.load(User.class,id);
                session.delete(user);
            } catch (EntityNotFoundException ignored){}
        }
    }

    @Override
    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            CriteriaQuery<User> criteriaQuery = session.getCriteriaBuilder().createQuery(User.class);
            criteriaQuery.from(User.class);
            List<User> userList = session.createQuery(criteriaQuery).getResultList();
            session.close();
            return userList;
        }
    }

    @Override
    public void cleanUsersTable() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createSQLQuery("TRUNCATE TABLE users")
                    .addEntity(User.class)
                    .executeUpdate();
        }
    }
}
