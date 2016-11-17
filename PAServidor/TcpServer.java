

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


public class TcpServer
{
    private ServerSocket serverSocket;
    

    public TcpServer(int port)
    {
        initServerSocket(port);
        try
        {
            while (true)
            {
                Socket socket = this.serverSocket.accept();
                TcpServerHandleClient handle = new TcpServerHandleClient(socket);
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

    private void initServerSocket(int port)
    {
        try
        {
            this.serverSocket = new java.net.ServerSocket(port);
            assert this.serverSocket.isBound();
            if (this.serverSocket.isBound())
            {
                System.out.println("SERVER inbound data port " +
                    this.serverSocket.getLocalPort() +
                    " is ready and waiting for client to connect...");
            }
        }
        catch (SocketException se)
        {
            System.err.println("Unable to create socket.");
            System.err.println(se.toString());
            System.exit(1);
        }
        catch (IOException ioe)
        {
            System.err.println("Unable to read data from an open socket.");
            System.err.println(ioe.toString());
            System.exit(1);
        }
    }
}
