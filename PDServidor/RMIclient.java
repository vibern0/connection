
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
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
public class RMIclient {
    
    public RMIclient()
    { }
    public void run(String serviceLocalization, String serverName)
    {
        
        String objectUrl;        
        File localDirectory;
        String fileName;                
        
        String localFilePath;
        FileOutputStream localFileOutputStream = null;     
        
        GetRemoteFileClient myRemoteService = null;
        GetRemoteFileServiceInterface remoteFileService;
               
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
        }catch(Exception e){
            System.out.println("Erro - " + e);
        }finally{
            
            /*if(myRemoteService != null){
                myRemoteService.setFout(null);
                try{
                    UnicastRemoteObject.unexportObject(myRemoteService, true);
                }catch(NoSuchObjectException e){}
            }*/
        }
    }
}
