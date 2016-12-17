
import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bernardovieira
 */
public class RMIservice {
    
    public RMIservice()
    { }
    public void run(String rootDirectory)
    {
        File localDirectory;
        
        /*
         * Trata os argumentos da linha de comando
         */      

        localDirectory = new File(rootDirectory.trim());

        if(!localDirectory.exists()){
           System.out.println("A directoria " + localDirectory + " nao existe!");
           return;
       }

       if(!localDirectory.isDirectory()){
           System.out.println("O caminho " + localDirectory + " nao se refere a uma directoria!");
           return;
       }

       if(!localDirectory.canRead()){
           System.out.println("Sem permissoes de leitura na directoria " + localDirectory + "!");
           return;
       }
       
       /*
        * Lanca o rmiregistry localmente no porto TCP por omissao (1099) ou, caso este ja' se encontre
        * a correr, obtem uma referencia.
        */
        try{
            
            Registry r;
            
            try{
                
                System.out.println("Tentativa de lancamento do registry no porto " + 
                                    Registry.REGISTRY_PORT + "...");
                
                r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                
                System.out.println("Registry lancado!");
                                
            }catch(RemoteException e){
                System.out.println("Registry provavelmente ja' em execucao!");
                r = LocateRegistry.getRegistry();          
            }
            
            /*
             * Cria o servico
             */            
            GetRemoteFileService fileService = new GetRemoteFileService(localDirectory);
            
            System.out.println("Servico GetRemoteFile criado e em execucao ("+fileService.getRef().remoteToString()+"...");
            
            /*
             * Regista o servico no rmiregistry local para que os clientes possam localiza'-lo, ou seja,
             * obter a sua referencia remota (endereco IP, porto de escuta, etc.).
             */
            
            r.bind(GetRemoteFileService.SERVICE_NAME, fileService);     
                   
            System.out.println("Servico " + GetRemoteFileService.SERVICE_NAME + " registado no registry...");
            
            /*
             * Para terminar um servico RMI do tipo UnicastRemoteObject:
             * 
             *  UnicastRemoteObject.unexportObject(fileService, true);
             */
            
            
        }catch(RemoteException e){
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        }catch(Exception e){
            System.out.println("Erro - " + e);
            System.exit(1);
        }
    }
}
