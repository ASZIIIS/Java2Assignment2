package application.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server{
    private static int gameCnt;
    public static boolean requeue=false;
    private static ServerControl wait;
    private static ServerControl[] sc;
    public static void main(String args[]) throws IOException{
        ServerSocket server=new ServerSocket(8808);
        Print("Ready for connect...");
        Socket s;
        while (true){
            sc=new ServerControl[2];
            for(int i=0;i<2;++i){
                if(requeue){
                    sc[i]=wait;
                    requeue=false;
                }else{
                    s=server.accept();
                    sc[i]=new ServerControl(s);
                    new Thread(sc[i]).start();
                    Print("Connected from "+s.getInetAddress().getHostAddress());
                }
            }
            if(!requeue){
                new ServerCheck(sc[0],sc[1],gameCnt);
                Print("Game"+gameCnt+":Game Start");
                gameCnt++;
            }else{
                wait=sc[1];
            }
        }
    }
    public static void requeue(){
        requeue=true;
    }
    public static int getGameCnt() {
        return gameCnt;
    }
    public static void Print(String s){
        System.out.println(s);
    }
}
