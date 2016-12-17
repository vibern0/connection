
import java.net.SocketException;

public class PAServidor
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
        
        new RMIclient().run(args[1], args[0]);
    }
}