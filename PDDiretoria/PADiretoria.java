
import java.io.IOException;
import java.net.SocketException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PADiretoria
{
    static String cmd;
    
    public static void main(String[] args) throws Exception {
        
        Thread thread_servers = new Thread(new UdpServers(5009));
        thread_servers.start();
        
//        int mcPort = 12345;
//        String mcIPStr = "230.1.1.1";
//        DatagramSocket udpSocket = new DatagramSocket();
//
//        InetAddress mcIPAddress = InetAddress.getByName(mcIPStr);
//        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
//        udpSocket.receive(packet);
//        byte[] msg = packet.getData();
//        packet = new DatagramPacket(msg, msg.length);
//        packet.setAddress(mcIPAddress);
//        packet.setPort(mcPort);
//        udpSocket.send(packet);
//
//        System.out.println("Sent a  multicast message.");
//        System.out.println("Exiting application");
//        udpSocket.close();
        
        UdpClients udpS = new UdpClients(6000, true);
         
        
        while(true){
           udpS.processRequests(udpS.waitDatagram());
//           
//           if(cmd.equals("hearbeat_cliente")){
//               System.out.println("recebi um heartbeat do cliente");
//           }
//           else{
//               System.out.println("recebi outra coisa qq");
//           }
        }
        
      }
    
    /*public static void main(String[] args)
    {
        UdpServer udpserver = null;
        
        try{
            
            udpserver = new UdpServer(5007, true);         
            udpserver.processRequests();
            
        }catch(NumberFormatException e){
            System.out.println("O porto de escuta deve ser um inteiro positivo.");
        }catch(SocketException e){
            System.out.println("Ocorreu um erro ao nível do socket UDP:\n\t"+e);
        }catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }finally{
            if(udpserver != null){
                udpserver.closeSocket();
            }
        }
    }*/
}