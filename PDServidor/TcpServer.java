

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import paservidor.Database;


public class TcpServer extends Thread
{
    private final ServerSocket serverSocket;
    private final Database database;
    private final String serverName;
    public TcpServer(ServerSocket serverSocket, String serverName)
    {
        this.serverSocket = serverSocket;
        this.serverName = serverName;
        this.database = new Database(serverName);
    }
    
    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                Socket socket = this.serverSocket.accept();
                TcpServerHandleClient handle = new TcpServerHandleClient(socket, serverName, database);
                Thread threadhandle = new Thread(handle);
                threadhandle.start();
            }
        }
        catch (SecurityException se)
        {
            System.err.println("Unable to get host address due to security.");
            System.err.println(se.toString());
            System.exit(1);
        }
        catch (IOException ioe)
        {
            System.err.println("Unable to read data from an open socket.");
            System.err.println(ioe.toString());
            System.exit(1);
        }
        finally
        {
            try
            {
                this.serverSocket.close();
            }
            catch (IOException ioe)
            {
                System.err.println("Unable to close an open socket.");
                System.err.println(ioe.toString());
                System.exit(1);
            }
        }
    }
}
