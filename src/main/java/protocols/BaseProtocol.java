package protocols;

import datatypes.PlayerList;
import datatypes.ServerStatus;

/**
 * Every protocol class has to extend this.
 * Contains methods to prepare the ServerStatus.
 * The prepareServerStatus method is declared final because this does NOT need to be modified.
 */
public abstract class BaseProtocol
{    
    /**If true, print gathered information to console*/
    protected final boolean DEBUG = true;
    
    /**The IP of the server whose status we're querying.*/
    protected String ip;
    /**The IP of the server whose status we're querying.*/
    protected int port;
    /**Holds the common server status information.*/
    protected String infoStr;
    
    /**
     * Send a status query and return the response.
     * @return Server response.
     */
    abstract protected String getInfo();
    
    /**
     * Process the gathered information and set up the ServerStatus.
     * Practically you have to only set the map and hasPassword in this method.
     * 
     * @param st The ServerStatus that has to be filled.
     */
    abstract protected void processInfo(ServerStatus st);
    
    /**
     * Information this plugin is able to gather.
     * <p>The return value states wether this plugin supports an option or not.
     * You just have to return the options for this plugin.<br>
     * <br>
     * You can combine them with the binary or -> |<br>
     * return PluginOptions.GET_MAPS | PluginOptions.RECOGNIZE_BOTS;<br>
     * @return An integer holding the bitflags.
     */
    abstract public int getSupportedOptions();
    
    /**
     * Processes an info/status string and reads out player data.
     * In this method you read the player info (name & ping) add it to a PlayerList and return it!
     * @return PlayerList filled with information.
     */
    protected PlayerList getPlayers()
    {
        return new PlayerList();
    }

    /**
     * Prepares a ServerStatus object for the given server and returns it.
     * @param ip Server IP/DNS
     * @param port Server port
     * @return The ServerStatus with gathered information.
     */
    public final ServerStatus prepareServerStatus(String ip, int port)
    {
        ServerStatus st = new ServerStatus();
        this.ip = ip;
        this.port = port;
        
        infoStr = getInfo();
        
        if(infoStr == null)
            return st;
        
        processInfo(st);
        st.reachable = true;
        st.ping = Short.parseShort(infoStr.substring(0, 3));
        
        //We only need to call this at all if the protocol supports a playerlist.
        if((getSupportedOptions() & PluginCapabilities.GET_PLAYERS) != 0)
            st.playerList = getPlayers();
        
        
        if(DEBUG)
        {
            System.out.println("\nServer: " + ip + ":" + port);
            System.out.println(infoStr);
            System.out.println("Map " + st.getMap());
            System.out.println("Password " + st.hasPassword);

            System.out.println(st.playerList.getNames() + "\n============================================");

        }
        
        return st;
    }
}
