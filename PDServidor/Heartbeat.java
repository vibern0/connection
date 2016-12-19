
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.logging.Level;
import java.util.logging.Logger;
import static paservidor.Properties.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bernardovieira
 */
public class Heartbeat {
    
    private final String name;
    private final String udpip;
    private final int udpport;
    private final int tcpport;
    private final DatagramSocket socket;
    private final ScheduledExecutorService scheduler;
    public Heartbeat(String name, String udpip, int udpport, int tcpport) throws SocketException
    {
        this.name = name;
        this.udpip = udpip;
        this.udpport = udpport;
        this.tcpport = tcpport;
        this.socket = new DatagramSocket();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    
    public void run()
    {
        final Runnable heartbeat = new Runnable()
        {
            @Override
            public void run()
            {
                DatagramPacket packet;
                try
                {
                    packet = new DatagramPacket(COMMAND_HEARTBEAT.getBytes(),
                            COMMAND_HEARTBEAT.length(),
                            InetAddress.getByName(udpip), udpport);
                    socket.send(packet);
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    System.out.println("Erro ao enviar packet UDP!");
                    scheduler.shutdownNow();
                }
            }
        };
        scheduler.scheduleAtFixedRate(heartbeat, 0, HEARTBEAT_TIME, SECONDS);
    }
}
