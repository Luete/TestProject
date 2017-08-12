package main;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import datatypes.Game;
import datatypes.Server;
import de.luete.gamebrowser.main.ApplicationSettings;
import gui.PasswordDialogS;
import gui.Window;
import protocols.BaseProtocol;

/**
 * Contains static methods and attributes that don't fit anywhere else.
 */
public class Controller
{
    public static Map<String, Class<BaseProtocol>> protocols = new HashMap<>();
    
    
    /**
     * Loads a protocol file from ./Protocols and adds it to the list.
     * Loads all class files in ./Protocols and adds them to the protocol map.
     * 
     * @param name The file name.
     * @return Returns true if the protocol could be loaded.
     */
    public static boolean loadProtocol(String name)
    {
        if(protocols.containsKey(name))
            return true;
        if(name.isEmpty())
            return false;
        
        if(name.equals("BaseProtocol"))
            return false;
        
        try
        {
            File dir = new File("./");
            URL[] classUrl = new URL[]{ dir.toURI().toURL() };

            ClassLoader cl = new URLClassLoader(classUrl);

            Class loadedClass = cl.loadClass("protocols."+name);
            
            //Just to cause an exception
            BaseProtocol p = (BaseProtocol)loadedClass.newInstance();                        
                        
            protocols.put(name, loadedClass);
            
            
            return true;
        }
        catch (Exception ex)
        {
           System.out.println("Couldn't load protocol " + name + "\n" + ex);
        }
        
        return false;
    }
    
    
    
