package datatypes;


/**
 * Contains statusinformation about the server (reachability, password, playerlist).
 */
public class ServerStatus
{
    private String      mapName = "";
    
    public String       password = null,
                        username = null;
    
    public boolean      reachable=false, hasPassword = false;
    
    public PlayerList   playerList = new PlayerList();;
    
    public short        ping = -1;
    
    
    /**
     * Returns the amount of players.
     * @param onlyRealPlayers If true it will exclude bots from the count.
     */
    public int getNumPlayers(boolean onlyRealPlayers)
    {
        if(onlyRealPlayers)
            return playerList.getNumBotPlayer(false);
        
        return playerList.getNumEntries();
    }
    
    /**
     * Returns the name of the current map.
     * @return The mapname.
     */
    public String getMap()
    {
        return mapName;
    }

    /**
    * Sets the map and escapes html characters.
    * @param map The maps name.
    */
    public void setMap(String map)
    {
        if(map == null)
            mapName = new String();
        else
            mapName = map.replace("<", "&lt;").replace(">", "&gt;");
    }
}
