
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class RemoteClient extends UnicastRemoteObject
        implements RemoteClientInterface
{
    private final String serverName;
    public RemoteClient(String serverName) throws RemoteException
    {
        //
        this.serverName = serverName;
    }

    @Override
    public String getName() throws RemoteException
    {
        return serverName;
    }
}
