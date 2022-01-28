package com.ISTGRoup32.RemoteAccessDocument.dao;

import com.ISTGRoup32.RemoteAccessDocument.models.Document;

import java.util.List;

public interface DocumentDao {
    List<Document> getDocuments(Long userId);

    String deleteDocument(Long id);

    Document getDocument(Long id);

    Document newDocument(Long ownerId);

    String shareDocument(Long docId, String username);

    String saveDocument(Long docId, String title, String content);
}
