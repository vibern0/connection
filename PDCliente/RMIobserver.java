
import java.rmi.Naming;
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
public class RMIobserver {
    
    public RMIobserver()
    { }
    public void run(String serviceLocalization)
    {
        try{
 			
            //Cria e lanca o servico 
            GetRemoteFileObserver observer = new GetRemoteFileObserver();
            System.out.println("Servico GetRemoteFileObserver criado e em execucao...");
            
            //Localiza o servico remoto nomeado "GetRemoteFile"
            String objectUrl = "rmi://"+serviceLocalization+"/GetRemoteFile"; 
                            
            RemoteServiceInterface getRemoteFileService = (RemoteServiceInterface)Naming.lookup(objectUrl);
            
            //adiciona observador no servico remoto
            getRemoteFileService.addObserver(observer);
            
            System.out.println("<Enter> para terminar...");
            System.out.println();
            System.in.read();
            
            getRemoteFileService.removeObserver(observer);
            UnicastRemoteObject.unexportObject(observer, true);
            
        }catch(RemoteException e){
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        }catch(Exception e){
            System.out.println("Erro - " + e);
            System.exit(1);
        } 
    }
}
