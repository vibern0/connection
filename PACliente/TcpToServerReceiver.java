
import pacliente.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Objects;
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
                
            }while(!cmd.equals(Properties.COMMAND_DISCONNECT));
            oiStream.close();
        }
        catch (IOException | ClassNotFoundException ex)
        {
            Logger.getLogger(TcpToServerReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void runCommand(String command, ObjectInputStream oiStream)
    {
        if(command.equals(Properties.COMMAND_DISCONNECT))
            return;
        
        if(command.equals(Properties.COMMAND_CUR_DIR_PATH))
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
        else if(command.startsWith(Properties.COMMAND_REGISTER))
        {
            Integer output_type;
            try
            {
                output_type = (Integer)oiStream.readObject();
                if(Objects.equals(output_type, Properties.SUCCESS_REGISTER))
                    System.out.println("You are successfully registered! Login now.");
                else if(Objects.equals(output_type, Properties.ERROR_ALREADY_REGISTERED))
                    System.out.println("You are already registered.");
                else if(Objects.equals(output_type, Properties.ERROR_MISSING_PARAMS))
                    System.out.println("Missing parameters. register [username] [password]");
            } 
            catch (IOException | ClassNotFoundException ex)
            {
                Logger.getLogger(TcpToServerReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(command.startsWith(Properties.COMMAND_LOGIN))
        {
            Integer output_type;
            try
            {
                output_type = (Integer)oiStream.readObject();
                if(Objects.equals(output_type, Properties.SUCCESS_LOGGED))
                    System.out.println("You are logged now!");
                else if(Objects.equals(output_type, Properties.ERROR_ALREADY_LOGGED))
                    System.out.println("You are already registered.");
                else if(Objects.equals(output_type, Properties.ERROR_WRONG_PASSWORD))
                    System.out.println("Erong login password.");
                else if(Objects.equals(output_type, Properties.ERROR_MISSING_PARAMS))
                    System.out.println("Missing parameters. login [username] [password]");
                else if(Objects.equals(output_type, Properties.ERROR_ACCOUNT_NOT_FOUND))
                    System.out.println("You are not registered.");
            } 
            catch (IOException | ClassNotFoundException ex)
            {
                Logger.getLogger(TcpToServerReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}