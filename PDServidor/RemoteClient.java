
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteClient extends UnicastRemoteObject
        implements RemoteClientInterface
{
    private final String serverName;
    private final ServerSocket serverSocket;
    private final List<RemoteObserverInterface> users;
    private final List<RemoteObserverInterface> users_auth;
    public RemoteClient(String serverName, ServerSocket serverSocket)
            throws RemoteException
    {
        //
        this.serverName = serverName;
        this.serverSocket = serverSocket;
        this.users = new ArrayList<>();
        this.users_auth = new ArrayList<>();
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

    @Override
    public void addAuthenticatedUser(RemoteObserverInterface user)
            throws RemoteException
    {
        if(!users_auth.contains(user))
        {
            users_auth.add(user);
        }
    }

    @Override
    public void removeAuthenticatedUser(RemoteObserverInterface user)
            throws RemoteException
    {
        if(users_auth.contains(user))
        {
            users_auth.remove(user);
        }
    }

    @Override
    public List<RemoteObserverInterface> getAllAuthenticatedUsers()
            throws RemoteException
    {
        return users_auth;
    }
}
