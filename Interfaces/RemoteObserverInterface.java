
public interface RemoteObserverInterface extends java.rmi.Remote
{
    public String getName()
            throws java.rmi.RemoteException;
    public void notifyNewOperationConcluded(String description)
            throws java.rmi.RemoteException;
}
