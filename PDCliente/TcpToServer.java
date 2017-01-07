

import pdcliente.Properties;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpToServer
{
    private final Socket socket;
    public static OutputStream oStream;
    public static ObjectOutputStream ooStream;
    private final Thread thread_tcpconn;
    private final TcpToServerReceiver tcpconn;

    public TcpToServer(String hostname, int port, String serverName)
            throws IOException
    {
        
        socket = new Socket(hostname, port);
        oStream = this.socket.getOutputStream();
        ooStream = new ObjectOutputStream(oStream);

        tcpconn = new TcpToServerReceiver(socket, serverName);
        thread_tcpconn = new Thread(tcpconn);
        thread_tcpconn.start();
        
    }
    
    public void close() throws IOException, InterruptedException
    {
        tcpconn.toClose();
        oStream.close();
        ooStream.close();
        socket.close();
        
        thread_tcpconn.join();
    }
    
    public void checkCommand(String command) throws IOException
    {
        if(!TcpToServerReceiver.connectedTo.contains(socket)
                && (!command.startsWith(Properties.COMMAND_LOGIN) &&
                !command.startsWith(Properties.COMMAND_REGISTER)))
        {
            System.out.println("Voce ainda nao esta autenticado!");
        }
        else if(TcpToServerReceiver.connectedTo.contains(socket)
                && command.startsWith(Properties.COMMAND_LOGIN))
        {
            System.out.println("Voce ja esta autenticado!");
        }
        else
        {
            if(     command.startsWith(Properties.COMMAND_LOGIN) ||
                    command.startsWith(Properties.COMMAND_REGISTER))
            {
                String [] tmp = command.split(" ");
                command = tmp[0] + " " + PDCliente.username + " " + tmp[1];
            }
            ooStream.writeObject(command);
            ooStream.flush();
        }
    }

}