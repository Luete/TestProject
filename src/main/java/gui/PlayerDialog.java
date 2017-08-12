package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import datatypes.Colorset;
import datatypes.Game;
import datatypes.PlayerList;
import de.luete.gamebrowser.main.ApplicationSettings;
import main.Controller;
import protocols.BaseProtocol;
import protocols.PluginCapabilities;
import windowmanager.Resizable;
import windowmanager.Resizer;


/**
 * This is the dialog that displays the PlayerList.
 * It displays the PlayerList in a JList and marks bots with a red background (if the protocol supports bot recognition).
 * This is class uses the Singleton pattern, this means there can only exist one object.
 */
public class PlayerDialog extends JDialog implements Resizable
{
    private JScrollPane scrlPlayers;
    private JList       lstPlayers;
    private PlayerList  pList;
    private int         pluginOptions;
    private Colorset    colors;
    private final Font  font = new Font("Arial", Font.PLAIN, 15);

    PlayerDialog()
    {
        super(Window.getInstance(), "Playerlist", false);

        this.setLayout(new SpringLayout());
        this.setMinimumSize(new Dimension(150, 150));
        
        if (ApplicationSettings.playerBounds == null)
        {
            this.setSize(200, 500);

            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        }
        else
        {
            this.setSize(new Dimension(ApplicationSettings.playerBounds.width, ApplicationSettings.playerBounds.height));
            this.setLocation(ApplicationSettings.playerBounds.x, ApplicationSettings.playerBounds.y);
        }

        lstPlayers = new JList();
        lstPlayers.setPreferredSize(this.getSize());
        lstPlayers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstPlayers.setCellRenderer(new PlayerListRenderer());
        lstPlayers.setEnabled(false);
        lstPlayers.setBackground(ApplicationSettings.colors.getColor("playerlist"));

        scrlPlayers = new JScrollPane(lstPlayers);
        scrlPlayers.setPreferredSize(this.getSize());


        add(scrlPlayers);
        initListener();
        
        Resizer.addToResizer(this);
    }

    /**
     * Sets the current PlayerList to be displayed.
     * Fills the JList with the amount of connected players and their names.
     * @param list The player list with name and bot information.
     * @param game The servers game
     */
    public void setList(PlayerList list, Game game)
    {
        if (list == null || game == null)
        {
            lstPlayers.setListData(new Object[]{});
            pluginOptions = 0;
            colors = null;
            return;
        }
        
        if(!Controller.protocols.containsKey(game.protocol))
            return;
        
        
        colors = Colorset.getColorSet(game.colorSet);
        if(colors == null)
            colors = new Colorset();
        
        pList = list;
        try
        {
            pluginOptions = ((BaseProtocol)Controller.protocols.get(game.protocol).newInstance()).getSupportedOptions();
        }
        catch (InstantiationException | IllegalAccessException ex)
        {
            Logger.getLogger(PlayerDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        
               
        String playerarr[];
        //Check if the protocol is able to gather player information
        if((pluginOptions & PluginCapabilities.GET_PLAYERS) != 0)
        {
            playerarr = new String[list.getNumEntries() + 1];

            playerarr[0] = "Connected players: " + (list.getNumEntries());
            
            System.arraycopy(list.getHtmlList(colors).toArray(), 0, playerarr, 1, list.getNumEntries());
        }
        else
        {
            playerarr = new String[2];

            playerarr[0] = "This protocol doesn't";
            playerarr[1] = "support a playerlist.";
            
            pluginOptions = 0;
        }


        lstPlayers.setListData(playerarr);

        resize();
    }

    private void initListener()
    {        
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                close();
            }
        });
    }

    @Override
    public void resize()
    {
        if (lstPlayers.getModel().getSize() > 0)
        {
            lstPlayers.setPreferredSize(new Dimension(this.getContentPane().getSize().width - 20, lstPlayers.getModel().getSize() * lstPlayers.getCellBounds(0, 0).height));
        }
        else
        {
            lstPlayers.setPreferredSize(new Dimension(0, 0));
        }

        scrlPlayers.setPreferredSize(this.getContentPane().getSize());

        scrlPlayers.revalidate();
        lstPlayers.revalidate();
        this.revalidate();
    }

    private void close()
    {
        ApplicationSettings.playerBounds = new Rectangle(this.getBounds());
        ApplicationSettings.playerListVisible = false;
        ApplicationSettings.save();
    }
    
    
    private static class SingletonHolder
    { 
        private static final PlayerDialog INSTANCE = new PlayerDialog();
    }

    public static PlayerDialog getInstance()
    {
        return  SingletonHolder.INSTANCE;
    }

    private class PlayerListRenderer extends DefaultListCellRenderer
    {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            String text = (String) value;
            setText(text);
            setFont(font);
            setOpaque(true);
            
            //so render with white background for the message.
            if(pluginOptions == 0)
            {
                this.setBackground(Color.white);
                return this;
            }
            
            
            if (index == 0)
            { //Mark the playeramount grey
                this.setBackground(new Color(150, 150, 150, 130));
            }
            else
            {
                if(index-1 >= pList.getNumEntries()) //I have no idea if this will ever happen. But if so, there is an error!
                {
                    this.setBackground(Color.yellow);
                    return this;
                }
                
                if(pList.isBot(index-1) && (pluginOptions & PluginCapabilities.RECOGNIZE_BOTS) != 0) //It's a bot? BLOODY RED
                {
                    this.setBackground(Color.decode(colors.getBotColor()));
                }
                else
                {
                    String backGround = colors.getBackground();
                    
                    if(ApplicationSettings.playerColor && !backGround.equalsIgnoreCase("transparent"))
                        this.setBackground(Color.decode(colors.getBackground())); //If we display the ingame colors,
                                                                      //use a defined background for better readability.
                    else
                        this.setOpaque(false);
                }
            }
            
            return this;
        }
    }
}
