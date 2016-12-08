
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bernardovieira
 */


public class UdpServers extends Thread {
    
    private final List<ServerBeat> servers;
    private final int portudp;
    private final int TIMEOUT = 30000;
    private final int TIMEOUT_TOLERANCE = 2000;
    
    public UdpServers(int portudp)
    {
        servers = new ArrayList<>();
        this.portudp = portudp;
    }
    
    public List<ServerBeat> getListServers()
    {
        return servers;
    }
    
    @Override
    public void run()
    {
        try
        {
            byte[] buf = new byte[64];
            DatagramPacket dgp = new DatagramPacket(buf, buf.length);
            DatagramSocket sk;

            sk = new DatagramSocket(portudp);
            while (true)
            {
                sk.receive(dgp);
                String [] params = new String(dgp.getData(), 0, dgp.getLength()).split(" ");
                ServerBeat server = new ServerBeat(params[0], Integer.parseInt(params[1]));
                processServerList(server);
            }
        }
        catch (SocketException ex)
        {
            System.out.println("Error initializing socket!");
        }
        catch(SocketTimeoutException e)
        {
            System.out.println("Socket timeout!");
        }
        catch (IOException ex) { }
    }
    
    private void processServerList(ServerBeat server)
    {
        ServerBeat ss = serverExists(server);
        if(ss != null)
        {
            ss.notifyTimestamp();
        }
        else
        {
            Thread thread_beat = new Thread(server);
            thread_beat.start();
            servers.add(server);
            System.out.println("New server connected! " + server.getServerName());
        }
    }
    
    private ServerBeat serverExists(ServerBeat server)
    {
        for(ServerBeat s : servers)
        {
            if(s.getServerName().equals(server.getServerName()) &&
                    s.getServerPort() == server.getServerPort())
                return s;
        }
        return null;
    }
    
    class ServerBeat extends Thread {
        
        private final String name;
        private final int tcpport;
        private long last;
        
        public ServerBeat(String name, int tcpport)
        {
            this.name = name;
            this.tcpport = tcpport;
            this.last = new Date().getTime();
        }
        
        public String getServerName()
        {
            return name;
        }
        
        public int getServerPort()
        {
            return tcpport;
        }
        
        public void notifyTimestamp()
        {
            last = new Date().getTime();
        }
        
        @Override
        public void run()
        {
            long now;
            do
            {
                try
                {
                    Thread.sleep(TIMEOUT + TIMEOUT_TOLERANCE);
                } catch (InterruptedException ex) { }
                now = new Date().getTime();
                
            }while(now - last < TIMEOUT);
            
            System.out.println(now - last);
                    
            System.out.println("Server removed! " + name);
            servers.remove(this);
        }
    }
}
