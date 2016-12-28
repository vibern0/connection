
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PDServidor
{   
    public static void main(String[] args)
    {
        if(args.length < 4)
        {
            System.out.println("Incompleto! Parametros -> [nome] [ip UDP] [porto UDP] [porto local]");
            return;
        }
        
        //check if folder exists
        File serverFolder = new File(args[0]);
        if(!serverFolder.exists())
        {
            try
            {
                Files.createDirectories(Paths.get(args[0]));
            }
            catch (IOException ex)
            {
                Logger.getLogger(PDServidor.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(1);
            }
        }
        
        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(Integer.valueOf(args[3]));
            assert serverSocket.isBound();
            if (serverSocket.isBound())
            {
                System.out.println("SERVER inbound data port " +
                    serverSocket.getLocalPort() +
                    " is ready and waiting for client to connect...");
            }
            
        }
        catch (SocketException se)
        {
            System.err.println("Unable to create socket.");
            System.err.println(se.toString());
            System.exit(1);
        }
        catch (IOException ioe)
        {
            System.err.println("Unable to read data from an open socket.");
            System.err.println(ioe.toString());
            System.exit(1);
        }
        
        Thread tcp_thread = new TcpServer(serverSocket, args[0]);
        tcp_thread.start();
        
        try
        {
            new Heartbeat(args[0], args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3])).run();
        }
        catch (SocketException ex)
        {
            System.out.println("Erro ao iniciar UDP!");
            tcp_thread.interrupt();
            try
            {
                serverSocket.close();
            } catch (IOException ex1) { }
            //
            Logger.getLogger(PDServidor.class.getName()).log(Level.SEVERE, null, ex);
            //
            return;
        }
        
        RMI rmi = null;
        try
        {
            rmi = new RMI(args[0], serverSocket);
            rmi.run(args[1]);
        }
        catch(RemoteException e)
        {
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        }
        catch(NotBoundException e)
        {
            System.out.println("Servico remoto desconhecido - " + e);
            System.exit(1);
        }
        catch(IOException e)
        {
            System.out.println("Erro E/S - " + e);
            System.exit(1);
        }
        
        System.out.println("<Enter> para terminar...");
        try
        {
            System.in.read();
        }
        catch (IOException ex) { }
        try
        {
            rmi.close();
        }
        catch (RemoteException ex)
        {
            System.out.println("Erro remoto - " + ex);
            System.exit(1);
        }
        System.exit(0);
    }
}