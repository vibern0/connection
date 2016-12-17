
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMI {
    
    private GetRemoteFileServiceInterface remoteFileService;
    private final String serverName;
    
    public RMI(String serverName)
    {
        this.serverName = serverName;
    }
    public void run(String serviceLocalization)
            throws NotBoundException, MalformedURLException, RemoteException
    {
        String objectUrl;
        objectUrl = "rmi://"+serviceLocalization+"/GetRemoteFile"; 
        remoteFileService = (GetRemoteFileServiceInterface)Naming.lookup(objectUrl);
        remoteFileService.connect(serverName);
        System.out.println("Conectado por RMI!");  
    }
    public void close() throws RemoteException
    {
        remoteFileService.disconnect(serverName);
    }
}
