

import pacliente.Properties;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TcpToServer
{
    private Socket socket;
    private Map<String, Integer> params;
    public static OutputStream oStream;
    private ObjectOutputStream ooStream;
    private Thread thread_tcpconn;

    public TcpToServer(String hostname, int port)
            throws IOException
    {
        registerNParams();
        
        this.socket = new Socket(hostname, port);
        oStream = this.socket.getOutputStream();
        this.ooStream = new ObjectOutputStream(oStream);

        TcpToServerReceiver tcpconn = new TcpToServerReceiver(socket);
        this.thread_tcpconn = new Thread(tcpconn);
        this.thread_tcpconn.start();
        
    }
    
    public void close() throws IOException, InterruptedException
    {
        oStream.close();
        ooStream.close();
        
        thread_tcpconn.join();
    }
    
    private void registerNParams()
    {
        params = new HashMap<>();
        params.put(Properties.COMMAND_DISCONNECT,       0);
        params.put(Properties.COMMAND_CUR_DIR_PATH,     0);
        params.put(Properties.COMMAND_REGISTER,         2);
        params.put(Properties.COMMAND_LOGIN,            2);
        params.put(Properties.COMMAND_LOGOUT,           0);
        params.put(Properties.COMMAND_CREATE_DIRECTORY, 1);
        params.put(Properties.COMMAND_LIST_CONTENT,     0);
        params.put(Properties.COMMAND_CHANGE_DIRECTORY, 1);
        params.put(Properties.COMMAND_COPY_FILE,        2);
        params.put(Properties.COMMAND_MOVE_FILE,        2);
        params.put(Properties.COMMAND_REMOVE_FILE,      1);
        params.put(Properties.COMMAND_UPLOAD,           1);
        params.put(Properties.COMMAND_DOWNLOAD,         1);
    }
    
    public void checkCommand(String command) throws IOException
    {
        if(!Properties.LOGGED && !command.startsWith(Properties.COMMAND_LOGIN))
        {
            System.out.println("You are not logged yet!");
        }
        else if(Properties.LOGGED && command.startsWith(Properties.COMMAND_LOGIN))
        {
            System.out.println("You are already logged!");
        }
        else
        {
            String [] ps = command.split(" ");
            Integer nparams = params.get(ps[0]);
            
            if(ps.length - 1 == nparams)
            {
                ooStream.writeObject(command);
                ooStream.flush();
            }
            else
            {
                System.out.println("Parameters missing!");
            }
        }
    }

}