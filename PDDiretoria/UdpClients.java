
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.SECONDS;

public class UdpClients extends Thread {

    private final DatagramSocket socket;
    private final HashMap<String, ScheduledExecutorService> clientesOn;
    private final HashMap<String, String> clientsNames;
    
    public static final int MAX_DPACK_SIZE = 256;
    public static final int HEARTBEAT_TIME = 4;
    public static final int HEARTBEAT_TIMEOUT = 1;

    public UdpClients(int listeningPort) throws SocketException
    {
        this.socket = new DatagramSocket(listeningPort);
        this.clientesOn = new HashMap<>();
        this.clientsNames = new HashMap<>();
    }

    private ScheduledExecutorService addClient(String registry, String username)
    {
        System.out.println("Cliente " + registry + " conectado!");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        clientesOn.put(registry, scheduler);
        clientsNames.put(username, registry);
        return scheduler;
    }
    
    private void setSchedule(String registry, ScheduledExecutorService scheduler)
    {
        clientesOn.put(registry, scheduler);
    }
    
    private boolean removeClient(String registry, String username)
    {
        System.out.println("Cliente " + registry + " desconectado!");
        return (clientesOn.remove(registry) != null &&
                clientsNames.remove(username, registry));
    }
    
    private ScheduledExecutorService findClient(String registry)
    {
        return clientesOn.get(registry);
    }
    
    private class loose_connection implements Runnable
    {
        String registry;
        String username;
        public loose_connection(String registry, String username)
        {
            this.registry = registry;
            this.username = username;
        }
        @Override
        public void run()
        {
            removeClient(registry, username);
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
            if(input_sock.startsWith(Properties.HEARTBEAT_CLIENT))
            {
                registry = packet.getAddress().getHostAddress() + ":" + packet.getPort();
                ScheduledExecutorService schedule = findClient(registry);
                String [] tmp = input_sock.split(" ");
                if(schedule != null)
                {
                    schedule.shutdownNow();
                    schedule = Executors.newScheduledThreadPool(1);
                }
                else
                {
                    schedule = addClient(registry, tmp[1]);
                }
                schedule.schedule(
                        new loose_connection(registry, tmp[1]),
                        HEARTBEAT_TIME + HEARTBEAT_TIMEOUT, SECONDS);
                setSchedule(registry, schedule);
            }
            else if(input_sock.startsWith(Properties.MESSAGE_TO_ALL))
            {
                Set<String> clients = clientesOn.keySet();
                String [] pms;
                String username;
                
                DatagramPacket packet_user = new DatagramPacket(new byte[MAX_DPACK_SIZE], MAX_DPACK_SIZE);
                DatagramPacket packet_send;
                
                try
                {
                    socket.receive(packet_user);
                }
                catch (IOException ex)
                { }
                username = new String(packet_user.getData(), 0, packet_user.getLength());
                //
                for(String client : clients)
                {
                    try
                    {
                        pms = client.split(":");
                        packet_send = new DatagramPacket(input_sock.getBytes(),
                                input_sock.length(),
                                InetAddress.getByName(pms[0]),
                                Integer.parseInt(pms[1]));
                        socket.send(packet_send);
                        //
                        packet_send = new DatagramPacket(username.getBytes(),
                                username.length(),
                                InetAddress.getByName(pms[0]),
                                Integer.parseInt(pms[1]));
                        socket.send(packet_send);
                    }
                    catch (UnknownHostException ex)
                    { }
                    catch (IOException ex)
                    { }
                }
            }
            else if(input_sock.startsWith(Properties.MESSAGE_TO_CLIENT))
            {
                String [] pms;
                String username;
                DatagramPacket packet_user = new DatagramPacket(new byte[MAX_DPACK_SIZE], MAX_DPACK_SIZE);
                DatagramPacket packet_send;
                
                pms = input_sock.split(" ");
                if(!clientsNames.containsKey(pms[1]))
                {
                    try
                    {
                        packet_send = new DatagramPacket(Properties.MESSAGE_CLIENT_NOT_FOUND.getBytes(),
                                Properties.MESSAGE_CLIENT_NOT_FOUND.length(),
                                InetAddress.getByName(packet.getAddress().getHostName()),
                                packet.getPort());
                        socket.send(packet_send);
                    }
                    catch (UnknownHostException ex)
                    { }
                    catch (IOException ex)
                    { }
                    continue;
                }
                
                try
                {
                    socket.receive(packet_user);
                }
                catch (IOException ex)
                { }
                username = new String(packet_user.getData(), 0, packet_user.getLength());
                //
                System.out.println(pms[1]);
                pms = clientsNames.get(pms[1]).split(":");
                
                System.out.println(clientsNames.get(pms[1]));
                System.out.println(pms);
                try
                {
                    packet_send = new DatagramPacket(input_sock.getBytes(),
                            input_sock.length(),
                            InetAddress.getByName(pms[0]),
                            Integer.parseInt(pms[1]));
                    socket.send(packet_send);
                    //
                    packet_send = new DatagramPacket(username.getBytes(),
                            username.length(),
                            InetAddress.getByName(pms[0]),
                            Integer.parseInt(pms[1]));
                    socket.send(packet_send);
                }
                catch (UnknownHostException ex)
                { }
                catch (IOException ex)
                { }
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
