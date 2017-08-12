package gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import datatypes.Colorset;
import datatypes.Game;
import main.Controller;
import windowmanager.Resizable;
import windowmanager.Resizer;

/**
 * A dialog window to display the OptionsPanel and GamePanel in a TabbedPane.
 * This is class uses the Singleton pattern, this means there can only exist one object.
 */
public class OptionsGameDialog extends JDialog implements Resizable
{
    private JTabbedPane     tabs;
    private OptionsPanel    options;
    private GamesPanel      games;
    
    private OptionsGameDialog()
    {
        super(Window.getInstance(), "Options", true);
        
        //We only need ALL protocols, games and colorsets when we start this (because of the GamesPanel)
        Controller.loadProtocols();
        Game.loadGames();
        Colorset.loadAll();
        
        this.setLayout(new SpringLayout());
        this.setMinimumSize(new Dimension(400, 300));
        this.setPreferredSize(new Dimension(600, 300));
        this.pack();
        
        
        //Center dialog
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        
        initComponents();
        Resizer.addToResizer(this);
    }
    
    private void initComponents()
    {
        options = new OptionsPanel();
        games = new GamesPanel();
        
        tabs = new JTabbedPane();
        tabs.setPreferredSize(this.getContentPane().getSize());
        
        tabs.add("Settings", options);
        tabs.add("Games", games);
        
        add(tabs);
    }
    
    @Override
    public void resize()
    {
        tabs.setPreferredSize(this.getContentPane().getSize());
        tabs.revalidate();
    }
    
    /**
     * This will display the options, load GamesPanel and OptionsPanel.
     */
    public void display()
    {
        tabs.setSelectedIndex(0);
        
        games.loadData();
        options.display();
        
        this.setVisible(true);
    }
    
    
    private static class SingletonHolder
    { 
        private static final OptionsGameDialog INSTANCE = new OptionsGameDialog();
    }

    public static OptionsGameDialog getInstance()
    {
        return SingletonHolder.INSTANCE;
    }
}
