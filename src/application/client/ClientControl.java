package application.client;

import javafx.application.Platform;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientControl implements Runnable{
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private ClientController client;
    private boolean start;
    public ClientControl(ClientController c){
        try{
            this.socket=new Socket("localhost",8808);
            this.in=new Scanner(socket.getInputStream());
            this.out=new PrintWriter(socket.getOutputStream());
            this.client=c;
            this.start=false;
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        int cmd;
        while(true){
            if(start){
                if(!in.hasNext()){
                    System.out.println("Disconnected...");
                    return;
                }
                cmd=Integer.parseInt(in.next());
                if(cmd>=0&&cmd<9){
                    int finalCmd = cmd;
                    Platform.runLater(()->client.update(finalCmd));
                }else if(cmd==11){
                    System.out.println("You Win");
                    return;
                }else if(cmd==12){
                    System.out.println("You Lose");
                    return;
                }else if(cmd==13){
                    System.out.println("Tied");
                    return;
                }else if(cmd==15){
                    System.out.println("Your Opponent Has Disconnected");
                    return;
                }else if(cmd==16){
                    System.out.println("Game Over");
                    return;
                }
            }else{
                System.out.println("In Queue");
                if(!in.hasNext()){
                    System.out.println("Disconnected...");
                    return;
                }
                cmd=Integer.parseInt(in.next());
                System.out.println("Match Found");
                if(cmd==0){
                    System.out.println("You are player1 (X)");
                    client.setPlayer(true);
                }else if(cmd==1){
                    System.out.println("You are player2 (O)");
                    client.setPlayer(false);
                }
                start=true;
            }
        }
    }
    public void send(int c){
        out.println(c);
        out.flush();
    }
}
