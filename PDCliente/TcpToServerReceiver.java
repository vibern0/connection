
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import pacliente.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
    public static List<Socket> connectedTo = null;
    private final Socket socket;
    private InputStream iStream;
    
    public TcpToServerReceiver(Socket socket)
    {
        this.socket = socket;
        if(connectedTo == null)
        {
            connectedTo = new ArrayList<>();
        }
    }

    @Override
    public void run()
    {
        //
        try
        {
            String cmd;
            iStream = this.socket.getInputStream();
            ObjectInputStream oiStream = new ObjectInputStream(iStream);
            
            do
            {
                cmd = (String)oiStream.readObject();
                runCommand(cmd, oiStream);
                
            }while(!cmd.equals(Properties.DISCONNECT_FROM_SERVER));
            oiStream.close();
        }
        catch (IOException | ClassNotFoundException ex)
        {
            Logger.getLogger(TcpToServerReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void runCommand(String command, ObjectInputStream oiStream)
            throws ClassNotFoundException, ObjectStreamException, IOException
    {
        if(command.equals(Properties.COMMAND_CUR_DIR_PATH))
        {
            String server_output;
            server_output = (String)oiStream.readObject();
            System.out.println("Actual path is '" + server_output + "'");
        }
        else if(command.startsWith(Properties.COMMAND_REGISTER))
        {
            Integer output_type;
            output_type = (Integer)oiStream.readObject();
            if(output_type.equals(Properties.SUCCESS_REGISTER))
                System.out.println("You are successfully registered! Login now.");
            else if(output_type.equals(Properties.ERROR_ALREADY_REGISTERED))
                System.out.println("You are already registered.");
        }
        else if(command.startsWith(Properties.COMMAND_LOGIN))
        {
            Integer output_type;
            output_type = (Integer)oiStream.readObject();
            if(output_type.equals(Properties.SUCCESS_LOGGED))
            {
                TcpToServerReceiver.connectedTo.add(socket);
                System.out.println("You are logged now.");
            }
            else if(output_type.equals(Properties.ERROR_WRONG_PASSWORD))
                System.out.println("Erong login password.");
            else if(output_type.equals(Properties.ERROR_ACCOUNT_NOT_FOUND))
                System.out.println("You are not registered.");
        }
        else if(command.startsWith(Properties.COMMAND_LOGOUT))
        {
            Integer output_type;
            output_type = (Integer)oiStream.readObject();
            if(output_type.equals(Properties.SUCCESS_LOGOUT))
                System.out.println("You are unlogged now!");
            else if(output_type.equals(Properties.ERROR_NOT_LOGGED))
                System.out.println("You are not logged yet.");
        }
        else if(command.equals(Properties.COMMAND_CREATE_DIRECTORY))
        {
            Integer output_type;
            output_type = (Integer)oiStream.readObject();
            if(output_type.equals(Properties.SUCCESS_CREATE_DIRECTORY))
                System.out.println("Directory created!");
            else if(output_type.equals(Properties.ERROR_NOT_LOGGED))
                System.out.println("You are not logged yet.");
        }
        else if(command.equals(Properties.COMMAND_LIST_CONTENT))
        {
            Integer output_type;
            output_type = (Integer)oiStream.readObject();
            if(output_type.equals(Properties.SUCCESS_SLIST_CONTENT_DIR))
            {
                ArrayList<String> content = (ArrayList)oiStream.readObject();
                for(String c : content)
                {
                    System.out.println(c);
                }
            }
        }
        else if(command.equals(Properties.COMMAND_CHANGE_DIRECTORY))
        {
            Integer output_type;
            output_type = (Integer)oiStream.readObject();
            if(output_type.equals(Properties.ERROR_ON_ROOT_FOLDER))
                System.out.println("You are on root folder.");
            else if(output_type.equals(Properties.SUCCESS_CHANGE_DIRECTORY))
                System.out.println("You moved to another folder.");
        }
        else if(command.equals(Properties.COMMAND_COPY_FILE))
        {
            Integer output_type;
            output_type = (Integer)oiStream.readObject();
            if(output_type.equals(Properties.ERROR_WHEN_COPY_FILE))
                System.out.println("Error when copy file.");
            else if(output_type.equals(Properties.SUCCESS_WHEN_COPY_FILE))
                System.out.println("File successfully copied!.");
        }
        else if(command.equals(Properties.COMMAND_MOVE_FILE))
        {
            Integer output_type;
            output_type = (Integer)oiStream.readObject();
            if(output_type.equals(Properties.ERROR_WHEN_MOVE_FILE))
                System.out.println("Error moving file.");
            else if(output_type.equals(Properties.SUCCESS_WHEN_MOVE_FILE))
                System.out.println("You moved the file.");
        }
        else if(command.equals(Properties.COMMAND_REMOVE_FILE))
        {
            Integer output_type;
            output_type = (Integer)oiStream.readObject();
            if(output_type.equals(Properties.ERROR_WHEN_REMOVE_FILE))
                System.out.println("Error removing file.");
            else if(output_type.equals(Properties.SUCCESS_WHEN_REMOVE_FILE))
                System.out.println("Success removing file.");
        }
        else if(command.equals(Properties.COMMAND_UPLOAD))
        {
            //começar copiar
            //terminou copiar
            Integer output_type;
            output_type = (Integer)oiStream.readObject();
            if(output_type.equals(Properties.ERROR_UPLOAD_FILE))
            {
                System.out.println("Error uploading file.");
            }
            else if(output_type.equals(Properties.SUCCESS_UPLOAD_FILE))
            {
                File file = new File((String)oiStream.readObject());
                // Get the size of the file
                byte[] bytes = new byte[1024];
                InputStream in = new FileInputStream(file);

                int count;
                while ((count = in.read(bytes)) > 0) {
                    TcpToServer.oStream.write(bytes, 0, count);
                }
                System.out.println("File uploaded.");
                in.close();
            }
        }
        else if(command.equals(Properties.COMMAND_DOWNLOAD))
        {
            Integer output_type;
            output_type = (Integer)oiStream.readObject();
            
            OutputStream out = null;
            try
            {
                out = new FileOutputStream((String)oiStream.readObject());
            }
            catch (FileNotFoundException ex)
            {
                System.out.println("File not found. ");
            }
            
            if(out == null)
            {
                System.out.println("Error creating file.");
            }
            else
            {
                byte[] bytes = new byte[1024];
                
                long length = (Long)oiStream.readObject();
                long atual_length = 0;

                int count;
                while (atual_length < length)
                {
                    count = iStream.read(bytes);
                    out.write(bytes, 0, count);
                    System.out.println("a");
                    atual_length += count;
                }
                System.out.println("b");
                out.close();
                System.out.println("File dowloaded. ");
            }
        }
    }
}