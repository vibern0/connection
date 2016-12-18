
import java.rmi.*;
import java.rmi.server.*;

public class RemoteObserver extends UnicastRemoteObject implements RemoteObserverInterface
{
    
    public RemoteObserver() throws RemoteException {}

    @Override
    public void notifyNewOperationConcluded(String description) throws RemoteException
    {
        System.out.println(description);
    }
    
}
