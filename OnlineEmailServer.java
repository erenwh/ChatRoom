import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import cs180.net.Socket;
import cs180.net.ServerSocket;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.RunnableFuture;

/**
 * Created by Synnek on 2016/4/14.
 */
public class OnlineEmailServer extends EmailServer implements Runnable {
    String filename;
    int port;
    Socket socket;
    ServerSocket serverSocket;


    public OnlineEmailServer(String filename, int port) throws IOException {
        this.filename = filename;
        this.port = port;
        socket.setReuseAddress(true);
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket client = this.serverSocket.accept();
                socket.setSoTimeout(60000);
                processClient(client);
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // process the client connection with processClient()
        }
    }

    public void processClient(Socket client) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter pw = new PrintWriter(client.getOutputStream());
        while (!client.isClosed()) {
            String request = br.readLine();
            if (request == null) {
                break;
            }
            String[] req = request.split("\t");
            switch (req[0]) {
                case "ADD-USER": {
                    pw.printf(addUser(req));
                    pw.flush();
                }
                case "DELETE-USER": {
                    pw.printf(deleteUser(req));
                    pw.flush();
                }
                case "GET-ALL-USERS": {
                    pw.printf(getAllUsers(req));
                    pw.flush();
                }
                case "SEND-EMAIL": {
                    pw.printf(sendEmail(req));
                    pw.flush();
                }
                case "GET-EMAILS": {
                    pw.printf(getEmails(req));
                    pw.flush();
                }
                case "DELETE-EMAIL": {
                    pw.printf(deleteEmail(req));
                    pw.flush();
                }
            }
            client.close();

        }
    }

    public void stop() {
        try {
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}