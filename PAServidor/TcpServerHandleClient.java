
import paservidor.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import paservidor.Database;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bernardovieira
 */
public class TcpServerHandleClient implements Runnable {

    private final Socket socket;
    private String current_folder;
    private final Database database;
    
    public TcpServerHandleClient(Socket socket)
    {
        this.socket = socket;
        this.current_folder = "/";
        this.database = new Database();
    }
    
    @Override
    public void run()
    {
        
        try
        {
            byte [] bytes = new byte[1024];
            String command;
            OutputStream oStream = socket.getOutputStream();
            InputStream iStream = socket.getInputStream();
            ObjectOutputStream ooStream = new ObjectOutputStream(oStream);
            ObjectInputStream oiStream = new ObjectInputStream(iStream);
            
            do
            {
                command = (String)oiStream.readObject();
                runCommand(command, ooStream);
                
            }while(!command.equals(Properties.COMMAND_DISCONNECT));
            
            oStream.close();
            iStream.close();
            ooStream.close();
            oiStream.close();
        }
        catch (IOException | ClassNotFoundException ex)
        {
            Logger.getLogger(TcpServerHandleClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void runCommand(String command, ObjectOutputStream ooStream)
    {
        if(command.equals(Properties.COMMAND_DISCONNECT))
            return;
        
        if(command.equals(Properties.COMMAND_CUR_DIR_PATH))
        {
            try
            {
                ooStream.writeObject(Properties.COMMAND_CUR_DIR_PATH);
                ooStream.writeObject(current_folder);
                ooStream.flush();
            }
            catch (IOException ex)
            {
                Logger.getLogger(TcpServerHandleClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(command.startsWith(Properties.COMMAND_REGISTER))
        {
            String [] params = command.split(" ");
            if(database.checkUser(params[1]))
            {
                try
                {
                    ooStream.writeObject(Properties.COMMAND_REGISTER);
                    ooStream.writeObject(Properties.ERROR_ALREADY_REGISTERED);
                    ooStream.flush();
                }
                catch (IOException ex)
                {
                    Logger.getLogger(TcpServerHandleClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                if(database.addUser(params[1], params[2]))
                {
                    try
                    {
                        ooStream.writeObject(Properties.COMMAND_REGISTER);
                        ooStream.writeObject(Properties.SUCCESS_REGISTER);
                        ooStream.flush();
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(TcpServerHandleClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
