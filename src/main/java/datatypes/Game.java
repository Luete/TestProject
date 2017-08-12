package datatypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import main.Controller;

/**
 * A game object holds the path to the executable and the protocol name.
 */
public class Game
{
    /**Carries all the loaded games in a Hashmap.*/
    private static Map<String, Game> games = new HashMap();
    
    public String name = "",
           executable = "",
           protocol = "",
           script = "cd \"%GAMEPATH%\"\n\"%EXECUTABLE%\" +connect %IP%:%PORT%",
           queryPort = "",
           colorSet = "";
    
    public int     standardPort = -1;
    
    public boolean loaded = false;
    
    public boolean playerColor = false;
    
    public Game()
    {
    }
    
    public Game(String file)
    {
        load(file);
    }

    public void save()
    {
        try(FileOutputStream stream = new FileOutputStream("./Games/" + name + ".game"))
        {
            Properties buf = new Properties();
            //buf.setProperty("name", name);
            buf.setProperty("executable", executable);
            buf.setProperty("protocol", protocol);
            buf.setProperty("script", script);
            buf.setProperty("queryport", queryPort);
            buf.setProperty("standardport", String.valueOf(standardPort));
            buf.setProperty("colorset", String.valueOf(colorSet));
            
            buf.store(stream, null);
        }
        catch (IOException ex)
        {
            System.out.println("Couldn't save game file. (" + name + ")\n"+ex);
        }
        
        
        /*
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./Games/"+name+".game")))
        {
            oos.writeObject(name);
            oos.writeObject(executable);
            oos.writeObject(protocol);
            oos.writeObject(script);
            oos.writeObject(queryPort);
            oos.writeBoolean(playerColor);
            oos.writeInt(standardPort);
        }
        catch (IOException ex)
        {
            System.out.println("Couldn't save game file. (" + name + ")\n"+ex);
        }*/
    }

    private void load(String file)
    {
        loaded = false;


        try(FileInputStream stream = new FileInputStream("./Games/" + file + ".game"))
        {
            Properties buf = new Properties();
            buf.load(stream);
            
            name = file;
            executable = buf.getProperty("executable");
            protocol = buf.getProperty("protocol");
            colorSet = buf.getProperty("colorset");
            script = buf.getProperty("script");
            queryPort = buf.getProperty("queryport");
            standardPort = Integer.parseInt(buf.getProperty("standardport", "-1"));


            Colorset.loadColorSet(colorSet);
            Controller.loadProtocol(protocol);

            if(!colorSet.isEmpty() && Colorset.getColorSet(colorSet) != null)
                playerColor = true;

            loaded = true;
        }
        catch(IOException ex)
        {
            System.out.println("Couldn't read game file. (" + name + ")\n"+ex);
            loaded = false; //I guess this is redundant, but just to be save..
        }
        /*
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./Games/" + file + ".game")))
        {
            name = (String)ois.readObject();
            executable = (String)ois.readObject();
            protocol = (String)ois.readObject();
            script = (String)ois.readObject();
            queryPort = (String)ois.readObject();
            playerColor = ois.readBoolean();
            standardPort = ois.readInt();
                    
            Controller.loadProtocol(protocol);
            loaded = true;
        }
        catch (ClassNotFoundException | IOException ex)
        {
            System.out.println("Couldn't read game file.\n" + ex);
            loaded = false; //I guess this is redundant, but just to be save..
        }*/
    }
    
    /**
     * Loads a game file from ./Games and adds it to the list.
     * 
     * @param pName The file name.
     */
    public static void loadGame(String pName)
    {
        if(games.containsKey(pName)) 
            return;
        
        Game buf = new Game(pName);
        
        
        if(buf.loaded)
            games.put(pName, buf);
    }
    
    /**
     * Loads all Games.
     * Loads all Games from ./Games and adds them to the ArrayList games.
     */
    public static void loadGames()
    {
        games.clear();
        
        File dir = new File("./Games/");

        String[] fileList = dir.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File d, String name)
            {
                return name.endsWith(".game");
            }
        });
        
        if(fileList == null)
        {
        	return;
        }
        
        for(int i = 0; i < fileList.length; i++)
        {
            loadGame(fileList[i].replace(".game", ""));
        }
    }
    
    /**
     * Returns the game object for the specified name.
     * @param pName The games name.
     */
    public static Game get(String pName)
    {
        return games.get(pName);
    }
    
    /**
     * Indidicates if the game is loaded correctly.
     */
    public boolean isLoaded()
    {
        return loaded;
    }
    
    /**
     * Returns the amount of servers in the list.
     */
    public static int getGameCount()
    {
        return games.keySet().size();
    }
    
    /**
     * Returns the game Map that contains all Game objects.
     */
    public static Map<String, Game> getGameMap()
    {
        return games;
    }
    
    /**
     * Removes the Game from the list and the harddisk.
     */
    public void delete()
    {
        File f = new File("./Games/" + name + ".game");
        if(!f.delete())
            return;

        games.remove(this);
    }
}
