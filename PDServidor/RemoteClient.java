
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RemoteClient extends UnicastRemoteObject
        implements RemoteClientInterface
{
    private final String serverName;
    private final List<RemoteObserverInterface> users;
    public RemoteClient(String serverName) throws RemoteException
    {
        //
        this.serverName = serverName;
        this.users = new ArrayList<>();
    }

    @Override
    public String getName() throws RemoteException
    {
        return serverName;
    }

    @Override
    public void connectUser(RemoteObserverInterface user)
            throws RemoteException
    {
        if(!users.contains(user))
        {
            users.add(user);
            System.out.println("Utilizador " + user.getName() + " conectado!");
        }
    }

    @Override
    public void disconnectUser(RemoteObserverInterface user)
            throws RemoteException
    {
        if(users.remove(user))
        {
            System.out.println("Utilizador " + user.getName() + " desconectado!");
        }
    }
}
