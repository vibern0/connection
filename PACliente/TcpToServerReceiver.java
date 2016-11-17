
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
class TcpToServerReceiver implements Runnable
{
    private final Socket socket;
    
    public TcpToServerReceiver(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        //
        try
        {
            String cmd;
            InputStream iStream = this.socket.getInputStream();
            ObjectInputStream oiStream = new ObjectInputStream(iStream);
            
            do
            {
                cmd = (String)oiStream.readObject();
                runCommand(cmd, oiStream);
                
            }while(!cmd.equals(Properties.DISCONNECT_COMMAND));
            oiStream.close();
        }
        catch (IOException | ClassNotFoundException ex)
        {
            Logger.getLogger(TcpToServerReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void runCommand(String command, ObjectInputStream oiStream)
    {
        if(command.equals(Properties.DISCONNECT_COMMAND))
            return;
        
        if(command.equals(Properties.CUR_DIR_PATH_COMMAND))
        {
            String server_output;
            try
            {
                server_output = (String)oiStream.readObject();
                System.out.println("Actual path is '" + server_output + "'");
            } 
            catch (IOException | ClassNotFoundException ex)
            {
                Logger.getLogger(TcpToServerReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            
        }
    }
}