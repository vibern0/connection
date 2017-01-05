
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class RMI {
    
    private RemoteServiceInterface remoteService;
    private final RemoteClient remoteClient;
    
    public RMI(String serverName, ServerSocket serverSocket)
            throws RemoteException
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
    public void loginUser(String username) throws RemoteException
    {
        List<RemoteObserverInterface> users = remoteClient.getAllConnectedUsers();
        for(RemoteObserverInterface user : users)
        {
            System.out.println(user.getName());
            System.out.println(username);
            if(user.getName().equals(username))
            {
                System.out.println(username);
                remoteService.loginUser(user, remoteClient);
                remoteClient.addAuthenticatedUser(user);
                break;
            }
        }
    }
    public void logoutUser(String username) throws RemoteException
    {
        List<RemoteObserverInterface> users = remoteClient.getAllConnectedUsers();
        for(RemoteObserverInterface user : users)
        {
            if(user.getName().equals(username))
            {
                remoteService.logoutUser(user, remoteClient);
                remoteClient.removeAuthenticatedUser(user);
                break;
            }
        }
    }
    public void close() throws RemoteException
    {
        remoteService.removeServer(remoteClient);
    }
}
