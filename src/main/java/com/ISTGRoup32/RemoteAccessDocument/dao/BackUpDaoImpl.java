package com.ISTGRoup32.RemoteAccessDocument.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.List;

@Repository
@Transactional
public class BackUpDaoImpl implements BackUpDao{
    @PersistenceContext
    private EntityManager entityManager;
    public boolean getBackUp() throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder(
                "/usr/lib/postgresql/14/bin/pg_dump",
                "--host", "localhost",
                "--username", "postgres",
                "--dbname", "projtest",
                "-f","/tmp/db.sql");
        pb.redirectErrorStream(true);
        Process p = pb.start();
        p.waitFor();
        return true;
    }
}
