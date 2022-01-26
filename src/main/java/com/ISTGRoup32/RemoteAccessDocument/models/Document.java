package com.ISTGRoup32.RemoteAccessDocument.models;

import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "documents")
@EqualsAndHashCode
public class Document {

    @Id @Column(name = "id")
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "content", columnDefinition="TEXT")
    private String content;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
