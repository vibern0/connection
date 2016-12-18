import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import static java.rmi.server.RemoteServer.getClientHost;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.List;

public class RemoteService  extends UnicastRemoteObject
        implements RemoteServiceInterface
{
    public static final String SERVICE_NAME = "GetRemoteFile";
    List<RemoteObserverInterface> observers;
    
    public RemoteService(File localDirectory) throws RemoteException 
    {
        observers = new ArrayList<>();
    }
    
    @Override
    public void connect(String serverName) throws java.rmi.RemoteException
    {
        try
        {
            notifyObservers("O servidor " + serverName +
                    " conectou-se desde " + getClientHost());
        }
        catch (ServerNotActiveException ex) { }
    }
    
    @Override
    public void disconnect(String serverName) throws java.rmi.RemoteException
    {
        notifyObservers("O servidor " + serverName + " desconectou-se!");
    }
    
    @Override
    public synchronized void addObserver(RemoteObserverInterface observer)
            throws java.rmi.RemoteException
    {
        if(!observers.contains(observer))
        {
            observers.add(observer);
            System.out.println("+ um observador.");
        }

    }
    
    @Override
    public synchronized void removeObserver(RemoteObserverInterface observer) throws java.rmi.RemoteException
    {
        if(observers.remove(observer))
            System.out.println("- um observador.");
    }
    
    public synchronized void notifyObservers(String msg)
    {
        int i;
        
        for(i=0; i < observers.size(); i++)
        {
            try
            {       
                observers.get(i).notifyNewOperationConcluded(msg);
            }
            catch(RemoteException e)
            {
                observers.remove(i--);
                System.out.println("- um observador (observador inacessivel).");
            }
        }
    }
}
