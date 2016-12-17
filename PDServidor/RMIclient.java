
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bernardovieira
 */
public class RMIclient {
    
    private GetRemoteFileServiceInterface remoteFileService;
    private final String serverName;
    
    public RMIclient(String serverName)
    {
        this.serverName = serverName;
    }
    public void run(String serviceLocalization)
    {
        
        String objectUrl;        
        
               
        /*
         * Trata os argumentos da linha de comando 
         */        

        objectUrl = "rmi://"+serviceLocalization+"/GetRemoteFile";   
        
        try{
                 
            /*
             * Obtem a referencia remota para o servico com nome "GetRemoteFile"
             */
            remoteFileService = (GetRemoteFileServiceInterface)Naming.lookup(objectUrl);
            
            /*
             * Lanca o servico local para acesso remoto por parte do servidor.
             */
            //myRemoteService = new GetRemoteFileClient();
            
            /*
             * Passa ao servico local uma referencia para o objecto localFileOutputStream
             */
            //myRemoteService.setFout(localFileOutputStream);
            
            /*
             * Obtem o ficheiro pretendido, invocando o metodo getFile no servico remoto.
             */            
            remoteFileService.connect(serverName);
            System.out.println("Conectado por RMI!");
                        
        }catch(RemoteException e){
            System.out.println("Erro remoto - " + e);
        }catch(NotBoundException e){
            System.out.println("Servico remoto desconhecido - " + e);
        }catch(IOException e){
            System.out.println("Erro E/S - " + e);
        }finally{
            
            /*if(myRemoteService != null){
                myRemoteService.setFout(null);
                try{
                    UnicastRemoteObject.unexportObject(myRemoteService, true);
                }catch(NoSuchObjectException e){}
            }*/
        }
    }
    public void close()
    {
        try {
            remoteFileService.disconnect(serverName);
        } catch (RemoteException ex) {
            Logger.getLogger(RMIclient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
