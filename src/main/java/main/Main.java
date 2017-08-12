package main;

import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import datatypes.Server;
import de.luete.gamebrowser.main.ApplicationSettings;
import gui.PlayerDialog;
import gui.Window;

/**
 * Contains main method and sets the Look&Feel.
 */
public class Main
{    
    public static void main(String[] args)
    {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try
        {
            /*
             * for (javax.swing.UIManager.LookAndFeelInfo info :
             * javax.swing.UIManager.getInstalledLookAndFeels()) { if
             * ("Nimbus".equals(info.getName())) {
             * javax.swing.UIManager.setLookAndFeel(info.getClassName()); break;
             * } }
             */
            Font sf = new Font("Arial", Font.PLAIN, 12);
            ImageIcon ic = new ImageIcon("./icon.png");

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("TextArea.border", UIManager.get("TextField.border"));

            UIManager.put("Button.font", sf);
            UIManager.put("Label.font", sf);
            
            UIManager.put("Tree.openIcon", ic.getImage());
            UIManager.put("Tree.closedIcon", ic);
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException  ex)
        {
            System.out.println(ex);
        }
        //</editor-fold>
        
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                ApplicationSettings.load();
                Server.loadFavs();
                
                Window.getInstance();
                
                if(ApplicationSettings.playerListStartUp)
                    PlayerDialog.getInstance().setVisible(true); //First initialisation
            }
        });
    }
}