package com.ISTGRoup32.RemoteAccessDocument.dao;

import com.ISTGRoup32.RemoteAccessDocument.models.User;

import java.util.List;

public interface UserDao {
    List<User> getUsers();

    void deleteUser(Long id);

    void register(User user);

    User verifyCredentials(User user);

}
