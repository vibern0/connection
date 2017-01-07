
import java.util.List;

public interface RemoteClientInterface extends java.rmi.Remote
{
    //
    public String getName() throws java.rmi.RemoteException;
    public String getIP() throws java.rmi.RemoteException;
    public int getPort() throws java.rmi.RemoteException;
    
    public void connectUser(RemoteObserverInterface user)
            throws java.rmi.RemoteException;
    public void disconnectUser(RemoteObserverInterface user)
            throws java.rmi.RemoteException;
    
    public void addAuthenticatedUser(RemoteObserverInterface user)
            throws java.rmi.RemoteException;
    public void removeAuthenticatedUser(RemoteObserverInterface user)
            throws java.rmi.RemoteException;
    public List<RemoteObserverInterface> getAllAuthenticatedUsers()
            throws java.rmi.RemoteException;
    
    public List<RemoteObserverInterface> getAllConnectedUsers()
            throws java.rmi.RemoteException;
}
