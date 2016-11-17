

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TcpToServer
{
    private Socket socket;

    public TcpToServer(String hostname, int port)
    {
        try
        {
            this.socket = new Socket(hostname, port);
            
            Scanner sc;
            String cmd;
            
            OutputStream oStream = this.socket.getOutputStream();
            ObjectOutputStream ooStream = new ObjectOutputStream(oStream);
            
            TcpToServerReceiver tcpconn = new TcpToServerReceiver(socket);
            Thread thread_tcpconn = new Thread(tcpconn);
            thread_tcpconn.start();
            
            sc = new Scanner(System.in);
            do
            {
                System.out.print("Command:");
                cmd = sc.nextLine();
                ooStream.writeObject(cmd);
                ooStream.flush();
                
            } while(!cmd.equals(Properties.DISCONNECT_COMMAND));
            ooStream.close();
            
            thread_tcpconn.join();
        }
        catch (IOException | InterruptedException ex)
        {
            Logger.getLogger(TcpToServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}