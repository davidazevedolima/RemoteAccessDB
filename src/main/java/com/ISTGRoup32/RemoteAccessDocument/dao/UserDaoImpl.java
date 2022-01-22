package com.ISTGRoup32.RemoteAccessDocument.dao;

import com.ISTGRoup32.RemoteAccessDocument.models.User;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional
public class UserDaoImpl implements UserDao{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> getUsers() {
        String queryString = "FROM User";
        return entityManager.createQuery(queryString).getResultList();
    }

    @Override
    public void deleteUser(Long id) {
        User user = entityManager.find(User.class, id);
        entityManager.remove(user);
    }

    @Override
    public void register(User user) {
        entityManager.merge(user);
    }

    @Override
    public User verifyCredentials(User user) {
        String queryString = "FROM User WHERE username = :username";
        Query query = entityManager.createQuery(queryString).setParameter("username", user.getUsername());
        List<User> resultList = query.getResultList();

        //If there is not a user with "username" and "password" in the database return false
        if (resultList.isEmpty()) return null;

        String hashedPassword = resultList.get(0).getPassword();
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

        if(argon2.verify(hashedPassword, user.getPassword().toCharArray()))
            return resultList.get(0);
        else return null;
    }
}
