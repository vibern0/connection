
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
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
public class Beat extends Thread {
    
    private final String name;
    private final int udpport;
    private final int tcpport;
    private final int TIMEOUT = 30000;
    
    public Beat(String name, int udpport, int tcpport)
    {
        this.name = name;
        this.udpport = udpport;
        this.tcpport = tcpport;
    }
    
    @Override
    public void run()
    {
        try
        {
            DatagramSocket s = new DatagramSocket();
            byte[] buf = new byte[64];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            
            InetAddress hostAddress = InetAddress.getByName("localhost");
            DatagramPacket out;
            
            while (true)
            {
                String outMessage = name + " " + tcpport;
                buf = outMessage.getBytes();

                out = new DatagramPacket(buf, buf.length, hostAddress, udpport);
                s.send(out);
                Thread.sleep(TIMEOUT);
            }
        }
        catch (SocketException | UnknownHostException ex)
        {
            Logger.getLogger(Beat.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException | InterruptedException ex) { }
    }
}
