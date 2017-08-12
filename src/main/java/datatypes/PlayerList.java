package datatypes;

import java.util.ArrayList;

/**
 * Holds an ArrayList with the player names and one with the info if it is a Bot.
 */
public class PlayerList
{
    /**Contains player names.*/
    private ArrayList<String> players = new ArrayList();
    /**Indicates if the player is a bot.*/
    private ArrayList<Boolean> isBot = new ArrayList();
    
    
    /**
     * Adds the player information to the lists and escapes the < and > html characters.
     * 
     * @param name The players name.
     * @param bot Indicates if the player is a bot.
     */
    public void add(String name, boolean bot)
    {
        if(name == null)
            return;
        
        String buf = name.replace("<", "&lt;")
                     .replace(">", "&gt;");
        
        players.add(buf);
        isBot.add(bot);
    }
    
    /**
     * Returns the name of the player at the specified index.
     */
    public String getName(int index)
    {
        if(index < 0 || index >= players.size())
            return null;
        
        return players.get(index);
    }
    
    /**
     * Copies the player names with unescaped html characters in a new list and returns it.
     */
    public ArrayList<String> getNames()
    {
        ArrayList<String> buf = new ArrayList();
        
        for(int i = 0; i < players.size(); i++)
            buf.add(players.get(i).replace("&gt;", ">").replace("&lt;", "<"));
        
        return buf;
    }
    
    /**
     * Returns a list containing the player names without color codes.
     */
    public ArrayList<String> getCleanList(Colorset colors)
    {
        ArrayList<String> buf = (ArrayList<String>)players.clone();
        
        //Theres nothing to clean...
        if(colors == null)
            return buf;
        
        for(int i = 0; i < buf.size(); i++)
        {
            buf.set(i, colors.cleanString(buf.get(i)));
        }
        
        return buf;
    }
    
    /**
     * Returns a list containing the player names in html format.
     * This function replaces all color codes with the html equivalent.
     * This is to be used in JLists, JTables....
     */
    public ArrayList<String> getHtmlList(Colorset colors)
    {
        ArrayList<String> buf = new ArrayList(players);
        
        //Theres nothing to convert...
        if(colors == null)
            return buf;
        
        for(int i = 0; i < buf.size(); i++)
        {
            buf.set(i, "<html><body text=\""+colors.getStandardColor()+"\">"
                       + buf.get(i) + "</body></html>");
                
            buf.set(i, colors.strToHtml(buf.get(i)));
        }
        
        return buf;
    }
    
    /**
     * Indicates if the player at the specified index is a bot.
     * @param index
     * @return If the player is a bot it returns true, otherwise false.
     *         Also returns false if index is out of range.
     */
    public boolean isBot(int index)
    {
        if(index < 0 || index >= players.size())
            return false;
        
        return isBot.get(index);
    }
    
    /**
     * Returns the number of bots/players in the list.
     * @param bot Indicates if you want to check for bots or players.
     */
    public int getNumBotPlayer(boolean bot)
    {
        int count = 0;
        
        for(int i = 0; i < isBot.size(); i++)
        {
            if(isBot.get(i) == bot)
                count++;
        }
        
        return count;
    }
    
    /**
     * Returns the number of entries.
     */
    public int getNumEntries()
    {
        return players.size();
    }
}
