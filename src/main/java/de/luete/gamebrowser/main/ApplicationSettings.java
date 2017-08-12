package de.luete.gamebrowser.main;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import datatypes.Colorset;
import gui.AddServer;
import gui.PlayerDialog;
import gui.Window;

/**
 * Contains the settings, and windowinformation (size etc.).
 */
public class ApplicationSettings
{
    public static Rectangle frameBounds = new Rectangle(100, 100, 180, 450),
            playerBounds = new Rectangle(100, 100, 180, 450),
            addBounds = new Rectangle(100, 100, 450, 250);
    
    public static boolean playerListStartUp = false,
            closeAtConnect = true,
            plrAmountInList = true,
            onlyRealPlayers = false,
            displayMaps = true,
            playerColor = true,
            displayPW = true,
            playerListVisible = false;
    
    public static     Colorset colors;
    
    public static int queryTimeout = 500;

    public static void save()
    {
        ApplicationSettings.frameBounds = Window.getInstance().getBounds();
        ApplicationSettings.playerBounds = PlayerDialog.getInstance().getBounds();
        ApplicationSettings.addBounds = AddServer.getInstance().getBounds();
        
        try(FileOutputStream stream = new FileOutputStream("./Settings.ini"))
        {
            Properties buf = new Properties();
            
            buf.setProperty("ServerListBounds", frameBounds.toString());
            buf.setProperty("PlayerListBounds", playerBounds.toString());
            buf.setProperty("ServerDialogBounds", addBounds.toString());
            
            buf.setProperty("StartWithPlayerlist", String.valueOf(playerListStartUp));
            buf.setProperty("CloseAtConnect", String.valueOf(closeAtConnect));
            
            buf.setProperty("DisplayPlayerCount", String.valueOf(plrAmountInList));
            buf.setProperty("DontCountBots", String.valueOf(onlyRealPlayers));
            
            buf.setProperty("DisplayMap", String.valueOf(displayMaps));
            buf.setProperty("ShowPlayerColor", String.valueOf(playerColor));
            buf.setProperty("ShowPassword", String.valueOf(displayPW));
            
            buf.setProperty("QueryTimeout", String.valueOf(queryTimeout));
            
            buf.store(stream, null);
        }
        catch (IOException ex)
        {
            Logger.getLogger(ApplicationSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void load()
    {
        colors = new Colorset("UI");
        
        try(FileInputStream stream = new FileInputStream("./Settings.ini"))
        {
            Properties buf = new Properties();
            buf.load(stream);
            
            frameBounds = stringToRectangle(buf.getProperty("ServerListBounds"));
            playerBounds = stringToRectangle(buf.getProperty("PlayerListBounds"));
            addBounds = stringToRectangle(buf.getProperty("ServerDialogBounds"));
            
            playerListStartUp = Boolean.valueOf(buf.getProperty("StartWithPlayerlist"));
            closeAtConnect = Boolean.valueOf(buf.getProperty("CloseAtConnect"));
            
            plrAmountInList = Boolean.valueOf(buf.getProperty("DisplayPlayerCount"));
            onlyRealPlayers = Boolean.valueOf(buf.getProperty("DontCountBots"));
            
            displayMaps = Boolean.valueOf(buf.getProperty("DisplayMap"));
            playerColor = Boolean.valueOf(buf.getProperty("ShowPlayerColor"));
            displayPW = Boolean.valueOf(buf.getProperty("ShowPassword"));
            
            queryTimeout = Integer.valueOf(buf.getProperty("QueryTimeout"));
        }
        catch (IOException ex)
        {
            System.out.println("Couldn't load settings.\n" + ex);
            File dir = new File("./Servers/");

            String[] fileList = dir.list(new FilenameFilter()
            {
                @Override
                public boolean accept(File d, String name)
                {
                    return name.endsWith(".server");
                }
            });

            if (fileList.length == 0)
            {
                JOptionPane.showMessageDialog(null, "This seems to be the first time you run this tool.\n"
                        + "You should go to the options and set the path to your game executable.\n"
                        + "Enjoy gaming!", "Hello there!", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    /**
     * Reads a Rectangle String representation and returns the rectangle object.
     * I use this function to cheat and load rectangle objects from a Properties file.
     * 
     * @param str The String representation of the Rectangle.
     * @return The rectangle object.
     * @throws IOException Throws an IOException if the String couldn't be read.
     */
    private static Rectangle stringToRectangle(String str) throws IOException
    {
        str = str.replace("java.awt.Rectangle[", "");
        str = str.replace(",", "\n");
        str = str.substring(0, str.length()-1);        
        
        Properties prop = new Properties();
        prop.load(new StringReader(str));
        
        int x, y, width, height;
        
        //If we use 0 as standard value the windows will use their minimum sizes so this is okay!
        x = Integer.parseInt(prop.getProperty("x", "0"));
        y = Integer.parseInt(prop.getProperty("y", "0"));
        width = Integer.parseInt(prop.getProperty("width", "0"));
        height = Integer.parseInt(prop.getProperty("height", "0"));
        
        return new Rectangle(x, y, width, height);
    }
}
