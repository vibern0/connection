import java.net.*;
import java.io.*;
import pacliente.Properties;
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
        String input, username;
        do
        {
            try
            {
                socket.receive(packet);
                input = new String(packet.getData(), 0, packet.getLength());
                if(input.startsWith(Properties.MESSAGE_TO_ALL))
                {
                    socket.receive(packet);
                    username = new String(packet.getData(), 0, packet.getLength());
                    input = input.substring(4, input.length());
                    System.out.println("Mensagem geral de " + username + " : " + input);
                }
                else if(input.startsWith(Properties.MESSAGE_TO_CLIENT))
                {
                    socket.receive(packet);
                    username = new String(packet.getData(), 0, packet.getLength());
                    input = input.substring(2, input.length());
                    System.out.println("Mensagem privada de " + username + " : " + input);
                }
                else if(input.startsWith(Properties.MESSAGE_CLIENT_NOT_FOUND))
                {
                    System.out.println("Utilizador nao encontrado!");
                }
            }
            catch (IOException ex)
            {
                System.out.println("Ligacao UDP perdida!");
                System.exit(1);
            }
        }while(true);
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

