
import java.net.SocketException;

public class PADiretoria
{
    static String cmd;
    
    public static void main(String[] args)
    {
        if(args.length < 1)
        {
            System.out.println("Incompleto! Parametros -> [UDP escuta]");
            return;
        }
        
        UdpClients udpClients;
        try
        {
            udpClients = new UdpClients(5008);
            System.out.println("UDP de escuta para clintes iniciado no porto " + 5008);
            udpClients.start();
        }
        catch (SocketException ex)
        {
            ex.printStackTrace();
            System.out.println("Erro ao iniciar UDP de escuta para clientes!");
            return;
        }
    }
}
