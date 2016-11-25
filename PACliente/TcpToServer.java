

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

    public TcpToServer(String hostname, int port)
    {
        registerNParams();
        
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
                checkCommand(ooStream, cmd);
                
            } while(!cmd.equals(Properties.COMMAND_DISCONNECT));
            oStream.close();
            ooStream.close();
            
            thread_tcpconn.join();
        }
        catch (IOException | InterruptedException ex)
        {
            Logger.getLogger(TcpToServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
    }
    
    private void checkCommand(ObjectOutputStream ooStream, String command) throws IOException
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