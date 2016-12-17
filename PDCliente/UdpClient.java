import java.net.*;
import java.io.*;
import static pacliente.Properties.*;

public class UdpClient extends Thread {

    private final DatagramSocket socket;
    public UdpClient() throws SocketException 
    {
        socket = new DatagramSocket();
    }
    
    public DatagramSocket getSocket()
    {
        return socket;
    }
    
    public int getPort()
    {
        return socket.getPort();
    }
    
    @Override
    public void run()
    {
        DatagramPacket packet;
        packet = new DatagramPacket(new byte[MAX_DPACK_SIZE], MAX_DPACK_SIZE);
        try
        {
            socket.receive(packet);
        } catch (IOException ex) { ex.printStackTrace(); }
        
        //do shit
    }
  
}

