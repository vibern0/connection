
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RMI {
    
    private RemoteServiceInterface remoteService;
    private RemoteObserver observer;
    private final String username;
    public RMI(String username)
    {
        this.username = username;
    }
    public void run(String serviceLocalization)
            throws RemoteException, MalformedURLException, NotBoundException
    {	
        
        observer = new RemoteObserver(username);
        System.out.println("Servico GetRemoteFileObserver criado e em execucao...");
        String objectUrl = "rmi://"+serviceLocalization+"/GetRemoteFile";
        remoteService = (RemoteServiceInterface)Naming.lookup(objectUrl);
        remoteService.addObserver(observer);
            
    }
    
    public List<String> getAllServersName() throws RemoteException
    {
        List<String> values = new ArrayList<>();
        //
        List<RemoteClientInterface> servers = remoteService.allServersInfo();
        for(RemoteClientInterface server : servers)
        {
            values.add(server.getName());
        }
        //
        return values;
    }
    
    public List<String> getAllOnlineClients() throws RemoteException
    {
        return remoteService.getAllObserversName();
    }
    
    public void connectToServer(String serverName) throws RemoteException
    {
        RemoteClientInterface server =
                remoteService.searchServerByName(serverName);
        server.connectUser(observer);
    }
    
    public void disconnectFromServer(String serverName) throws RemoteException
    {
        RemoteClientInterface server =
                remoteService.searchServerByName(serverName);
        server.disconnectUser(observer);
    }
    
    public String getServerIP(String serverName) throws RemoteException
    {
        RemoteClientInterface server =
                remoteService.searchServerByName(serverName);
        return server.getIP();
    }
    
    public int getServerPort(String serverName)
            throws RemoteException
    {
        RemoteClientInterface server =
                remoteService.searchServerByName(serverName);
        return server.getPort();
    }
    
    public void close() throws NoSuchObjectException, RemoteException
    {
        remoteService.removeObserver(observer);
        UnicastRemoteObject.unexportObject(observer, true);
    }
}
