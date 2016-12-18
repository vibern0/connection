
import java.io.IOException;
import java.net.SocketException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PADiretoria
{
    static String cmd;
    
    public static void main(String[] args)
    {
        if(args.length < 3)
        {
            System.out.println("Incompleto! Parametros -> " +
                    "[UDP escuta clientes] [UDP escuta servidores] [diretorio RMI]");
            return;
        }
        
        UdpClients udpClients;
        try
        {
            udpClients = new UdpClients(Integer.parseInt(args[0]));
            System.out.println("UDP de escuta para clintes iniciado no porto " +
                    Integer.parseInt(args[0]));
            udpClients.start();
        }
        catch (SocketException ex)
        {
            ex.printStackTrace();
            System.out.println("Erro ao iniciar UDP de escuta para clientes!");
            return;
        }
        
        UdpServers udpServers;
        try
        {
            udpServers = new UdpServers(Integer.parseInt(args[1]));
            System.out.println("UDP de escuta para servidores iniciado no porto " +
                    Integer.parseInt(args[1]));
            udpServers.start();
        }
        catch (SocketException ex)
        {
            ex.printStackTrace();
            System.out.println("Erro ao iniciar UDP de escuta para clientes!");
            return;
        }
        
        RMI rmi = new RMI();
        try
        {
            rmi.run(args[2]);
        }
        catch (RemoteException ex)
        {
            System.out.println("Erro remoto - " + ex);
            System.exit(1);
        }
        catch (AlreadyBoundException ex)
        {
            Logger.getLogger(PADiretoria.class.getName()).log(Level.SEVERE, null, ex);
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
