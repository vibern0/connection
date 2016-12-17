import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import static java.rmi.server.RemoteServer.getClientHost;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jose'
 */
public class GetRemoteFileService  extends UnicastRemoteObject implements GetRemoteFileServiceInterface
{
    public static final String SERVICE_NAME = "GetRemoteFile";
    public static final int MAX_CHUNCK_SIZE = 10000; //bytes
    
    List<GetRemoteFileObserverInterface> observers;
    
    protected File localDirectory;    
    
    public GetRemoteFileService(File localDirectory) throws RemoteException 
    {
        this.localDirectory = localDirectory;
        observers = new ArrayList<>();
    }
    
    public byte [] getFileChunk(String fileName, long offset) throws RemoteException
    {
        String requestedCanonicalFilePath = null;
        FileInputStream requestedFileInputStream = null;
        byte [] fileChunck = new byte[MAX_CHUNCK_SIZE];
        int nbytes;        
        
        fileName = fileName.trim();
        //System.out.println("Recebido pedido para: " + fileName);
        
        try{

            /*
             * Verifica se o ficheiro solicitado existe e encontra-se por baixo da localDirectory 
             */
            requestedCanonicalFilePath = new File(localDirectory+File.separator+fileName).getCanonicalPath();

            if(!requestedCanonicalFilePath.startsWith(localDirectory.getCanonicalPath()+File.separator)){
                System.out.println("Nao e' permitido aceder ao ficheiro " + requestedCanonicalFilePath + "!");
                System.out.println("A directoria de base nao corresponde a " + localDirectory.getCanonicalPath()+"!");
                return null;
            }

            /*
             * Abre o ficheiro solicitado para leitura.
             */
            requestedFileInputStream = new FileInputStream(requestedCanonicalFilePath);            
            //System.out.println("Ficheiro " + requestedCanonicalFilePath + " aberto para leitura.");

            /*
             * Obtem um bloco de bytes do ficheiro, omitindo os primeiros offset bytes.
             */
            requestedFileInputStream.skip(offset);
            nbytes = requestedFileInputStream.read(fileChunck);

            if(nbytes == -1){//EOF
                return null;
            }

            /*
             * Se fileChunk nao esta' totalmente preenchido (MAX_CHUNCK_SIZE), recorre-se
             * a um array auxiliar com tamanho correspondente ao numero de bytes efectivamente lidos.
             */
            if(nbytes < fileChunck.length){
                
                byte [] aux = new byte[nbytes];
                System.arraycopy(aux, 0, fileChunck, 0, nbytes);
                
                notifyObservers("Devolvido um bloco do ficheiro " + requestedCanonicalFilePath + " com " + nbytes + " bytes.\n\n"); 
                
                return aux;
                
            }
                
            notifyObservers("Devolvido um bloco do ficheiro " + requestedCanonicalFilePath + " com " + nbytes + " bytes.\n\n");
            
            return fileChunck;
            
        }catch(FileNotFoundException e){   //Subclasse de IOException                 
            System.out.println("Ocorreu a excepcao {" + e + "} ao tentar abrir o ficheiro " + requestedCanonicalFilePath + "!");            
            notifyObservers("Ocorreu um problema ao tentar abrir o ficheiro " + requestedCanonicalFilePath + ".\n\n");
            
        }catch(IOException e){
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);            
            notifyObservers("Ocorreu um problema ao tentar aceder ao ficheiro " + requestedCanonicalFilePath + ".\n\n");
            
        }finally{
            if(requestedFileInputStream != null){
                try {
                    requestedFileInputStream.close();
                } catch (IOException e) {}
            }
        }
        
