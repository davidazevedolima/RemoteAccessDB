package com.ISTGRoup32.RemoteAccessDocument.dao;

import com.ISTGRoup32.RemoteAccessDocument.models.Document;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@Transactional
public class BackUpDaoImpl implements BackUpDao{
    private EntityManager entityManager;
    public boolean getBackUp(){

        String query = "pg_dump projtest -U g32 > /tmp/db.sql";
        return true;
    }
}
