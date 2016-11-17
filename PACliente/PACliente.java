

public class PACliente
{
    public static void main(String[] args) throws Exception {
        
        //pergunta o nome de utilizador
        //conectar à diretoria
        //UdpClient udpclient = new UdpClient("127.0.0.1", 5007);
        //retorna ip e porto do servidor
        //conecta ao servidor
        TcpToServer tcpClient = new TcpToServer("127.0.0.1", 5007);
        //iniciar multicaste
    }
}