    /**
     * Loads all Protocols.
     * Clears the protocols list and calls loadProtocol(filepath) for all .class files in ./Protocols.
     */
    public static void loadProtocols()
    {
        protocols.clear();
        
        File dir = new File("./Protocols/");

        String[] fileList = dir.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File d, String name)
            {
                return name.endsWith(".class");
            }
        });
        
        if(fileList == null)
        {
        	return;
        }
        
        for(int i = 0; i < fileList.length; i++)
        {
            loadProtocol(fileList[i].replace(".class", ""));
        }
    }
    
    /**
     * Sends a package to the server (UDP) and returns the answer.
     *
     * @param sendData The message that has to be sent, as byte array.
     * @param IP Host IP/Address.
     * @param PORT Blubb
     * @return Returns the received answer with the ping (first 3 digits).
     */
    public static String send(byte[] sendData, String IP, int PORT, String encoding)
    {
        String str = "";
        InetAddress ipAddress;
        long start = 0;
            
        try(DatagramSocket clientSocket = new DatagramSocket())
        {
            ipAddress = InetAddress.getByName(IP);
            clientSocket.setSoTimeout(ApplicationSettings.queryTimeout);

            start = System.currentTimeMillis();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, PORT);
            clientSocket.send(sendPacket);

            str = receive(clientSocket, "UTF-8");
            

            clientSocket.close();
        }
        catch (IOException ex)
        {//Timeout, do nothing
            return null;
        }
        
        str = String.format("%03d", System.currentTimeMillis()-start)+str;
        return str;
    }
    
    /**
     * Sends a package to the server (UDP) and receives multiple packets.
     * Checks until the last packets with the endbytes arrives.
     *
     * @param sendData The message that has to be sent, as byte array.
     * @param IP Host IP/Address.
     * @param PORT Blubb
     * @return Returns the received answer with the ping (first 3 digits).
     */
    public static String sendMulti(byte[] sendData, String IP, int PORT, String encoding, String endStr)
    {
        String str = new String();
        
        InetAddress ipAddress;
        
        long start = 0;
        int ping = 0;
        boolean firstLoopRun = true;
            
        try(DatagramSocket clientSocket = new DatagramSocket())
        {
            ipAddress = InetAddress.getByName(IP);
            clientSocket.setSoTimeout(ApplicationSettings.queryTimeout);

            
            start = System.currentTimeMillis();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, PORT);
            clientSocket.send(sendPacket);
            

            String lastStr = "";
            while(!lastStr.contains(endStr))
            {
                lastStr = receive(clientSocket, encoding);
                
                if(firstLoopRun)
                {//Just use the tme of the first packet.
                    ping = (int)(System.currentTimeMillis()-start);
                    firstLoopRun = false;
                }
                
                str += lastStr;
            }
        }
        finally
        {
            if(str.isEmpty())
                return null;
            
            str = String.format("%03d", ping)+str;
            return str;
        }
    }
    
    
    private static String receive(DatagramSocket sock, String encoding) throws IOException
    {
        String str = "";

        byte[] receiveData = new byte[4096];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, 0, receiveData.length);

        sock.receive(receivePacket);
        if(encoding != null)
            str = new String(receivePacket.getData(), encoding); //Receive and decode answer
        else
            str = new String(receivePacket.getData());

        return str.trim()+"\0";
    }

    /**
     * Connects to a server at a specified index.
     * @param listIndex The index the server has in Server.svArray
     */
    public static void start(int listIndex)
    {
        if (listIndex < 0)
            return;
        
        try {
            File temp = null;
            String OS = System.getProperty("os.name").toLowerCase();
            if (OS.indexOf("win") >= 0) {
                temp = File.createTempFile("command", ".bat");
            } else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0) {
                temp = File.createTempFile("command", ".sh");
            } else {
                JOptionPane.showMessageDialog(Window.getInstance(), "The operating system " + OS + " is not supported.", "Operating system not supported", JOptionPane.ERROR_MESSAGE);
                return;
            }
            temp.deleteOnExit();
            
            try (FileWriter out = new FileWriter(temp.getAbsoluteFile()))
            {
                Server sv = Server.get(listIndex);
                if (sv.status.hasPassword)
                {
                    PasswordDialogS dia = new PasswordDialogS();
                    dia.setVisible(true);
                    String pw = dia.getPassword();
                    if (pw == null) {
                        return;
                    }
                }
                if (sv.script.isEmpty()) {
                    return;
                }
                Game game = Game.get(sv.game);
                File executable = new File(game.executable);
                if (!executable.exists()) {
                    return;
                }
                
                String cmd = sv.script;
                cmd = cmd.replace("%IP%", sv.IP);
                cmd = cmd.replace("%PORT%", String.valueOf(sv.PORT));
                cmd = cmd.replace("%GAMEPATH%", executable.getParent());
                cmd = cmd.replace("%EXECUTABLE%", executable.getName());
                
                if (sv.status.password != null) {
                    cmd = cmd.replace("%PASSWORD%", sv.status.password);
                } else {
                    cmd = cmd.replace("%PASSWORD%", "");
                }
                        
                out.write(cmd);
                out.close();
                
                ProcessBuilder pb = new ProcessBuilder(temp.getAbsolutePath());
                pb.start();
                    
                if (ApplicationSettings.closeAtConnect)
                {
                    ApplicationSettings.save();
                    System.exit(0);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Searches a key in a string and returns the value.
     * Use this method if you have a String containing key/value pairs.
     * @param str The string that is searched in.
     * @param key The key we want the value for.
     * @param delimitter The delimitter of the key/value pairs.
     * @return The value of the searched key. If no key was found it will return null.
     */
    public static String getValueForKey(String str, String key, String delimitter)
    {
        Scanner sc = new Scanner(str);
        sc.useDelimiter(delimitter);

        for (int i = 0; i < str.length(); i++)
        {
            if (sc.hasNext() && sc.next().equals(key))
            {
                return sc.next();
            }
        }

        return null;
    }
    
    
    /**
     * Returns the position of the nth occurence of a Char in a String.
     * @param str The string that is searched in.
     * @param c The char to be searched
     * @param n The number of chars to skip.
     * @return The position of the nth occurence in the string. -1 if the string doesn't contain the char.
     */
    public static int nthOccurrence(String str, char c, int n)
    {
        int pos = str.indexOf(c, 0);
        
        while (n-- > 0 && pos != -1)
            pos = str.indexOf(c, pos+1);
        
        return pos;
    }
}