
import java.net.InetAddress;
import java.time.LocalDateTime;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Daniel Simões
 */
public class Clientes {
    
    InetAddress ip;
    int port;
    LocalDateTime timer;

    public Clientes(InetAddress ip, int port, LocalDateTime timer) {
        this.ip = ip;
        this.port = port;
        this.timer = timer;
        
    }

    @Override
    public String toString() {
        return "Clientes{" + "ip=" + ip + ", port=" + port + ", timer=" + timer + '}';
    }

    public void setTimer(LocalDateTime timer) {
        this.timer = timer;
    }
    
    
    
    
}