        return null;
    }
    
    public boolean getFile(String fileName, GetRemoteFileClientInterface cliRemoto) throws RemoteException
    {
        String requestedCanonicalFilePath = null;
        FileInputStream requestedFileInputStream = null;
        byte [] fileChunck = new byte[MAX_CHUNCK_SIZE];
        int nbytes;
        
        fileName = fileName.trim();
        System.out.println("Recebido pedido para: " + fileName + ".");
        
        try{
            System.out.println("Origem do pedido: " + getClientHost()+ ".");
        }catch(ServerNotActiveException e){}
        
        System.out.println();
        
        try{

            /*
             * Verifica se o ficheiro solicitado existe e encontra-se por baixo da localDirectory.
             */
            
            requestedCanonicalFilePath = new File(localDirectory+File.separator+fileName).getCanonicalPath();

            if(!requestedCanonicalFilePath.startsWith(localDirectory.getCanonicalPath()+File.separator)){
                System.out.println("Nao e' permitido aceder ao ficheiro " + requestedCanonicalFilePath + "!");
                System.out.println("A directoria de base nao corresponde a " + localDirectory.getCanonicalPath()+"!");
                
                notifyObservers("Solicitado ficheiro nao permitido: " + requestedCanonicalFilePath);                    
                try{
                    notifyObservers(" por um cliente em " + getClientHost());
                }catch(ServerNotActiveException e){}
                notifyObservers(".\n\n");
                
                return false;
            }

            /*
             * Abre o ficheiro solicitado para leitura.
             */
            requestedFileInputStream = new FileInputStream(requestedCanonicalFilePath);            
            System.out.println("Ficheiro " + requestedCanonicalFilePath + " aberto para leitura.");
            
            /*
             * Obtem os bytes do ficheiro por blocos de bytes.
             */
            while((nbytes = requestedFileInputStream.read(fileChunck))!=-1){                         

                /*
                 * Escreve o bloco actual no cliente, invocando o metodo writeFileChunk da sua interface remota.
                 */
                if(!cliRemoto.writeFileChunk(fileChunck, nbytes)){
                    System.out.print("Surgiu um problema ao tentar escrever um bloco do ficheiro " + requestedCanonicalFilePath);
                    System.out.println(" com " + nbytes + " bytes no cliente!");

                    notifyObservers("Surgiu um problema ao tentar escrever um bloco do ficheiro solicitado " + requestedCanonicalFilePath);                                        
                    try{
                        notifyObservers(" num cliente em " + getClientHost());
                    }catch(ServerNotActiveException ex){}
                    notifyObservers(".\n\n");                
                    
                    return false;
                }                    
                
            }
                
            System.out.println("Ficheiro " + requestedCanonicalFilePath + " transferido para o cliente com sucesso.");
            System.out.println();
            
            notifyObservers("Ficheiro " + requestedCanonicalFilePath + " depositado com sucesso");                    
            try{
                notifyObservers(" num cliente em " + getClientHost());
            }catch(ServerNotActiveException e){}
            notifyObservers(".\n\n");
                    
            return true;
            
        }catch(FileNotFoundException e){   //Subclasse de IOException                 
            System.out.println("Ocorreu a excepcao {" + e + "} ao tentar abrir o ficheiro " + requestedCanonicalFilePath + "!"); 
                        
            notifyObservers("Nao foi possivel abrir o ficheiro " + requestedCanonicalFilePath + " solicitado");                    
            try{
                notifyObservers(" por um cliente em " + getClientHost());
            }catch(ServerNotActiveException ex){}
            notifyObservers(".\n\n");            
            
        }catch(IOException e){
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);
            
            notifyObservers("Ocorreu um problema ao ler o ficheiro " + requestedCanonicalFilePath + " solicitado");                    
            try{
                notifyObservers(" por um cliente em " + getClientHost());
            }catch(ServerNotActiveException ex){}
            notifyObservers(".\n\n");
            
        }finally{
            if(requestedFileInputStream != null){
                try {
                    requestedFileInputStream.close();
                } catch (IOException e) {}
            }
        }
        
        return false;
    }
    
        
    public synchronized void addObserver(GetRemoteFileObserverInterface observer) throws java.rmi.RemoteException
    {
        if(!observers.contains(observer)){
            observers.add(observer);
            System.out.println("+ um observador.");
        }

    }
    
    public synchronized void removeObserver(GetRemoteFileObserverInterface observer) throws java.rmi.RemoteException
    {
        if(observers.remove(observer))
            System.out.println("- um observador.");
    }
    
    public synchronized void notifyObservers(String msg)
    {
        int i;
        
        for(i=0; i < observers.size(); i++){
            try{       
                observers.get(i).notifyNewOperationConcluded(msg);
            }catch(RemoteException e){
                observers.remove(i--);
                System.out.println("- um observador (observador inacessivel).");
            }
        }
    }
    
    /*
     * Lanca e regista um servico com interface remota do tipo GetRemoteFileInterface
     * sob o nome dado pelo atributo estatico SERVICE_NAME.
     */  
}
