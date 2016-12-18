
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;
import pacliente.Properties;

public class PDCliente
{
    private static TcpToServer tcpToServer;
    private static UdpClient udpClient;
    private static RMI rmi;
    public static void main(String[] args)
    {    
        if(args.length < 3)
        {
            System.out.println("Incompleto! Parametros -> [ip UDP] [porto UDP] [username]");
            return;
        }
        
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
        tcpToServer = null;
        //
        
        Scanner sc;
        String command;
        sc = new Scanner(System.in);
        do
        {
            System.out.print("Command:");
            command = sc.nextLine();
            processCommand(command);

        } while(!command.equals(Properties.COMMAND_DISCONNECT));
        
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
    
    public static void processCommand(String command)
    {
        if(command.equals("help"))
        {
            //mostra todos os comandos
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
                        System.out.println(name);
                    }
                }
                
                
                
                udpClient.sendCommand(command);
            }
            catch (IOException ex)
            {
                System.out.println("Erro ao enviar comando para UDP." + ex);
                System.exit(1);
            }
        }
        else
        {
            //enviar para servidor tcp
        }
    }
}