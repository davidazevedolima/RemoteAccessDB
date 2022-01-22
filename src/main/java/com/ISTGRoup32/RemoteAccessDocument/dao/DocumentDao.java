package com.ISTGRoup32.RemoteAccessDocument.dao;

import com.ISTGRoup32.RemoteAccessDocument.models.Document;
import com.ISTGRoup32.RemoteAccessDocument.models.User;

import java.util.List;

public interface DocumentDao {
    List<Document> getDocuments();

    void deleteDocument(Long id);

}
