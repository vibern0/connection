import java.net.*;
import java.io.*;
import static pacliente.Properties.*;

public class UdpClient extends Thread
{
    private final DatagramSocket socket;
    private final String ip;
    private final int port;
    public UdpClient(String ip, int port) throws SocketException 
    {
        this.socket = new DatagramSocket();
        this.ip = ip;
        this.port = port;
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
    
    public void sendCommand(String command) throws IOException
    {
        //
        DatagramPacket packet;
        packet = new DatagramPacket(command.getBytes(),
                command.length(), InetAddress.getByName(ip), port);
        socket.send(packet);
    }
    
    public void finish()
    {
        socket.close();
        interrupt();
    }
  
}

