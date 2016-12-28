
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMI {
    
    private RemoteServiceInterface remoteService;
    private final RemoteClient remoteClient;
    
    public RMI(String serverName, ServerSocket serverSocket) throws RemoteException
    {
        this.remoteClient = new RemoteClient(serverName, serverSocket);
    }
    public void run(String serviceLocalization)
            throws NotBoundException, MalformedURLException, RemoteException
    {
        String objectUrl;
        objectUrl = "rmi://"+serviceLocalization+"/GetRemoteFile"; 
        remoteService = (RemoteServiceInterface)Naming.lookup(objectUrl);
        remoteService.addServer(remoteClient);
        System.out.println("Conectado por RMI!");  
    }
    public void close() throws RemoteException
    {
        remoteService.removeServer(remoteClient);
    }
}
