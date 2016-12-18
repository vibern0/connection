
import java.rmi.*;

public interface RemoteObserverInterface extends Remote
{
    public void notifyNewOperationConcluded(String description)
            throws RemoteException;
}
