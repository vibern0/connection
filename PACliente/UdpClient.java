import java.net.*;
import java.io.*;
import java.util.*;

public class UdpClient {

    private final int MAX_SIZE = 256;
    private final String TIME_REQUEST = "TIME";
    private final int TIMEOUT = 10; //segundos

    public UdpClient(String ip, int port) 
    {
        
        InetAddress serverAddr = null;
        int serverPort = -1;
        DatagramSocket socket = null;
        DatagramPacket packet = null;
        
        try{

            serverAddr = InetAddress.getByName(ip);
            serverPort = port;  
            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT*1000);
            
            packet = new DatagramPacket(TIME_REQUEST.getBytes(), TIME_REQUEST.length(), serverAddr,
                    serverPort);
            socket.send(packet);
            
            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            socket.receive(packet);
            
            System.out.println("Hora indicada pelo servidor: " + new String(packet.getData(), 0, packet.getLength()));
            
            //******************************************************************
            //Exemplo de como retirar os valores da mensagem
            try{
                StringTokenizer tokens = new StringTokenizer(new String(packet.getData(), 0, packet.getData().length)," :");
                        
                int hour = Integer.parseInt(tokens.nextToken().trim());
                int minute = Integer.parseInt(tokens.nextToken().trim());
                int second = Integer.parseInt(tokens.nextToken().trim());
            
                System.out.println("Horas: " + hour + " ; Minutos: " + minute + " ; Segundos: " + second);
            }catch(NumberFormatException e){}
           
            //******************************************************************
            
        }catch(UnknownHostException e){
             System.out.println("Destino desconhecido:\n\t"+e);
        }catch(NumberFormatException e){
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        }catch(SocketTimeoutException e){
            System.out.println("Nao foi recebida qualquer resposta:\n\t"+e);
        }catch(SocketException e){
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t"+e);
        }catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }finally{
            if(socket != null){
                socket.close();
            }
        }
   }
  
}

