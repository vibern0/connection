
public interface RemoteServiceInterface extends java.rmi.Remote
{
    public void addServer(RemoteClientInterface server)
            throws java.rmi.RemoteException;
    public void removeServer(RemoteClientInterface server)
            throws java.rmi.RemoteException;

    public void addObserver(RemoteObserverInterface observer)
            throws java.rmi.RemoteException;
    public void removeObserver(RemoteObserverInterface observer)
            throws java.rmi.RemoteException;    
}
