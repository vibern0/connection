
import java.rmi.*;
import java.rmi.server.*;

public class RemoteObserver extends UnicastRemoteObject implements RemoteObserverInterface
{
    private final String name;
    public RemoteObserver(String name) throws RemoteException
    {
        this.name = name;
    }

    @Override
    public void notifyNewOperationConcluded(String description) throws RemoteException
    {
        System.out.println(description);
    }

    @Override
    public String getName() throws RemoteException
    {
        return name;
    }
    
}
