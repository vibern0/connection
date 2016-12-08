
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import static pacliente.Properties.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Daniel Simões
 */
public class HeartbeatClient extends Thread {

    InetAddress dirAddr = null;
    int dirPort = -1;
    DatagramSocket socket = null;
    DatagramPacket packet = null;
    private final int TIMEOUT = 10; //segundos
    private final int SLEEPTIME = 4; //segundos

    HeartbeatClient(String dirIp, int dirPort) {

        try {

            dirAddr = InetAddress.getByName(dirIp);
            this.dirPort = dirPort;

            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT * 1000);

        } catch (UnknownHostException e) {
            System.out.println("Destino desconhecido:\n\t" + e);
        } catch (SocketException e) {
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);
        }
        //finally {
//            if (socket != null) {
//                socket.close();
//            }
//        }
    }

    @Override
    public void run() {

        int contador = -1;

        try {
            while (true) {
                
                contador++;

                packet = new DatagramPacket((COMMAND_HEARTBEAT + " " + contador).getBytes(), (COMMAND_HEARTBEAT + " " + contador).length(), dirAddr,
                        dirPort);
                socket.send(packet);

                packet.setAddress(dirAddr);
                packet.setPort(dirPort);

                Thread.sleep(SLEEPTIME * 1000);

                //throw new InterruptedException();

            }
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);

        } catch (InterruptedException e) {
            System.out.println("Ocorreu um erro durante o sleep da thread" + e);

        } finally {
            if (socket != null) {
                System.out.println("fechei o socket");
                socket.close();
            }
        }
        //socket.close();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
