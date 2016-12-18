
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PDCliente
{
    public static void main(String[] args){
        
        if(args.length < 2)
        {
            System.out.println("Incompleto! Parametros -> [ip UDP] [porto UDP]");
            return;
        }
        
        UdpClient udpClient;
        try
        {
            udpClient = new UdpClient();
            System.out.println("UDP iniciado no porto " + udpClient.getPort());
            udpClient.start();
        }
        catch (SocketException ex)
        {
            ex.printStackTrace();
            System.out.println("Erro ao iniciar UDP!");
            return;
        }
        
        new HeartbeatClient(udpClient.getSocket(), args[0], Integer.parseInt(args[1])).run();
        
        RMI rmi = new RMI();
        try
        {
            rmi.run(args[0]);
        }
        catch(RemoteException e)
        {
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        }
        catch (MalformedURLException | NotBoundException ex)
        {
            Logger.getLogger(PDCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("<Enter> para terminar...");
        System.out.println();
        try
        {
            System.in.read();
        }
        catch (IOException ex)
        {
            Logger.getLogger(PDCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try
        {
            rmi.close();
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(PDCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}