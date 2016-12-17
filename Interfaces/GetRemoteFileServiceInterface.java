/**
 *
 * @author Jose'
 */
public interface GetRemoteFileServiceInterface extends java.rmi.Remote
{
    //public byte [] getFileChunk(String fileName, long offset) throws java.rmi.RemoteException;
    //public boolean getFile(String fileName, GetRemoteFileClientInterface cli) throws java.rmi.RemoteException;
    
    public void connect(String serverName) throws java.rmi.RemoteException;
    
    public void addObserver(GetRemoteFileObserverInterface observer) throws java.rmi.RemoteException;
    public void removeObserver(GetRemoteFileObserverInterface observer) throws java.rmi.RemoteException;    
}
