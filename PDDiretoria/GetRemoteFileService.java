import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import static java.rmi.server.RemoteServer.getClientHost;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jose'
 */
public class GetRemoteFileService  extends UnicastRemoteObject implements GetRemoteFileServiceInterface
{
    public static final String SERVICE_NAME = "GetRemoteFile";
    
    List<GetRemoteFileObserverInterface> observers;
    
    protected File localDirectory;    
    
    public GetRemoteFileService(File localDirectory) throws RemoteException 
    {
        this.localDirectory = localDirectory;
        observers = new ArrayList<>();
    }
    
    @Override
    public void connect(String serverName) throws java.rmi.RemoteException
    {
        try
        {
            notifyObservers("O servidor " + serverName + " conectou-se desde " + getClientHost());
        }
        catch (ServerNotActiveException ex)
        {
            Logger.getLogger(GetRemoteFileService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void disconnect(String serverName) throws java.rmi.RemoteException
    {
        notifyObservers("O servidor " + serverName + " desconectou-se!");
    }
    
    @Override
    public synchronized void addObserver(GetRemoteFileObserverInterface observer) throws java.rmi.RemoteException
    {
        if(!observers.contains(observer)){
            observers.add(observer);
            System.out.println("+ um observador.");
        }

    }
    
    @Override
    public synchronized void removeObserver(GetRemoteFileObserverInterface observer) throws java.rmi.RemoteException
    {
        if(observers.remove(observer))
            System.out.println("- um observador.");
    }
    
    public synchronized void notifyObservers(String msg)
    {
        int i;
        
        for(i=0; i < observers.size(); i++){
            try{       
                observers.get(i).notifyNewOperationConcluded(msg);
            }catch(RemoteException e){
                observers.remove(i--);
                System.out.println("- um observador (observador inacessivel).");
            }
        }
    }
    
    /*
     * Lanca e regista um servico com interface remota do tipo GetRemoteFileInterface
     * sob o nome dado pelo atributo estatico SERVICE_NAME.
     */  
}
