

public class PAServidor
{
    public static void main(String[] args)
    {
        Thread doido = new Thread(new Beat("jaquim", 5009, 5050));
        doido.start();
        TcpServer tcpServer = new TcpServer(5007);
    }
}