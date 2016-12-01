
import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class UdpClients {

    public static final int MAX_SIZE = 256;
    public static final String TIME_REQUEST = "TIME";
    public static final int ADD_SECONDS = 30;

    private DatagramSocket socket;
    private DatagramPacket packet; //para receber os pedidos e enviar as respostas
    private boolean debug;

    private List<Clientes> clientesOn = Collections.synchronizedList(new ArrayList<>());
    private Clientes infoCli;

    public UdpClients(int listeningPort, boolean debug) throws SocketException {
        socket = null;
        packet = null;
        socket = new DatagramSocket(listeningPort);
        this.debug = debug;
    }

    public String waitDatagram() throws IOException {
        String request;

        if (socket == null) {
            return null;
        }

        packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);

        if (debug) {
            System.out.println("A espera de receber um packet UDP");
        }

        socket.receive(packet);
        request = new String(packet.getData(), 0, packet.getLength());

        if (debug) {
            System.out.println("--------------------------------------------");
            System.out.println("Recebido \"" + request + "\" de "
                    + packet.getAddress().getHostAddress() + ":" + packet.getPort());
        }

        return request;

    }

    public void processRequests(String request) throws IOException {
//        String receivedMsg, timeMsg;
//        Calendar calendar;        
//        
//        if(socket == null){
//            return;
//        }
//        
//        if(debug){
//            System.out.println("UDP Time Server iniciado...");
//        }
//        
//        while(true){
//            
//            receivedMsg = waitDatagram();
//            
//            if(receivedMsg == null){
//                continue;
//            }
//            
//            if(!receivedMsg.equalsIgnoreCase(TIME_REQUEST)){
//                continue;
//            }
//            
//            calendar = GregorianCalendar.getInstance();
//            timeMsg = calendar.get(GregorianCalendar.HOUR_OF_DAY)+":"+ 
//                    calendar.get(GregorianCalendar.MINUTE)+":"+calendar.get(GregorianCalendar.SECOND);
//            
//            packet.setData(timeMsg.getBytes());
//            packet.setLength(timeMsg.length());
//            
//            //O ip e porto de destino ja' se encontram definidos em packet
//            socket.send(packet);

//        }
        if (request.contains("hearbeat_cliente")) {
            System.out.println("recebi um heartbeat do cliente");

            manageClients();

            for(Clientes x : clientesOn)
                System.out.println(x);
            
//               LocalDateTime x = LocalDateTime.now().;
//               x.plusSeconds(30);
        } else {
            System.out.println("recebi outra coisa qq");
        }

    }

    public void manageClients() {

        infoCli = new Clientes(packet.getAddress(), packet.getPort(),
                LocalDateTime.now().plusSeconds(ADD_SECONDS));

//        if (clientesOn.isEmpty()) {
//            clientesOn.add(infoCli);
//        } else {
        synchronized (clientesOn) {

            boolean existeCli = false;

            for (Clientes x : clientesOn) {
                if (x.ip.equals(infoCli.ip)) { //se for igual, adiciona tempo
                    x.setTimer(LocalDateTime.now().plusSeconds(ADD_SECONDS));
                    //x.timer.plusSeconds(ADD_SECONDS);
                    System.out.println("timer do x: " + x.timer);
                    existeCli = true;
                    break;
                    //clientesOn.add(infoCli);
                }

            }

            if (!existeCli) {
                clientesOn.add(infoCli);
            }
        }
        //}
    }

    public void closeSocket() {
        if (socket != null) {
            socket.close();
        }
    }

}
