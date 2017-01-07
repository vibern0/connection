
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bernardovieira
 */
public class RMI
{
    private RemoteServiceInterface remoteService;
    private final RemoteMonitoring remoteMonitoring;
    
    public RMI() throws RemoteException
    {
        this.remoteMonitoring = new RemoteMonitoring();
    }
    public void run(String serviceLocalization)
            throws NotBoundException, MalformedURLException, RemoteException
    {
        String objectUrl;
        objectUrl = "rmi://"+serviceLocalization+"/GetRemoteFile"; 
        remoteService = (RemoteServiceInterface)Naming.lookup(objectUrl);
        remoteService.setMonitoringApp(remoteMonitoring);
        System.out.println("Conectado por RMI!");  
        
        showAtualInfo();
    }
    
    private void showAtualInfo() throws RemoteException
    {
        List<RemoteClientInterface> servers = remoteService.allServersInfo();
        for(RemoteClientInterface server : servers)
        {
            List<RemoteObserverInterface> users =
                    server.getAllAuthenticatedUsers();
            System.out.println("No servidor : " + server.getName());
            for(RemoteObserverInterface user : users)
            {
                System.out.println(user.getName());
            }
        }
    }
    
    public void close() throws RemoteException
    {
        remoteService.setMonitoringApp(null);
    }
}
