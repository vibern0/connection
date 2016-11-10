
import java.io.IOException;
import java.net.SocketException;



public class PADiretoria
{
    public static void main(String[] args)
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
    }
}
