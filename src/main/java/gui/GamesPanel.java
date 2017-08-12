package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import datatypes.Game;
import main.Controller;
import windowmanager.Resizer;

/**
 * This class is the UI for the game manager in the options.
 */
public class GamesPanel extends JPanel
{
    private JTable                      gameTable;
    private JButton                     btnAdd, btnRemove, btnEdit;
    private SpringLayout                layout;
    private DefaultTableModel           model;
    private JScrollPane                 scrlTable;
    private ArrayList<TableCellEditor>  cbCellEditors = new ArrayList();
    private ArrayList<TableCellEditor>  fcCellEditors = new ArrayList();
    
    GamesPanel()
    {
        initComponents();
        initLayout();
        initListener();
    }
    
    /**
     * Loads the informations for the list and sets them (name, protocol).
     * Clears the table, then all games get added to the table and it will be repainted.
     */
    public void loadData()
    {
        ArrayList<String> games = new ArrayList(Game.getGameMap().keySet());
        
        model.setRowCount(0);
        
        for(int i = 0; i < Game.getGameCount(); i++)
        {
            addRow(Game.get(games.get(i)));
        }
        
        gameTable.setModel(model);
        gameTable.repaint();
    }
    
    private void addRow(Game g)
    {
        if(g == null)
            return;
        
        String[] rowData = new String[4];
        
        rowData[0] = g.name;
        rowData[1] = g.executable;
        rowData[2] = g.protocol;
        
        if(!Controller.protocols.containsKey(g.protocol))
        {
            if(!g.loaded)
            {
                rowData[3] = "<html><a color = \"#BF0000\">Failed to load (Game & Protocol)</a></html>";
                return;
            }

            rowData[3] = "<html><a color = \"#BF0000\">Failed to load (Protocol)</a></html>";
        }
        else if(!g.loaded)
        {
            rowData[3] = "<html><a color = \"#BF0000\">Failed to load (Game)</a></html>";
        }
        else
        {
            rowData[3] = "<html><a color = \"#45FF24\">Loaded</a></html>";
        }
        
        model.addRow(rowData);
    }
    
    private void initComponents()
    {
        gameTable = new JTable();
        
        scrlTable = new JScrollPane(gameTable);
        
        String[] columnNames = new String[4];
        columnNames[0] = "Game";
        columnNames[1] = "Executable";
        columnNames[2] = "Protocol";
        columnNames[3] = "Status";
        
        model = new DefaultTableModel(columnNames, 0)
        {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex)
            {
                return false;
            }
        };
        gameTable.setModel(model);
        gameTable.setAutoCreateRowSorter(true);
        
        btnAdd = new JButton("Add");
        btnAdd.setPreferredSize(new Dimension(100, 25));
        
        btnRemove = new JButton("Remove");
        btnRemove.setPreferredSize(new Dimension(100, 25));
        
        btnEdit = new JButton("Edit");
        btnEdit.setPreferredSize(new Dimension(100, 25));
        
        
        add(scrlTable);
        add(btnAdd);
        add(btnRemove);
        add(btnEdit);
    }
    
    private void initLayout()
    {
        layout = new SpringLayout();
        this.setLayout(layout);
        
        int buttonHeight = btnAdd.getPreferredSize().height+5;
        
        layout.putConstraint(SpringLayout.NORTH, scrlTable,
                             0,
                             SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.WEST, scrlTable,
                             0,
                             SpringLayout.WEST, this);
        
        layout.putConstraint(SpringLayout.NORTH, btnAdd,
                             10,
                             SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.EAST, btnAdd,
                             -5,
                             SpringLayout.EAST, this);
        
        layout.putConstraint(SpringLayout.NORTH, btnRemove,
                             buttonHeight,
                             SpringLayout.NORTH, btnAdd);
        layout.putConstraint(SpringLayout.EAST, btnRemove,
                             -5,
                             SpringLayout.EAST, this);
        
        layout.putConstraint(SpringLayout.NORTH, btnEdit,
                             buttonHeight,
                             SpringLayout.NORTH, btnRemove);
        layout.putConstraint(SpringLayout.EAST, btnEdit,
                             -5,
                             SpringLayout.EAST, this);
    }
    
    private void initListener()
    {
        this.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent evt)
            {
                resize();
            }
        });
        
        btnAdd.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                EditGameDialog tx = new EditGameDialog(null);
                
                Game g = tx.getGame();
                
                if(g != null)
                    addRow(g);
            }
        });
        
        btnRemove.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(gameTable.getSelectedRow() < 0 || gameTable.getSelectedRow() > gameTable.getRowCount())
                    return;
                
                Game.get((String)model.getValueAt(gameTable.getSelectedRow(), 0)).delete();
                File f = new File("./Games/" + (String)model.getValueAt(gameTable.getSelectedRow(), 0) + ".game");
                f.delete();
                model.removeRow(gameTable.getSelectedRow());
            }
        });
        
        
        btnEdit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(gameTable.getSelectedRow() < 0 || gameTable.getSelectedRow() > gameTable.getRowCount())
                    return;
                
                Game g = Game.get((String)model.getValueAt(gameTable.getSelectedRow(), 0));
                
                EditGameDialog tx = new EditGameDialog(g);
                
                loadData();
            }
        });
    }
    
    
    private void resize()
    {
        scrlTable.setPreferredSize(Resizer.calculateResize(this, null, btnAdd, null, null, new Dimension(5, 0), null));
        scrlTable.revalidate();
    }
}