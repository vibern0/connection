
import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.SECONDS;
import static pacliente.Properties.*;


public class HeartbeatClient
{
    private final String ip;
    private final int port;
    private final DatagramSocket socket;
    private final ScheduledExecutorService scheduler;
    public HeartbeatClient(DatagramSocket socket, String ip, int port)
    {
        this.socket = socket;
        this.ip = ip;
        this.port = port;
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
                            COMMAND_HEARTBEAT.length(), InetAddress.getByName(ip), port);
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
