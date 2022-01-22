package com.ISTGRoup32.RemoteAccessDocument.dao;

import com.ISTGRoup32.RemoteAccessDocument.models.Document;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class DocumentDaoImpl implements DocumentDao{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Document> getDocuments() {
        String query = "FROM Document";
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public void deleteDocument(Long id) {
        Document document = entityManager.find(Document.class, id);
        entityManager.remove(document);
    }

}
