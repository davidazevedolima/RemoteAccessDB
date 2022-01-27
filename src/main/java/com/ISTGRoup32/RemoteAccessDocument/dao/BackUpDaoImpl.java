package com.ISTGRoup32.RemoteAccessDocument.dao;

import com.ISTGRoup32.RemoteAccessDocument.models.Document;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class BackUpDaoImpl implements BackUpDao{
    public Document getBackUp(){
        return new Document();
    }
}
