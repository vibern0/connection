/**
 *
 * @author Jose'
 */
public interface GetRemoteFileClientInterface extends java.rmi.Remote
{
    
    boolean writeFileChunk(byte [] fileChunk, int nbytes) throws java.rmi.RemoteException;
    
}
