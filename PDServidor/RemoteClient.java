
import java.net.ServerSocket;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RemoteClient extends UnicastRemoteObject
        implements RemoteClientInterface
{
    private final String serverName;
    private final ServerSocket serverSocket;
    private final List<RemoteObserverInterface> users;
    public RemoteClient(String serverName, ServerSocket serverSocket) throws RemoteException
    {
        //
        this.serverName = serverName;
        this.serverSocket = serverSocket;
        this.users = new ArrayList<>();
    }

    @Override
    public String getName() throws RemoteException
    {
        return serverName;
    }
    
    @Override
    public String getIP() throws RemoteException
    {
        return serverSocket.getInetAddress().getHostAddress();
    }
    
    @Override
    public int getPort() throws RemoteException
    {
        return serverSocket.getLocalPort();
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

    @Override
    public List<RemoteObserverInterface> getAllConnectedUsers()
            throws RemoteException
    {
        return users;
    }
}
