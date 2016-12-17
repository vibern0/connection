import java.rmi.*;
import java.rmi.server.*;

/**
 *
 * @author Jose'
 */
public class GetRemoteFileObserver extends UnicastRemoteObject implements GetRemoteFileObserverInterface
{
    
    public GetRemoteFileObserver() throws RemoteException {}

    @Override
    public void notifyNewOperationConcluded(String description) throws RemoteException
    {
        System.out.print(description);
    }
    
}
