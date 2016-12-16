/*
*
*
*
*/

public class PACliente
{
    public static void main(String[] args){
        
        
        //TcpToServer tcpClient = new TcpToServer("127.0.0.1", 5007);
        
        Thread hb = new HeartbeatClient("localhost", 5008);
        
        hb.start();
        
    }
}