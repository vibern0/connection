
import pacliente.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
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
                if(output_type.equals(Properties.SUCCESS_REGISTER))
                    System.out.println("You are successfully registered! Login now.");
                else if(output_type.equals(Properties.ERROR_ALREADY_REGISTERED))
                    System.out.println("You are already registered.");
                else if(output_type.equals(Properties.ERROR_MISSING_PARAMS))
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
                if(output_type.equals(Properties.SUCCESS_LOGGED))
                {
                    Properties.LOGGED = true;
                    System.out.println("You are logged now!");
                }
                else if(output_type.equals(Properties.ERROR_ALREADY_LOGGED))
                    System.out.println("You are already registered.");
                else if(output_type.equals(Properties.ERROR_WRONG_PASSWORD))
                    System.out.println("Erong login password.");
                else if(output_type.equals(Properties.ERROR_MISSING_PARAMS))
                    System.out.println("Missing parameters. login [username] [password]");
                else if(output_type.equals(Properties.ERROR_ACCOUNT_NOT_FOUND))
                    System.out.println("You are not registered.");
            } 
            catch (IOException | ClassNotFoundException ex)
            {
                Logger.getLogger(TcpToServerReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(command.startsWith(Properties.COMMAND_LOGOUT))
        {
            Integer output_type;
            try
            {
                output_type = (Integer)oiStream.readObject();
                if(output_type.equals(Properties.SUCCESS_LOGOUT))
                    System.out.println("You are unlogged now!");
                else if(output_type.equals(Properties.ERROR_NOT_LOGGED))
                    System.out.println("You are not logged yet.");
            } 
            catch (IOException | ClassNotFoundException ex)
            {
                Logger.getLogger(TcpToServerReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(command.equals(Properties.COMMAND_CREATE_DIRECTORY))
        {
            Integer output_type;
            try
            {
                output_type = (Integer)oiStream.readObject();
                if(output_type.equals(Properties.SUCCESS_CREATE_DIRECTORY))
                    System.out.println("Directory created!");
                else if(output_type.equals(Properties.ERROR_NOT_LOGGED))
                    System.out.println("You are not logged yet.");
                else if(output_type.equals(Properties.ERROR_MISSING_PARAMS))
                    System.out.println("Missing parameters. mkdir [direcory name]");
            } 
            catch (IOException | ClassNotFoundException ex)
            {
                Logger.getLogger(TcpToServerReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(command.equals(Properties.COMMAND_LIST_CONTENT))
        {
            Integer output_type;
            try
            {
                output_type = (Integer)oiStream.readObject();
                if(output_type.equals(Properties.ERROR_NOT_LOGGED))
                    System.out.println("You are not logged yet.");
                else if(output_type.equals(Properties.SUCCESS_SLIST_CONTENT_DIR))
                {
                    ArrayList<String> content = (ArrayList)oiStream.readObject();
                    for(String c : content)
                    {
                        System.out.println(c);
                    }
                }
               
            } 
            catch (IOException | ClassNotFoundException ex)
            {
                Logger.getLogger(TcpToServerReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(command.equals(Properties.COMMAND_CHANGE_DIRECTORY))
        {
            Integer output_type;
            try
            {
                output_type = (Integer)oiStream.readObject();
                if(output_type.equals(Properties.ERROR_NOT_LOGGED))
                    System.out.println("You are not logged yet.");
                else if(output_type.equals(Properties.ERROR_ON_ROOT_FOLDER))
                    System.out.println("You are on root folder.");
                else if(output_type.equals(Properties.ERROR_MISSING_PARAMS))
                    System.out.println("Missing parameters. Use cd [directory]");
                else if(output_type.equals(Properties.SUCCESS_CHANGE_DIRECTORY))
                    System.out.println("Yu moved to another folder.");
            } 
            catch (IOException | ClassNotFoundException ex)
            {
                Logger.getLogger(TcpToServerReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(command.equals(Properties.COMMAND_COPY_FILE))
        {
            Integer output_type;
            try
            {
                output_type = (Integer)oiStream.readObject();
                if(output_type.equals(Properties.ERROR_WHEN_COPY_FILE))
                    System.out.println("Error when copy file.");
                else if(output_type.equals(Properties.SUCCESS_WHEN_COPY_FILE))
                    System.out.println("File successfully copied!.");
            } 
            catch (IOException | ClassNotFoundException ex)
            {
                Logger.getLogger(TcpToServerReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(command.equals(Properties.COMMAND_MOVE_FILE))
        {
            Integer output_type;
            try
            {
                output_type = (Integer)oiStream.readObject();
                if(output_type.equals(Properties.ERROR_WHEN_MOVE_FILE))
                    System.out.println("You are on root folder.");
                else if(output_type.equals(Properties.SUCCESS_WHEN_MOVE_FILE))
                    System.out.println("Yu moved to another folder.");
            } 
            catch (IOException | ClassNotFoundException ex)
            {
                Logger.getLogger(TcpToServerReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}