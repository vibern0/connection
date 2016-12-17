
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        
        RMIclient rmi = new RMIclient(args[0]);
        rmi.run(args[1]);
        
        System.out.println("<Enter> para terminar...");
        try
        {
            System.in.read();
        }
        catch (IOException ex) { }
        rmi.close();
        System.exit(0);
    }
}