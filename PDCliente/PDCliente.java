
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacliente.Properties;

public class PDCliente
{
    private static List<TcpToServer> tcpToServer;
    private static UdpClient udpClient;
    private static RMI rmi;
    private static List<String> connectedToServers;
    private static int toServer;
    private static boolean commandsToRemote;
    private static String current_server_folder;
    private static String current_folder;
    
    public static void main(String[] args)
    {    
        if(args.length < 3)
        {
            System.out.println("Incompleto! Parametros -> [ip UDP] [porto UDP] [username]");
            return;
        }
        
        registerNParams();
        connectedToServers = new ArrayList<>();
        try
        {
            udpClient = new UdpClient(args[0], Integer.parseInt(args[1]));
            System.out.println("UDP iniciado no porto " + udpClient.getPort());
            udpClient.start();
        }
        catch (SocketException ex)
        {
            System.out.println("Erro ao iniciar UDP!" + ex);
            return;
        }
        
        new HeartbeatClient(udpClient.getSocket(), args[0], Integer.parseInt(args[1])).run();
        
        rmi = new RMI(args[2]);
        try
        {
            rmi.run(args[0]);
        }
        catch(RemoteException e)
        {
            udpClient.finish();
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        }
        catch (MalformedURLException | NotBoundException ex)
        {
            System.out.println("Erro RMI." + ex);
            System.exit(1);
        }
        current_folder = "";
        tcpToServer = new ArrayList<>();
        toServer = -1;
        commandsToRemote = true;
        //
        
        Scanner sc;
        String command;
        sc = new Scanner(System.in);
        do
        {
            System.out.print("Command:");
            command = sc.nextLine();
            if(checkParams(command))
            {
                processCommand(command);
            }
            else
            {
                System.out.println("Parameters missing!");
            }

        } while(!command.equals(Properties.COMMAND_FINISH));
        
        try
        {
            rmi.close();
        }
        catch (RemoteException ex)
        {
            System.out.println("Erro ao fechar RMI." + ex);
            System.exit(1);
        }
    }
    
