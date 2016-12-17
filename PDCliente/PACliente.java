
import java.net.SocketException;

public class PACliente
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
        
        new RMIobserver().run(args[0]);
        
    }
}