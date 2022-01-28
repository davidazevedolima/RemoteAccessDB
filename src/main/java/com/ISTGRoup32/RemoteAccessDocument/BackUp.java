package com.ISTGRoup32.RemoteAccessDocument;

import com.ISTGRoup32.RemoteAccessDocument.dao.BackUpDao;
import com.ISTGRoup32.RemoteAccessDocument.dao.DocumentDao;
import com.ISTGRoup32.RemoteAccessDocument.models.Document;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class BackUp extends Thread{

    private Communication channel;
    BackUpDao backUp;
    public BackUp(Communication communication){
        channel = communication;
        backUp = SpringUtils.getBean(BackUpDao.class);
    }
    @Override
    public void run(){
        while(true){
            if(backUp.getBackUp()){

                try {
//                    Path src = Paths.get("/tmp/db.sql");
//                    Path dest = Paths.get("/run/user/1000/gvfs/dav:host=10.0.2.15,ssl=false,prefix=%2Fremote.php%2Fdav/files/g32/db.sql");
//                    Files.copy(src.toFile(),dest.toFile());
//                    Files.copy( src.toFile() , dest.toFile());

                    InputStream is = null;
                    OutputStream os = null;
                    File myObj = new File("/run/user/1000/gvfs/dav:host=10.0.2.15,ssl=false,user=g32,prefix=%2Fremote.php%2Fwebdav/db.sql");
                    myObj.createNewFile();
                    try {
                        is = new FileInputStream("/tmp/db.sql");
                        os = new FileOutputStream("/run/user/1000/gvfs/dav:host=10.0.2.15,ssl=false,user=g32,prefix=%2Fremote.php%2Fwebdav/db.sql"); // buffer size 1K
                        byte[] buf = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = is.read(buf)) > 0)
                        {
                            os.write(buf, 0, bytesRead);
                        }
                    } finally { is.close(); os.close(); }
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(5 * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
