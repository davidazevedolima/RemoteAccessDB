package com.ISTGRoup32.RemoteAccessDocument.dao;


import java.io.IOException;

public interface BackUpDao {
    boolean getBackUp() throws IOException, InterruptedException;
}
