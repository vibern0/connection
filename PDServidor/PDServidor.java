
import java.io.IOException;
import java.net.SocketException;
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
        
        //TcpServer tcpServer = new TcpServer(Integer.parseInt(args[3]));
        
        try
        {
            new Heartbeat(args[0], args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3])).run();
        }
        catch (SocketException ex)
        {
            ex.printStackTrace();
            System.out.println("Erro ao iniciar UDP!");
            //fechar tcp!
            return;
        }
        
        RMI rmi = new RMI(args[0]);
        try
        {
            rmi.run(args[1]);
        }
        catch(RemoteException e)
        {
            System.out.println("Erro remoto - " + e);
        }
        catch(NotBoundException e)
        {
            System.out.println("Servico remoto desconhecido - " + e);
        }
        catch(IOException e)
        {
            System.out.println("Erro E/S - " + e);
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
        }
        System.exit(0);
    }
}