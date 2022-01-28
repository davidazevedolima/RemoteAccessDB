package com.ISTGRoup32.RemoteAccessDocument.dao;

import com.ISTGRoup32.RemoteAccessDocument.models.Document;
import com.ISTGRoup32.RemoteAccessDocument.models.User;
import com.ISTGRoup32.RemoteAccessDocument.models.UserDocument;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional
public class DocumentDaoImpl implements DocumentDao{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Document> getDocuments(Long userId) {
        String query = "SELECT doc FROM Document doc " +
                        "INNER JOIN UserDocument ud ON doc.id = ud.documentId " +
                        "WHERE ud.userId = :userId ";

        return entityManager.createQuery(query).setParameter("userId", userId).getResultList();
    }

    @Override
    public String deleteDocument(Long id) {
        Document document = entityManager.find(Document.class, id);
        entityManager.remove(document);
        document = entityManager.find(Document.class, id);
        if(document == null)
            return "SUCCESS";
        else return "FAIL";
    }

    @Override
    public Document getDocument(Long id) {
        String queryString = "FROM Document WHERE id = :id";
        Query query = entityManager.createQuery(queryString).setParameter("id", id);
        List<Document> resultList = query.getResultList();

        return resultList.get(0);
    }

    @Override
    public Document newDocument(Long ownerId) {
        Document newDocument = new Document();
        newDocument.setOwnerId(ownerId);
        newDocument.setTitle("New document");
        newDocument.setContent("");
        newDocument = entityManager.merge(newDocument);

        UserDocument newUserDocument = new UserDocument();
        newUserDocument.setUserId(ownerId);
        newUserDocument.setDocumentId(newDocument.getId());
        newUserDocument.setOwner(true);
        entityManager.merge(newUserDocument);
        return newDocument;
    }

    @Override
    public String shareDocument(Long docId, String username) {

        String queryString = "FROM User WHERE username = :username";
        Query query = entityManager.createQuery(queryString).setParameter("username", username);
        List<User> resultList = query.getResultList();
        if (resultList.isEmpty())
            return "FAIL";

        UserDocument newUserDocument = new UserDocument();
        newUserDocument.setUserId(resultList.get(0).getId());
        newUserDocument.setDocumentId(docId);
        newUserDocument.setOwner(false);

        UserDocument documentShared = entityManager.merge(newUserDocument);
        if(documentShared != null)
            return "SUCCESS";
        else return "FAIL";
    }

    @Override
    public String saveDocument(Long docId, String title, String content) {
        String queryString = "UPDATE documents SET title = :title , content = :content WHERE id = :docId";
        Query query = entityManager.createNativeQuery(queryString)
                .setParameter("title", title)
                .setParameter("content", content)
                .setParameter("docId", docId);
        int documentSaved = query.executeUpdate();
        if(documentSaved == 1)
            return "SUCCESS";
        else return "FAIL";
    }

}
