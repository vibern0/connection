
import java.net.SocketException;

public class PADiretoria
{
    static String cmd;
    
    public static void main(String[] args)
    {
        if(args.length < 1)
        {
            System.out.println("Incompleto! Parametros -> [UDP escuta clientes] [UDP escuta servidores]");
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
        
        new RMIservice().run("./");
    }
}
