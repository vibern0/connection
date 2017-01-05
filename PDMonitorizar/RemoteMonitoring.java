
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bernardovieira
 */
public class RemoteMonitoring extends UnicastRemoteObject
        implements RemoteMonitoringInterface
{
    
    public RemoteMonitoring() throws RemoteException
    {
        super();
    }

    @Override
    public void notifyNewServer(RemoteClientInterface server)
            throws RemoteException
    {
        System.out.println("Um novo servidor esta online : " + server.getName());
    }

    @Override
    public void notifyNewUser(RemoteObserverInterface user, RemoteClientInterface server)
            throws RemoteException
    {
        System.out.println("Um novo utilizador esta autenticado : " +
                user.getName() + " no servidor : " + server.getName());
    }

    @Override
    public void notifyCloseServer(RemoteClientInterface server)
            throws RemoteException
    {
        System.out.println("Um servidor ficou offline : " + server.getName());
    }

    @Override
    public void notifyCloseUser(RemoteObserverInterface user, RemoteClientInterface server)
            throws RemoteException
    {
        System.out.println("Um utilizador ficou offline : " +
                user.getName() + " no servidor : " + server.getName());
    }


    
}
