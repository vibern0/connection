
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.SECONDS;
import padiretoria.Constants;
import static padiretoria.Constants.*;

public class UdpServers extends Thread {

    private final DatagramSocket socket;
    private final HashMap<String, ScheduledExecutorService> serversOn;

    public UdpServers(int listeningPort) throws SocketException
    {
        this.socket = new DatagramSocket(listeningPort);
        this.serversOn = new HashMap<>();
    }

    private ScheduledExecutorService addServer(String registry)
    {
        System.out.println("Servidor " + registry + " conectado!");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        serversOn.put(registry, scheduler);
        return scheduler;
    }
    
    private void setSchedule(String registry, ScheduledExecutorService scheduler)
    {
        serversOn.put(registry, scheduler);
    }
    
    private boolean removeServer(String registry)
    {
        System.out.println("Servidor " + registry + " desconectado!");
        return (serversOn.remove(registry) != null);
    }
    
    private ScheduledExecutorService findServer(String registry)
    {
        return serversOn.get(registry);
    }
    
    private class loose_connection implements Runnable
    {
        String registry;
        public loose_connection(String registry)
        {
            this.registry = registry;
        }
        @Override
        public void run()
        {
            removeServer(registry);
        }
    }
    
    @Override
    public void run()
    {
        String registry;
        String input_sock;
        DatagramPacket packet;
        packet = new DatagramPacket(new byte[MAX_DPACK_SIZE], MAX_DPACK_SIZE);
        
        do
        {
            try
            {
                socket.receive(packet);
            }
            catch (IOException ex) { ex.printStackTrace(); }
            input_sock = new String(packet.getData(), 0, packet.getLength());
            if(input_sock.startsWith(Constants.HEARTBEAT_SERVER))
            {
                registry = packet.getAddress().getHostAddress() + ":" + packet.getPort();
                ScheduledExecutorService schedule = findServer(registry);
                if(schedule != null)
                {
                    schedule.shutdownNow();
                    schedule = Executors.newScheduledThreadPool(1);
                }
                else
                {
                    schedule = addServer(registry);
                }
                schedule.schedule(
                        new loose_connection(registry),
                        HEARTBEAT_TIME + HEARTBEAT_TIMEOUT, SECONDS);
                setSchedule(registry, schedule);
            }
        } while(true);
    }
    
    public void closeSocket()
    {
        if (socket != null)
        {
            socket.close();
        }
    }
}
