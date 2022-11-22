package application.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerControl implements Runnable {
    private ServerCheck server;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private int player;
    private boolean turn;
    private boolean end = false;
    private int game;
    private boolean inqueue = true;

    public ServerControl(Socket s2) {
        try {
            this.socket = s2;
            this.in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int cmd;
        while (true) {
            if (!in.hasNext()) {
                if (inqueue) {
                    Server.Print("Disconnected from " + socket.getInetAddress().getHostAddress());
                    Server.requeue();
                } else {
                    Server.Print("Game" + game + ":Player" + player + " Disconnect");
                    server.disconnect(player);
                }
                return;
            }
            if (!end) {
                cmd = Integer.parseInt(in.next());
                if (cmd >= 0 && cmd < 9) {
                    Server.Print("Game" + game + ":Player" + player + ":(" + cmd / 3 + ", " + cmd % 3 + ")");
                    server.put(cmd, player);
                }
                if (cmd == 10) {
                    if (inqueue) {
                        Server.Print("Quit from " + socket.getInetAddress().getHostAddress());
                        Server.requeue();
                    } else {
                        Server.Print("Game" + game + ":Player" + player + " Quit");
                        server.disconnect(player);
                    }
                    return;
                }
            } else {
                return;
            }
        }
    }

    public void setServer(ServerCheck sc, boolean t, int g) {
        try {
            this.server = sc;
            this.out = new PrintWriter(socket.getOutputStream());
            if (t) {
                player = 1;
                out.println(0);
                out.flush();
            } else {
                player = 2;
                out.println(1);
                out.flush();
            }
            this.turn = t;
            this.game = g;
            inqueue = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void update(int cmd) {
        turn = !turn;
        out.println(cmd);
        out.flush();
    }

    public void win(int x) {
        if (x == 1) {
            out.println(11);
            out.flush();
        } else if (x == 2) {
            out.println(12);
            out.flush();
        } else if (x == 3) {
            out.println(13);
            out.flush();
        }
    }

    public void disconnect() {
        out.println(15);
        out.flush();
        server.end();
    }

    public void end() {
        out.println(16);
        out.flush();
        end = true;
    }
}
