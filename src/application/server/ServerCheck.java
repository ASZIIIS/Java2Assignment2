package application.server;

import java.net.Socket;

public class ServerCheck {
    private ServerControl control1;
    private ServerControl control2;

    private int[][] chessboard;
    private int cnt;
    private Thread t1;
    private Thread t2;
    private boolean end=false;
    private int game;
    private boolean requeue;
    public ServerCheck(ServerControl sc1,ServerControl sc2,int g){
        this.control1=sc1;
        this.control2=sc2;
        control1.setServer(this,true,g);
        control2.setServer(this,false,g);
        this.game=g;
        this.chessboard=new int[3][3];
        for(int i=0;i<3;++i){
            for(int j=0;j<3;++j) {
                chessboard[i][j] = 0;
            }
        }
        cnt=0;
    }
    public void put(int cmd,int p){
        if(chessboard[cmd/3][cmd%3]==0){
            chessboard[cmd/3][cmd%3]=p;
            control1.update(cmd);
            control2.update(cmd);
            cnt++;
            if(win(cmd/3,cmd%3)){
                if(p==1){
                    control1.win(1);
                    control2.win(2);
                }else{
                    control2.win(1);
                    control1.win(2);
                }
                end();
            }else if(cnt==9){
                control1.win(3);
                control2.win(3);
                end();
            }
        }
    }
    private boolean win(int x,int y){
        boolean flag;
        int flag2;
        int p=chessboard[x][y];
        //line
        flag=true;
        for(int i=1;x-i>=0;++i){
            if(chessboard[x-i][y]!=p){
                flag=false;
                break;
            }
        }
        for(int i=1;x+i<3;++i){
            if(chessboard[x+i][y]!=p){
                flag=false;
                break;
            }
        }
        if(flag){
            return true;
        }
        //column
        flag=true;
        for(int i=1;y-i>=0;++i){
            if(chessboard[x][y-i]!=p){
                flag=false;
                break;
            }
        }
        for(int i=1;y+i<3;++i){
            if(chessboard[x][y+i]!=p){
                flag=false;
                break;
            }
        }
        if(flag){
            return true;
        }
        if(x%2==y%2){
            //diagonal
            flag2=0;
            for(int i=1;x-i>=0&&y-i>=0;++i){
                if(chessboard[x-i][y-i]==p){
                    ++flag2;
                }
            }
            for(int i=1;x+i<3&&y+i<3;++i){
                if(chessboard[x+i][y+i]==p){
                    ++flag2;
                }
            }
            if(flag2==2){
                return true;
            }
            //anti-diagonal
            flag2=0;
            for(int i=1;x-i>=0&&y+i<3;++i){
                if(chessboard[x-i][y+i]==p){
                    ++flag2;
                }
            }
            for(int i=1;x+i<3&&y-i>=0;++i){
                if(chessboard[x+i][y-i]==p){
                    ++flag2;
                }
            }
            if(flag2==2){
                return true;
            }
        }
        return false;
    }

    public void disconnect(int p) {
        if(p==1){
            control2.disconnect();
        }else{
            control1.disconnect();
        }
        end();
    }

    public void end() {
        if(!end){
            Server.Print("Game"+game+":Game Over");
            control1.end();
            control2.end();
            end=true;
        }
    }

    public void requeue() {
        requeue=true;
        Server.requeue();
        end=true;
    }
    public boolean getEnd(){
        return end;
    }
}
