
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
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
public class ChatReceiver implements Runnable {
    
    private final MulticastSocket socket;
    
    public ChatReceiver(MulticastSocket socket)
    {
        this.socket = socket;
    }
    @Override
    public void run ()
    {
        String msg_received = "";
        do
        {
            try
            {
                DatagramPacket packet;
                packet = new DatagramPacket(new byte[1024], 1024);
                socket.receive(packet);
                msg_received = new String(packet.getData(), packet.getOffset(), packet.getLength());
                //
                System.out.println("[Multicast  Receiver] Received:" + msg_received);
            }
            catch (IOException ex)
            {
                Logger.getLogger(ChatReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }while(!msg_received.equals("close"));
    }
}
