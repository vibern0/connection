
public interface RemoteServiceInterface extends java.rmi.Remote
{
    public void connect(String serverName)
            throws java.rmi.RemoteException;
    public void disconnect(String serverName)
            throws java.rmi.RemoteException;

    public void addObserver(RemoteObserverInterface observer)
            throws java.rmi.RemoteException;
    public void removeObserver(RemoteObserverInterface observer)
            throws java.rmi.RemoteException;    
}
