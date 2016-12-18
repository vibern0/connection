
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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
    
    public void close() throws NoSuchObjectException, RemoteException
    {
        remoteService.removeObserver(observer);
        UnicastRemoteObject.unexportObject(observer, true);
    }
}
