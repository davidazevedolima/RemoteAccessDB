package com.ISTGRoup32.RemoteAccessDocument;

public class BackUp extends Thread{

    private Communication channel;
    public BackUp(Communication communication){
        channel = communication;
    }
    @Override
    public void run(){
        while(true){
            System.out.print("bola\n");
            try {
                Thread.sleep(5*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
