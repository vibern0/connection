

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import paservidor.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private String current_folder;
    private final Database database;
    private final OutputStream oStream;
    private final InputStream iStream;
    private final String serverName;
    private String player_name;
    private String client_folder_on_server;
    private ObjectOutputStream ooStream;
    private ObjectInputStream oiStream;
    private final RMI rmi;
    
    public TcpServerHandleClient(Socket socket, String serverName,
            Database database, RMI rmi)
            throws IOException
    {
        this.current_folder = "/";
        this.database = database;
        this.serverName = serverName;
        this.oStream = socket.getOutputStream();
        this.iStream = socket.getInputStream();
        this.rmi = rmi;
    }
    
    @Override
    public void run()
    {
        
        try
        {
            String command;
            //
            ooStream = new ObjectOutputStream(oStream);
            oiStream = new ObjectInputStream(iStream);
            
            do
            {
                command = (String)oiStream.readObject();
                runCommand(command);
                
            }while(!command.equals(Properties.COMMAND_DISCONNECT));
            rmi.logoutUser(player_name);
            oStream.close();
            iStream.close();
            ooStream.close();
            oiStream.close();
        }
        catch (IOException | ClassNotFoundException | SQLException ex)
        {
            try
            {
                rmi.logoutUser(player_name);
            }
            catch (RemoteException ex1)
            {
                Logger.getLogger(TcpServerHandleClient.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(TcpServerHandleClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void runCommand(String command)
            throws ObjectStreamException, FileAlreadyExistsException,
                SecurityException, UnsupportedOperationException,
                NotDirectoryException, DirectoryNotEmptyException,
                UnsupportedOperationException, IOException, SQLException, ClassNotFoundException
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
                database.addUser(params[1], params[2]);
                Files.createDirectories(Paths.get(
                        serverName + "/" + params[1]));

                ooStream.writeObject(Properties.COMMAND_REGISTER);
                ooStream.writeObject(Properties.SUCCESS_REGISTER);
                ooStream.flush();
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
            
            if(result.equals(Properties.SUCCESS_LOGGED))
            {
                rmi.loginUser(params[1]);
                player_name = params[1];
                client_folder_on_server = serverName + "/" + params[1];
            }
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
            
            Files.createDirectories(Paths.get(client_folder_on_server +
                    current_folder + params[1]));

            ooStream.writeObject(Properties.COMMAND_CREATE_DIRECTORY);
            ooStream.writeObject(Properties.SUCCESS_CREATE_DIRECTORY);
            ooStream.flush();
        }
        else if(command.equals(Properties.COMMAND_LIST_CONTENT))
        {
            ArrayList<String> content = new ArrayList<>();
            Path dir = Paths.get(client_folder_on_server + current_folder);
                
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
            
            String [] moves = params[1].split("/");
            boolean success = true;
            
            for(String move : moves)
            {
                if(move.equals(".."))
                {
                    if(current_folder.equals("/"))
                    {
                        success = false;
                        break;
                    }
                    else
                    {
                        String opath = current_folder.substring(0, current_folder.length()-1);
                        int last = opath.lastIndexOf("/");
                        current_folder = current_folder.substring(0, last + 1);
                    }
                }
                else 
                {
                    current_folder += move + "/";
                }   

                if (!Files.exists(Paths.get(client_folder_on_server
                        + current_folder)))
                {
                    success = false;
                    break;
                }
            }
            
            ooStream.writeObject(Properties.COMMAND_CHANGE_DIRECTORY);
            if(!success)
            {
                ooStream.writeObject(Properties.ERROR_ON_ROOT_FOLDER);
            }
            else
            {
                ooStream.writeObject(Properties.SUCCESS_CHANGE_DIRECTORY);
            }
            ooStream.flush();
        }
        else if(command.startsWith(Properties.COMMAND_COPY_FILE))
        {
            String [] params = command.split(" ");
            
            Path source = Paths.get(client_folder_on_server +
                    current_folder + params[1]);
            Path newdir = Paths.get(client_folder_on_server +
                    current_folder + params[2] + "/" + params[1]);
            Path ret = Files.copy(source, newdir);
            
            ooStream.writeObject(Properties.COMMAND_COPY_FILE);
            if(ret == null)
            {
                ooStream.writeObject(Properties.ERROR_WHEN_COPY_FILE); 
            }
            else
            {
                ooStream.writeObject(Properties.SUCCESS_WHEN_COPY_FILE);
            }
            ooStream.flush();
        }
        else if(command.startsWith(Properties.COMMAND_MOVE_FILE))
        {
            String [] params = command.split(" ");
            
            Path source = Paths.get(client_folder_on_server +
                    current_folder + params[1]);
            Path newdir = Paths.get(client_folder_on_server +
                    current_folder + params[2] + "/" + params[1]);
            Path ret = Files.move(source, newdir);
            
            ooStream.writeObject(Properties.COMMAND_MOVE_FILE);
            if(ret == null)
            {
                ooStream.writeObject(Properties.ERROR_WHEN_MOVE_FILE);
            }
            else
            {
                ooStream.writeObject(Properties.SUCCESS_WHEN_MOVE_FILE);
            }
            ooStream.flush();
        }
        else if(command.startsWith(Properties.COMMAND_REMOVE_FILE))
        {
            String [] params = command.split(" ");
            
            File file = new File(client_folder_on_server +
                    current_folder + params[1]);
            ooStream.writeObject(Properties.COMMAND_REMOVE_FILE);
            if(!file.exists())
            {
                ooStream.writeObject(Properties.ERROR_WHEN_REMOVE_FILE);
            }
            else if(file.isDirectory())
            {
                if(file.list().length == 0)
                {
                    file.delete();
                    ooStream.writeObject(Properties.SUCCESS_WHEN_REMOVE_FILE);
                }
                else
                {
                    ooStream.writeObject(Properties.ERROR_WHEN_REMOVE_FILE);
                }
            }
            else
            {
                ooStream.writeObject(Properties.SUCCESS_WHEN_REMOVE_FILE);
                file.delete();
            }
            ooStream.flush();
        }
        else if(command.startsWith(Properties.COMMAND_UPLOAD))
        {
            String [] params = command.split(" ");
            ooStream.writeObject(Properties.COMMAND_UPLOAD);
            if(params[2].charAt(0) != '/')
            {
                params[2] = '/' + params[2];
            }
            if(params[2].lastIndexOf("/") != params[2].length() - 1)
            {
                params[2] += '/';
            }
            try
            {
                System.out.println(client_folder_on_server +
                        params[2] + params[1]);
                OutputStream out = new FileOutputStream(client_folder_on_server +
                        params[2] + params[1]);
                //
                ooStream.writeObject(Properties.SUCCESS_UPLOAD_FILE);
                ooStream.writeObject(params[1]);
                ooStream.flush();
                
                System.out.println("a");
                long length = (Long)oiStream.readObject();
                System.out.println(length);
                System.out.println("a");
                
                byte[] bytes = new byte[1024];
                long atual_length = 0;
                int count;
                while (atual_length < length)
                {
                    count = iStream.read(bytes);
                    out.write(bytes, 0, count);
                    out.flush();
                    atual_length += count;
                }
                out.close();
            }
            catch (FileNotFoundException ex)
            {
                ooStream.writeObject(Properties.ERROR_UPLOAD_FILE);
                ooStream.flush();
            }
        }
        else if(command.startsWith(Properties.COMMAND_DOWNLOAD))
        {
            String [] params = command.split(" ");
            ooStream.writeObject(Properties.COMMAND_DOWNLOAD);
            
            File file = new File(params[1]);
            long length = file.length();
            byte[] bytes = new byte[1024];
            try
            {
                InputStream in = new FileInputStream(file);
                ooStream.writeObject(Properties.SUCCESS_DOWNLOAD_FILE);
                ooStream.writeObject(params[1] + " " + params[2]);
                ooStream.writeObject((Long)length);
                ooStream.flush();
            
                int count;
                while ((count = in.read(bytes)) > 0)
                {
                    oStream.write(bytes, 0, count);
                    oStream.flush();
                }
                in.close();
            }
            catch(FileNotFoundException ex)
            {
                ooStream.writeObject(Properties.ERROR_DOWLOAD_FILE);
                ooStream.flush();
            }
        }
    }
}
