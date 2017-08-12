package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

import datatypes.Colorset;
import datatypes.Game;
import datatypes.Server;
import windowmanager.Resizable;
import windowmanager.Resizer;

public class PlayerSearch extends JDialog implements Resizable
{
    private JTextField   txtSearch;
    private JTable       tblResults;
    private JScrollPane  scrlResults;
    private DefaultTableModel model;
    private JComboBox    cbGames;
    private JRadioButton btnStartsWith, btnContains;
    private ButtonGroup  btnGroup = new ButtonGroup();
    
    private SpringLayout layout;
    
    public PlayerSearch()
    {
        super(Window.getInstance(), "Search a player", true);
        initComponents();
        initLayout();
        
        Resizer.addToResizer(this);
        
        this.setMinimumSize(new Dimension(350,450));
        this.setSize(new Dimension(350,450));
        
        //Center dialog
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        
        this.setVisible(true);
    }
    
    private void initGamesBox()
    {
        DefaultComboBoxModel cModel = new DefaultComboBoxModel();
        ArrayList<String> games = new ArrayList(Game.getGameMap().keySet());
        
        
        cModel.addElement("ALL games");
        
        for(int i = 0; i < games.size(); i++)
            cModel.addElement(games.get(i));
        
        cbGames.setModel(cModel);
        cbGames.setSelectedIndex(0);
    }
    
    private void initComponents()
    {
        txtSearch = new JTextField();
        cbGames = new JComboBox();
        initGamesBox();
        cbGames.setPreferredSize(new Dimension(cbGames.getPreferredSize().width, 25));
        
        btnStartsWith = new JRadioButton("Starts with", true);
        btnContains = new JRadioButton("Contains", false);
        btnGroup = new ButtonGroup();
        btnGroup.add(btnStartsWith);
        btnGroup.add(btnContains);
        
        
        txtSearch.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String gameFilter = (String)cbGames.getSelectedItem();
                
                model.setRowCount(0);
                
                Vector columns = new Vector();
                
                if(cbGames.getSelectedIndex() == 0)
                    columns.add("Game");
                
                columns.add("Server");
                columns.add("Player");

                model.setColumnIdentifiers(columns);
                
                
                for(int i = 0; i < Server.getServerCount(); i++)
                {
                        Server sv = Server.get(i);
                        String gameName = sv.game;
                        
                        if(cbGames.getSelectedIndex() != 0 && !gameName.equals(gameFilter))
                                continue;
                        

                        if(gameName.isEmpty() || !sv.status.reachable)
                                continue;
                        
                        ArrayList<String> playerList = sv.status.playerList.getCleanList(Colorset.getColorSet(sv.game));

                        for(int b = 0; b < playerList.size(); b++)
                        {
                            String player = new String(playerList.get(b));

                            Vector v = new Vector();
                            if(cbGames.getSelectedIndex() == 0)
                                v.add(Server.get(i).game);
                            v.add(Server.get(i).altName);
                            v.add(player);

                            if((btnStartsWith.isSelected() && player.toLowerCase().startsWith(txtSearch.getText().toLowerCase()))
                            || (btnContains.isSelected() && player.toLowerCase().contains(txtSearch.getText().toLowerCase())))
                            {
                                    model.addRow(v);
                            }
                        }
                }
            }
        });
        
        tblResults = new JTable();
        tblResults.setAutoCreateRowSorter(true);
        
        scrlResults = new JScrollPane(tblResults);
        
        
        Vector columnNames = new Vector();
        columnNames.add("Server");
        columnNames.add("Player");
        
        model = new DefaultTableModel(columnNames, 0);
        tblResults.setModel(model);
        
        add(btnStartsWith);
        add(btnContains);
        
        add(txtSearch);
        add(cbGames);
        
        add(scrlResults);
    }
    
    private void initLayout()
    {
        layout = new SpringLayout();
        
        this.setLayout(layout);
        
        /**
         * Radio buttons
         */
        layout.putConstraint(SpringLayout.NORTH, btnStartsWith,
                             5,
                             SpringLayout.NORTH, this.getContentPane());
        layout.putConstraint(SpringLayout.WEST, btnStartsWith,
                             5,
                             SpringLayout.WEST, this.getContentPane());
        
        layout.putConstraint(SpringLayout.NORTH, btnContains,
                             5,
                             SpringLayout.NORTH, this.getContentPane());
        layout.putConstraint(SpringLayout.WEST, btnContains,
                             btnStartsWith.getPreferredSize().width + 5,
                             SpringLayout.WEST, btnStartsWith);
        
        /**
         * Search field
         */
        layout.putConstraint(SpringLayout.NORTH, txtSearch,
                             btnStartsWith.getPreferredSize().height + 5,
                             SpringLayout.NORTH, btnStartsWith);
        layout.putConstraint(SpringLayout.WEST, txtSearch,
                             5,
                             SpringLayout.WEST, this.getContentPane());
        
        /**
         * Game combobox
         */
        layout.putConstraint(SpringLayout.NORTH, cbGames,
                             btnStartsWith.getPreferredSize().height + 5,
                             SpringLayout.NORTH, btnStartsWith);
        layout.putConstraint(SpringLayout.EAST, cbGames,
                             -5,
                             SpringLayout.EAST, this.getContentPane());
        
        /**
         * Resultstable
         */
        layout.putConstraint(SpringLayout.NORTH, scrlResults,
                             txtSearch.getPreferredSize().height + 10,
                             SpringLayout.NORTH, txtSearch);
        layout.putConstraint(SpringLayout.WEST, scrlResults,
                             5,
                             SpringLayout.WEST, this.getContentPane());
    }

    @Override
    public void resize()
    {
        txtSearch.setPreferredSize(Resizer.calculateResize(this.getContentPane(), null, cbGames, null, null, new Dimension(10, -1), new Dimension(-1, 25)));
        scrlResults.setPreferredSize(Resizer.calculateResize(this.getContentPane(), null, null, txtSearch, null, new Dimension(10, 10), null));

        txtSearch.revalidate();
        cbGames.revalidate();
        tblResults.revalidate();
        scrlResults.revalidate();
        this.revalidate();
    }
}
