/*
 * Exemplo de utilizacao do servico com interface remota GetRemoteFileInterface.
 * Assume-se que o servico encontra-se registado sob o nome "GetRemoteFile".
 */
import java.io.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Jose'
 */
public class GetRemoteFileClient extends UnicastRemoteObject implements GetRemoteFileClientInterface
{

    FileOutputStream fout;
    
    public GetRemoteFileClient() throws RemoteException
    {
        fout = null;
    }
    
     public boolean writeFileChunk(byte [] fileChunk, int nbytes) throws RemoteException
     {
        if(fout == null){
            System.out.println("Nao existe qualquer ficheiro aberto para escrita!");
            return false;
        }
        
        try {
            fout.write(fileChunk, 0, nbytes);
        } catch (IOException e) {
            System.out.println("Excepcao ao escrever no ficheiro: " + e);
            return false;
        }
         
        return true;
     }

    public void setFout(FileOutputStream fout) {
        this.fout = fout;
    }
}
