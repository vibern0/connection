
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bernardovieira
 */
public class MulticastCC {
    
    public MulticastCC()
    {
        try
        {
            int mcPort = 12345;
            String mcIPStr = "230.1.1.1";
            //
            MulticastSocket mcSocket;
            InetAddress mcIPAddress;
            mcIPAddress = InetAddress.getByName(mcIPStr);
            mcSocket = new MulticastSocket(mcPort);
            System.out.println("Multicast Receiver running at:" + mcSocket.getLocalSocketAddress());
            mcSocket.joinGroup(mcIPAddress);
            
            ChatReceiver chatreceiver = new ChatReceiver(mcSocket);
            Thread threadchatreceiver = new Thread(chatreceiver);
            threadchatreceiver.start();
            
            String msg_sent = "";
            Scanner sc;
            do
            {
                sc = new Scanner(System.in);
                System.out.print("Digite um texto:");
                
                msg_sent = sc.nextLine();
                byte[] msgs = msg_sent.getBytes();
                DatagramPacket packet;
                
                packet = new DatagramPacket(msgs, msgs.length);
                packet.setAddress(mcIPAddress);
                packet.setPort(mcPort);
                mcSocket.send(packet);
                
            } while(!msg_sent.equals("close"));
            
            System.out.println("abc");
            threadchatreceiver.join();
            System.out.println("abc");
            
            mcSocket.leaveGroup(mcIPAddress);
            mcSocket.close();
        }
        catch (IOException | InterruptedException ex)
        {
            Logger.getLogger(MulticastCC.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
