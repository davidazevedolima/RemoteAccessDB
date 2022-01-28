package com.ISTGRoup32.RemoteAccessDocument.models;

import javax.persistence.*;

@Entity
@Table(name = "user_documents", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "document_id" }))
public class UserDocument {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "document_id")
    private Long documentId;

    @Column(name = "owner")
    private boolean owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }
}
