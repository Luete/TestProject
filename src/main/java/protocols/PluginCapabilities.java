package protocols;

/**This class contains and provides constants to use as bitflags.
 * These constants represent the the possibilities the plugin has/informations it may gather.
 * 
 * The following line would save GET_MAPS and RECOGNIZE_BOTS in the "options" integer.
 * <pre>
 * {@code
 * int options = PluginOptions.GET_MAPS | PluginOptions.RECOGNIZE_BOTS;
 * }
 * </pre>
 */
public class PluginCapabilities
{
    /**All options disabled.*/
    public static final int NO_OPTIONS = 0;
    /**Indicates if the plugin is able to gather the mapname.*/
    public static final int GET_MAPS = 1;
    /**Indicates if the plugin is able to recognize bots.*/
    public static final int RECOGNIZE_BOTS = 2;
    /**Indicates if the plugin supports passwords.*/
    public static final int GET_PASSWORD = 4;
    /**Indicates if the plugin is able to receive a playerlist.*/
    public static final int GET_PLAYERS = 8;
}
