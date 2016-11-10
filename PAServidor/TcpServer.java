

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


public class TcpServer
{
    public final static int COMM_PORT = 5007;

    private ServerSocket serverSocket;
    private InetSocketAddress inboundAddr;
    private TcpPayload payload;

    public TcpServer()
    {
        this.payload = new TcpPayload();
        initServerSocket();
        try
        {
            while (true)
            {
                // listen for and accept a client connection to serverSocket
                Socket sock = this.serverSocket.accept();
                OutputStream oStream = sock.getOutputStream();
                ObjectOutputStream ooStream = new ObjectOutputStream(oStream);
                ooStream.writeObject(this.payload);  // send serilized payload
                ooStream.close();
                Thread.sleep(1000);
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
        catch (InterruptedException ie) { }  // Thread sleep interrupted
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

    private void initServerSocket()
    {
        this.inboundAddr = new InetSocketAddress(COMM_PORT);
        try
        {
            this.serverSocket = new java.net.ServerSocket(COMM_PORT);
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
