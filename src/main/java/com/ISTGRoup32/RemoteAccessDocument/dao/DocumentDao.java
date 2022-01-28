package com.ISTGRoup32.RemoteAccessDocument.dao;

import com.ISTGRoup32.RemoteAccessDocument.models.Document;

import java.util.List;

public interface DocumentDao {
    List<Document> getDocuments(Long userId);

    void deleteDocument(Long id);

    Document getDocument(Long id);

    Document newDocument(Long ownerId);

    void shareDocument(Long docId, String username);

    void saveDocument(Long docId, String title, String content);
}
