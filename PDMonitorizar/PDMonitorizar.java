
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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
public class PDMonitorizar {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        if(args.length < 1)
        {
            return;
        }
        
        RMI rmi = null;
        try
        {
            rmi = new RMI();
            rmi.run(args[0]);
        }
        catch (RemoteException | NotBoundException | MalformedURLException ex)
        {
            Logger.getLogger(PDMonitorizar.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        
        System.out.println("<Enter> para terminar...");
        try
        {
            System.in.read();
        }
        catch (IOException ex) { }
        try
        {
            rmi.close();
        }
        catch (RemoteException ex)
        {
            System.out.println("Erro remoto - " + ex);
            System.exit(1);
        }
        System.exit(0);
    }
}
