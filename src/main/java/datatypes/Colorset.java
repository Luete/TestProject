package datatypes;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * This class stores and manages colorcodes for games.
 */
public class Colorset
{
    private static Map<String, Colorset> gameColors = new HashMap();
    
    private Map<String, String> colorMap = new HashMap();
    private String backGround = "#FFFFFF";
    private String botColor = "#FF8282";
    private String standardColor = "#000000";
    
    private boolean loaded = false;
    
    public Colorset(String file)
    {
        load(file);
    }
    
    public Colorset()
    {
        
    }
    
    public String getBackground()
    {
        return new String(backGround);
    }
    
    public String getStandardColor()
    {
        return new String(standardColor);
    }
    
    public String getBotColor()
    {
        return new String(botColor);
    }
    
    private void load(String file)
    {        
        if(file == null || file.isEmpty())
            return;
        
        try(BufferedReader in = new BufferedReader(new FileReader("./Colorsets/" + file + ".txt")))
        {
            String buf = null;
            
            while ((buf = in.readLine()) != null)
            {
                String[] parts = buf.split(" ");
                
                try
                {
                    if(parts[0].equals("background"))
                        backGround = parts[1];
                    else if(parts[0].equals("standard"))
                        standardColor = parts[1];
                    else if(parts[0].equals("botbackground"))
                        botColor = parts[1];
                    else
                        colorMap.put(parts[0], parts[1]);
                }
                catch(ArrayIndexOutOfBoundsException e)
                {//Just print a message since the value won't be written to the attribute if this error occurs.
                    System.out.println("Error reading attribute \"" + parts[0] + "\" in colorset \"" + file + "\".\nThe standard value will be used.");
                }
            }
            
            loaded = true;
        }
        catch(IOException ex)
        {
            System.out.println("Couldn't load colorset " + file + ".\n" + ex);
            loaded = false;
        }
    }
    
    /**
     * Returns a Color object for the specified key.
     * @param key The "Colorcode".
     * @return Color object.
     */
    public Color getColor(String key)
    {
        return Color.decode(colorMap.get(key));
    }
    
    /**
     * Resturns the map that holds the color codes and the color equivalent.
     * @return The color map.
     */
    public Map<String,String> getMap()
    {
        return colorMap;
    }
    
    /**
     * Loads a colorset from a file.
     * @param file The colorsets name.
     */
    public static void loadColorSet(String file)
    {
        if(gameColors.containsKey(file) || file.equalsIgnoreCase("UI"))
            return;
        
        Colorset buf = new Colorset(file);
        
        if(buf.loaded)
            gameColors.put(file, buf);
    }
    
    /**
     * Loads all colorsets to memory.
     */
    public static void loadAll()
    {
        gameColors.clear();
        
        File dir = new File("./Colorsets/");

        String[] fileList = dir.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File d, String name)
            {
                return name.endsWith(".txt");
            }
        });
        
        for(int i = 0; i < fileList.length; i++)
        {
            loadColorSet(fileList[i].replace(".txt", ""));
        }
    }
    
    /*
     * Returns a colorset from the list.
     */
    public static Colorset getColorSet(String name)
    {
        return gameColors.get(name);
    }
    
    /**
     * Returns the names of all loaded colorsets.
     * @return The names of the colorsets.
     */
    public static Set<String> getSetNames()
    {
        return gameColors.keySet();
    }
    
    /**
     * Removes all color codes from the passed string.
     * @param str
     * @return A clean string representation.
     */
    public String cleanString(String str)
    {
        String buf = new String(str);
        
        
        for (Map.Entry<String, String> entry : colorMap.entrySet())
            buf = buf.replace(entry.getKey(), "");
        
        return buf;
    }
    
    /**
     * Replaces all color codes by html tags.
     * @param str
     * @return A Html string. (for representation in labels, lists....)
     */
    public String strToHtml(String str)
    {
        String buf = new String(str);
        
        
        for (Map.Entry<String, String> entry : colorMap.entrySet())
            buf = buf.replace(entry.getKey(), "<font color=\""+entry.getValue()+"\">");
        
        return buf;
    }     
            
}
