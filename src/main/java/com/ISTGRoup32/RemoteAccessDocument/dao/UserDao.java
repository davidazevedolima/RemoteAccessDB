package com.ISTGRoup32.RemoteAccessDocument.dao;

import com.ISTGRoup32.RemoteAccessDocument.models.User;
import com.ISTGRoup32.RemoteAccessDocument.models.UserDocument;

import java.util.List;

public interface UserDao {
    List<User> getUsers();

    void deleteUser(Long id);

    void register(User user);

    User verifyCredentials(User user);

    User isUserInDB(Long id);

    UserDocument getUserPermissions(Long id, Long docId);
}
