
public interface RemoteClientInterface extends java.rmi.Remote
{
    //
    public String getName() throws java.rmi.RemoteException;
    
    public void connectUser(RemoteObserverInterface user)
            throws java.rmi.RemoteException;
    public void disconnectUser(RemoteObserverInterface user)
            throws java.rmi.RemoteException;
}