    private static void processCommand(String command)
    {
        if(command.equals("help"))
        {
            System.out.println("Help wanted! :");
            //mostra todos os comandos
        }
        if(command.startsWith(Properties.CHANGE_SERVER))
        {
            String [] params = command.split(" ");
            int serverNumber = Integer.parseInt(params[1]);
            if(serverNumber < 0)
                return;
            if(serverNumber >= tcpToServer.size())
                return;
            if(tcpToServer.get(toServer) == null)
                return;

            toServer = serverNumber;
        }
        else if(command.startsWith(Properties.CHANGE_FOLDER))
        {
            String [] params = command.split(" ");

            if(params.length < 2)
                return;

            if(params[1].equals(Properties.FOLDER_REMOTE))
            {
                commandsToRemote = true;
                System.out.println("Comando reconhecidos remotamente!");
            }
            else if(params[1].equals(Properties.FOLDER_LOCAL))
            {
                commandsToRemote = false;
                System.out.println("Comando reconhecidos localmente!");
            }
            else
            {
                System.out.println("Cant understand the location!");
            }
        }
        else if(command.startsWith(Properties.LIST) ||
                command.startsWith(Properties.MESSAGE_TO_ALL) ||
                command.startsWith(Properties.MESSAGE_TO_CLIENT) ||
                command.startsWith(Properties.CONNECT_TO_SERVER) ||
                command.startsWith(Properties.DISCONNECT_FROM_SERVER))
        {
            try
            {
                if(command.startsWith(Properties.LIST_SERVERS))
                {
                    List<String> serversN = rmi.getAllServersName();
                    System.out.println("Servers available:");
                    for(String name : serversN)
                    {
                        if(!connectedToServers.contains(name))
                            System.out.println(name);
                    }
                }
                else if(command.startsWith(Properties.LIST_CONNECTED))
                {
                    for(String name : connectedToServers)
                    {
                        System.out.println(name);
                    }
                }
                else if(command.startsWith(Properties.LIST_CLIENTS))
                {
                    List<String> clients = rmi.getAllOnlineClients();
                    System.out.println("Clients online:");
                    for(String client : clients)
                    {
                        System.out.println(client);
                    }
                }
                //message to client
                //message to all
                else if(command.startsWith(Properties.CONNECT_TO_SERVER))
                {
                    String [] params = command.split(" ");
                    if(connectedToServers.contains(params[1]))
                    {
                        System.out.println("Ja esta conectado a esse servidor!");
                        return;
                    }
                    //connectar ao servidor
                    List<String> servers = rmi.getAllServersName();
                    if(!servers.contains(params[1]))
                    {
                        System.out.println("Servidor nao encontrado!");
                        return;
                    }
                    //
                    try
                    {
                        rmi.connectToServer(params[1]);
                        System.out.println("Voce conectou-se ao servidor " + 
                            params[1]);
                        System.out.println(rmi.getServerIP(params[1]) + " " + rmi.getServerPort(params[1]));
                        connectedToServers.add(params[1]);
                        TcpToServer tcp = new TcpToServer(
                                "127.0.0.1",
                                rmi.getServerPort(params[1]),
                                params[1]
                        );
                        tcpToServer.add(tcp);
                        current_server_folder = "remote" + params[1];
                        current_folder = "/";
                        toServer = tcpToServer.size() - 1;
                        
                        File file = new File(current_folder);
                        if(!file.exists())
                        {
                            Files.createDirectories(Paths.get(current_folder));
                        }
                    }
                    catch(RemoteException ex)
                    {
                        System.out.println("Erro ao conectar ao servidor " + ex);
                    }
                }
                else if(command.startsWith(Properties.DISCONNECT_FROM_SERVER))
                {
                    String [] params = command.split(" ");
                    if(params.length < 2)
                    {
                        System.out.println("É necessário dizer o nome do servidor!");
                        return;
                    }
                    if(params[1].equals("all"))
                    {
                        for(String serverName : connectedToServers)
                        {
                            try
                            {
                                tcpToServer.get(toServer).checkCommand(
                                        Properties.DISCONNECT_FROM_SERVER);
                                rmi.disconnectFromServer(serverName);
                                tcpToServer.get(toServer).close();
                                tcpToServer.remove(toServer);
                                connectedToServers.remove(serverName);
                            }
                            catch (IOException ex)
                            {
                                System.out.println(
                                        "Erro ao enviar comando para TCP." + ex);
                                System.exit(1);
                            }
                            catch (InterruptedException ex)
                            {
                                System.out.println("Erro na conexao TCP." + ex);
                                System.exit(1);
                            }
                        }
                        toServer = -1;
                    }
                    else
                    {
                        if(!connectedToServers.contains(params[1]))
                        {
                            System.out.println(
                                    "Nao esta conectado a esse servidor!");
                            return;
                        }
                        //
                        List<String> servers = rmi.getAllServersName();
                        if(!servers.contains(params[1]))
                        {
                            System.out.println("Servidor nao encontrado!");
                            return;
                        }
                        //
                        try
                        {
                            tcpToServer.get(toServer).checkCommand(params[0]);
                            rmi.disconnectFromServer(params[1]);
                            tcpToServer.get(toServer).close();
                            tcpToServer.remove(toServer);
                            connectedToServers.remove(params[1]);
                            toServer = -1;
                            System.out.println("Voce desconctou-se do servidor!");
                            System.out.println("Conecte-se a outro servidor!");
                        }
                        catch (IOException ex)
                        {
                            System.out.println("Erro ao enviar comando para TCP." + ex);
                            System.exit(1);
                        }
                        catch (InterruptedException ex)
                        {
                            System.out.println("Erro na conexao TCP." + ex);
                            System.exit(1);
                        }
                    }
                    
                }
            }
            catch (IOException ex)
            {
                System.out.println("Erro ao enviar comando para UDP." + ex);
                System.exit(1);
            }
        }
        else if(!commandsToRemote)
        {
            try
            {
                processLocalCommand(command);
            }
            catch (IOException ex)
            {
                Logger.getLogger(PDCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(tcpToServer.size() > 0)
        {
            if(toServer == -1)
            {
                System.out.println("Voce nao esta a trabalhar em nenhum servidor no momento.");
                System.out.println("Conecte-se um servidor ou altere a area de trabalho.");
                return;
            }
            try
            {
                tcpToServer.get(toServer).checkCommand(command);
                //enviar para servidor tcp
            }
            catch (IOException ex)
            {
                System.out.println("Erro ao enviar comando para TCP." + ex);
                System.exit(1);
            }
        }
    }
   
    private static void processLocalCommand(String command)
            throws IOException
    {
        if(command.equals(Properties.COMMAND_CUR_DIR_PATH))
        {
            System.out.println(current_folder);
        }
        else if(command.startsWith(Properties.COMMAND_CREATE_DIRECTORY))
        {
            String [] params = command.split(" ");
            Files.createDirectories(Paths.get(current_server_folder + 
                    current_folder + params[1]));
        }
        //
        else if(command.equals(Properties.COMMAND_LIST_CONTENT))
        {
            Path dir = Paths.get(current_server_folder + current_folder);
                
            DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
            for (Path file: stream)
            {
                System.out.println(file.getFileName().toString());
            }
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

                if (!Files.exists(Paths.get(current_server_folder + current_folder)))
                {
                    success = false;
                    break;
                }
            }
            if(!success)
            {
                System.out.println("Voce esta na pasta raiz!");
            }
            else
            {
                System.out.println("Voce moveu-se para outra pasta!");
            }
        }
        else if(command.startsWith(Properties.COMMAND_COPY_FILE))
        {
            String [] params = command.split(" ");
            
            Path source = Paths.get(current_server_folder +
                    current_folder + params[1]);
            Path newdir = Paths.get(current_server_folder +
                    current_folder + params[2] + "/" + params[1]);
            Path ret = Files.copy(source, newdir);
            
            if(ret == null)
            {
                System.out.println("Erro ao copiar ficheiro");
            }
            else
            {
                System.out.println("Ficheiro copiado");
            }
        }
        else if(command.startsWith(Properties.COMMAND_MOVE_FILE))
        {
            String [] params = command.split(" ");
            
            Path source = Paths.get(current_server_folder +
                    current_folder + params[1]);
            Path newdir = Paths.get(current_server_folder +
                    current_folder + params[2] + "/" + params[1]);
            Path ret = Files.move(source, newdir);
            
            if(ret == null)
            {
                System.out.println("Erro ao mover ficheiro");
            }
            else
            {
                System.out.println("Ficheiro movido");
            }
        }
        else if(command.startsWith(Properties.COMMAND_REMOVE_FILE))
        {
            String [] params = command.split(" ");
            
            File file = new File(current_server_folder +
                    current_folder + params[1]);
            if(!file.exists())
            {
                System.out.println("O ficheiro nao existe");
            }
            else if(file.isDirectory())
            {
                if(file.list().length == 0)
                {
                    file.delete();
                    System.out.println("Diretorio removido");
                }
                else
                {
                    System.out.println("O diretorio nao esta vazio");
                }
            }
            else
            {
                System.out.println("Ficheiro removido");
                file.delete();
            }
        }
    }
    
    private static void registerNParams()
    {
        Properties.params.put(Properties.COMMAND_REGISTER,          2);
        Properties.params.put(Properties.COMMAND_LOGIN,             2);
        Properties.params.put(Properties.COMMAND_CREATE_DIRECTORY,  1);
        Properties.params.put(Properties.COMMAND_CHANGE_DIRECTORY,  1);
        Properties.params.put(Properties.COMMAND_COPY_FILE,         2);
        Properties.params.put(Properties.COMMAND_MOVE_FILE,         2);
        Properties.params.put(Properties.COMMAND_REMOVE_FILE,       1);
        Properties.params.put(Properties.COMMAND_UPLOAD,            2);
        Properties.params.put(Properties.COMMAND_DOWNLOAD,          2);
        //
        Properties.params.put(Properties.LIST,                      1);
        Properties.params.put(Properties.MESSAGE_TO_ALL,            1);
        Properties.params.put(Properties.MESSAGE_TO_CLIENT,         2);
        Properties.params.put(Properties.CONNECT_TO_SERVER,         1);
        Properties.params.put(Properties.DISCONNECT_FROM_SERVER,    1);
        Properties.params.put(Properties.CHANGE_SERVER,             1);
        Properties.params.put(Properties.CHANGE_FOLDER,             1);
    }
    
    private static boolean checkParams(String command)
    {
        String [] ps = command.split(" ");
        if(!Properties.params.containsKey(ps[0]))
        {
            return true;
        }
        Integer nparams = Properties.params.get(ps[0]);
        return ps.length - 1 == nparams;
    }
}