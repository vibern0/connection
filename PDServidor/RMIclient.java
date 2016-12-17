
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
    public void run(String serviceLocalization, String rootDiectory, String wantedFile)
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
        localDirectory = new File(rootDiectory.trim());
        fileName = wantedFile.trim();
                
        if(!localDirectory.exists()){
            System.out.println("A directoria " + localDirectory + " nao existe!");
            return;
        }
        
        if(!localDirectory.isDirectory()){
            System.out.println("O caminho " + localDirectory + " nao se refere a uma directoria!");
            return;
        }
        if(!localDirectory.canWrite()){
            System.out.println("Sem permissoes de escrita na directoria " + localDirectory);
            return;
        }
               
        try{
            
            /*
             * Cria o ficheiro local
             */ 
            localFilePath = new File(localDirectory.getPath()+File.separator+fileName).getCanonicalPath();
            localFileOutputStream = new FileOutputStream(localFilePath);
            
            System.out.println("Ficheiro " + localFilePath + " criado.");
                        
            /*
             * Obtem a referencia remota para o servico com nome "GetRemoteFile"
             */
            remoteFileService = (GetRemoteFileServiceInterface)Naming.lookup(objectUrl);
            
            /*
             * Lanca o servico local para acesso remoto por parte do servidor.
             */
            myRemoteService = new GetRemoteFileClient();
            
            /*
             * Passa ao servico local uma referencia para o objecto localFileOutputStream
             */
            myRemoteService.setFout(localFileOutputStream);
            
            /*
             * Obtem o ficheiro pretendido, invocando o metodo getFile no servico remoto.
             */            
            if(remoteFileService.getFile(fileName, myRemoteService)){
                System.out.println("Transferencia do ficheiro " + fileName + " concluida com sucesso.");
            }else{
                System.out.println("Transferencia do ficheiro " + fileName + " concluida SEM sucesso.");
            }            
                        
        }catch(RemoteException e){
            System.out.println("Erro remoto - " + e);
        }catch(NotBoundException e){
            System.out.println("Servico remoto desconhecido - " + e);
        }catch(IOException e){
            System.out.println("Erro E/S - " + e);
        }catch(Exception e){
            System.out.println("Erro - " + e);
        }finally{
            if(localFileOutputStream != null){
                /*
                 * Encerra o ficheiro.
                 */
                try{
                    localFileOutputStream.close();
                }catch(IOException e){}
            }
            
            if(myRemoteService != null){
                /*
                  * Retira do servico local a referencia para o objecto localFileOutputStream
                  */
                myRemoteService.setFout(null);
                /*
                 * Termina o servi�o local
                 */
                try{
                    UnicastRemoteObject.unexportObject(myRemoteService, true);
                }catch(NoSuchObjectException e){}
            }
        }
    }
}
