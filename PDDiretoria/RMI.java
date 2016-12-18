
import java.io.File;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMI
{
    
    private RemoteService fileService;
    public RMI() { }
    
    public void run(String rootDirectory)
            throws RemoteException, AccessException, AlreadyBoundException
    {
        File localDirectory;
        localDirectory = new File(rootDirectory.trim());

        if(!localDirectory.exists())
        {
            System.out.println("A directoria " + localDirectory + " nao existe!");
            return;
        }

        if(!localDirectory.isDirectory())
        {
            System.out.println("O caminho " + localDirectory + " nao se refere a uma directoria!");
            return;
        }

        if(!localDirectory.canRead())
        {
            System.out.println("Sem permissoes de leitura na directoria " + localDirectory + "!");
            return;
        }

        Registry r;

        try
        {
            System.out.println("Tentativa de lancamento do registry no porto " + 
                                Registry.REGISTRY_PORT + "...");

            r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            System.out.println("Registry lancado!");
        }
        catch(RemoteException e)
        {
            System.out.println("Registry provavelmente ja' em execucao!");
            r = LocateRegistry.getRegistry();          
        }
        
        fileService = new RemoteService(localDirectory);
        System.out.println("Servico GetRemoteFile criado e em execucao ("+
                fileService.getRef().remoteToString()+"...");

        r.bind(RemoteService.SERVICE_NAME, fileService);
        System.out.println("Servico " + RemoteService.SERVICE_NAME +
                " registado no registry...");
    }
    public void close() throws NoSuchObjectException
    {
        UnicastRemoteObject.unexportObject(fileService, true);
    }
}
