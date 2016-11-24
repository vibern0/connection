

import paservidor.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
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
    private boolean logged;
    
    public TcpServerHandleClient(Socket socket)
    {
        this.socket = socket;
        this.current_folder = "/";
        this.database = new Database();
        this.logged = false;
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
            throws ObjectStreamException, FileAlreadyExistsException,
                SecurityException, UnsupportedOperationException,
                NotDirectoryException, IOException
    {
        if(command.equals(Properties.COMMAND_DISCONNECT))
            return;
        
        if(command.equals(Properties.COMMAND_CUR_DIR_PATH))
        {
            ooStream.writeObject(Properties.COMMAND_CUR_DIR_PATH);
            ooStream.writeObject(current_folder);
            ooStream.flush();
        }
        else if(command.startsWith(Properties.COMMAND_REGISTER))
        {
            String [] params = command.split(" ");
            
            if(database.checkUser(params[1]))
            {
                ooStream.writeObject(Properties.COMMAND_REGISTER);
                ooStream.writeObject(Properties.ERROR_ALREADY_REGISTERED);
                ooStream.flush();
            }
            else
            {
                if(database.addUser(params[1], params[2]))
                {
                    ooStream.writeObject(Properties.COMMAND_REGISTER);
                    ooStream.writeObject(Properties.SUCCESS_REGISTER);
                    ooStream.flush();
                }
            }
        }
        else if(command.startsWith(Properties.COMMAND_LOGIN))
        {
            String [] params = command.split(" ");
            Integer result;
            
            result = database.checkLogin(params[1], params[2]);
            ooStream.writeObject(Properties.COMMAND_LOGIN);
            ooStream.writeObject(result);
            ooStream.flush();

            if(Objects.equals(result, Properties.SUCCESS_LOGGED))
                logged = true;
        }
        else if(command.equals(Properties.COMMAND_LOGOUT))
        {
            ooStream.writeObject(Properties.COMMAND_LOGOUT);
            ooStream.writeObject(Properties.SUCCESS_LOGOUT);
            ooStream.flush();
        }
        else if(command.startsWith(Properties.COMMAND_CREATE_DIRECTORY))
        {
            String [] params = command.split(" ");
            
            Path currentRelativePath = Paths.get("");
            String path = currentRelativePath.toAbsolutePath().toString();

            Files.createDirectories(Paths.get(path + current_folder + params[1]));

            ooStream.writeObject(Properties.COMMAND_CREATE_DIRECTORY);
            ooStream.writeObject(Properties.SUCCESS_CREATE_DIRECTORY);
            ooStream.flush();
        }
        else if(command.equals(Properties.COMMAND_LIST_CONTENT))
        {
            ArrayList<String> content = new ArrayList<>();
            Path currentRelativePath = Paths.get("");
            String path = currentRelativePath.toAbsolutePath().toString();
            Path dir = Paths.get(path + current_folder);
                
            DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
            for (Path file: stream)
            {
                content.add(file.getFileName().toString());
            }
            ooStream.writeObject(Properties.COMMAND_LIST_CONTENT);
            ooStream.writeObject(Properties.SUCCESS_SLIST_CONTENT_DIR);
            ooStream.writeObject((ArrayList)content);
            ooStream.flush();
        }
        else if(command.startsWith(Properties.COMMAND_CHANGE_DIRECTORY))
        {
            String [] params = command.split(" ");
            
            ArrayList<String> content = new ArrayList<>();
            Path currentRelativePath = Paths.get("");
            String path = currentRelativePath.toAbsolutePath().toString();
            Path dir = currentRelativePath;
            
            if(params[1].equals(".."))
            {
                if(current_folder.equals("/"))
                {
                    ooStream.writeObject(Properties.COMMAND_CHANGE_DIRECTORY);
                    ooStream.writeObject(Properties.ERROR_ON_ROOT_FOLDER);
                    ooStream.flush();
                }
                else
                {
                    String opath = current_folder.substring(0, current_folder.length()-1);
                    int last = opath.lastIndexOf("/");
                    current_folder = current_folder.substring(0, last + 1);
                    
                    dir = Paths.get(path + current_folder);
                }
            }
            else 
            {
                current_folder += params[1] + "/";
                dir = Paths.get(path + current_folder);
            }   
            
            if (Files.exists(dir))
            {
                ooStream.writeObject(Properties.COMMAND_CHANGE_DIRECTORY);
                ooStream.writeObject(Properties.SUCCESS_CHANGE_DIRECTORY);
                ooStream.flush();
            }
        }
        else if(command.startsWith(Properties.COMMAND_COPY_FILE))
        {
            String [] params = command.split(" ");
            
            //open file
            //read it
            //write in new place
            
            //send a message saying if it was successfully copied or an error
            //occurred
        }
        else if(command.startsWith(Properties.COMMAND_MOVE_FILE))
        {
            String [] params = command.split(" ");
            
            //open file
            //read it
            //write in new place
            
            //send a message saying if it was successfully copied or an error
            //occurred
        }
    }
}
