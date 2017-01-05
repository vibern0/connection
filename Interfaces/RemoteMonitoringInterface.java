/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bernardovieira
 */
public interface RemoteMonitoringInterface extends java.rmi.Remote
{
    public void notifyNewServer(RemoteClientInterface server)
            throws java.rmi.RemoteException;
    public void notifyNewUser(RemoteObserverInterface user,
            RemoteClientInterface server)
            throws java.rmi.RemoteException;
    
    public void notifyCloseServer(RemoteClientInterface server)
            throws java.rmi.RemoteException;
    public void notifyCloseUser(RemoteObserverInterface user,
            RemoteClientInterface server)
            throws java.rmi.RemoteException;
}
