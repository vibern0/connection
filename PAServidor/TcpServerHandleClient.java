
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    public TcpServerHandleClient(Socket socket)
    {
        this.socket = socket;
        this.current_folder = "/";
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
                
            }while(!command.equals(Properties.DISCONNECT_COMMAND));
            
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
        if(command.equals(Properties.DISCONNECT_COMMAND))
            return;
        
        if(command.equals(Properties.CUR_DIR_PATH_COMMAND))
        {
            try
            {
                ooStream.writeObject(Properties.CUR_DIR_PATH_COMMAND);
                ooStream.writeObject(current_folder);
                ooStream.flush();
            }
            catch (IOException ex)
            {
                Logger.getLogger(TcpServerHandleClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            //
        }
    }
}
