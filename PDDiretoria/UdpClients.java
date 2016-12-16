
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.SECONDS;
import padiretoria.Constants;
import static padiretoria.Constants.*;

public class UdpClients extends Thread {

    private final DatagramSocket socket;
    private final HashMap<String, ScheduledExecutorService> clientesOn;

    public UdpClients(int listeningPort) throws SocketException
    {
        this.socket = new DatagramSocket(listeningPort);
        this.clientesOn = new HashMap<>();
    }

    private ScheduledExecutorService addClient(String registry)
    {
        System.out.println("Cliente " + registry + " conectado!");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        clientesOn.put(registry, scheduler);
        return scheduler;
    }
    
    private void setSchedule(String registry, ScheduledExecutorService scheduler)
    {
        clientesOn.put(registry, scheduler);
    }
    
    private boolean removeClient(String registry)
    {
        System.out.println("Cliente " + registry + " desconectado!");
        return (clientesOn.remove(registry) != null);
    }
    
    private ScheduledExecutorService findClient(String registry)
    {
        return clientesOn.get(registry);
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
            removeClient(registry);
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
            if(input_sock.startsWith(Constants.HEARTBEAT_CLIENT))
            {
                registry = packet.getAddress().getHostAddress() + ":" + packet.getPort();
                ScheduledExecutorService schedule = findClient(registry);
                if(schedule != null)
                {
                    schedule.shutdownNow();
                    schedule = Executors.newScheduledThreadPool(1);
                }
                else
                {
                    schedule = addClient(registry);
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
