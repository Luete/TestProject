package gui.servergametree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import datatypes.Game;
import datatypes.Server;
import datatypes.ServerStatus;
import de.luete.gamebrowser.main.ApplicationSettings;
import gui.PlayerDialog;
import main.Controller;
 
/**
 * Nicely looking JTree.
 */
public class ServerTree extends JTree
{
    private ServerTreeNode root;
    private DefaultTreeModel model;
    
    public ServerTree(ServerTreeNode pRoot)
    {
        super(pRoot);
        
        root = pRoot;
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        this.setBackground(ApplicationSettings.colors.getColor("serverlist"));
        
        //This enables dynamic row heights.
        this.setRowHeight(0);
        
        //We don't need a root node..
        this.expandRow(0);
        this.setRootVisible(false);
        this.setShowsRootHandles(true);
        
        
        this.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                newSelection(e.getX(), e.getY());
                
                if(e.getClickCount() == 2)
                    Controller.start(getSelectedServerIndex());
                
                toggleNode(e.getX(), e.getY());
            }
        });
        
        //Use our custom cell renderer.
        this.setCellRenderer( new ServerTreeCellRenderer() );
        //Use our custom tree UI.
        this.setUI( new ServerTreeUI() );
    }
    
    public int getSelectedServerIndex()
    {
        if(this.getSelectionCount() == 0)
            return -1;
        
        ServerTreeNode stn = (ServerTreeNode)this.getSelectionPath().getLastPathComponent();
        
        return stn.listIndex;
    }
    
    private void toggleNode(int x, int y)
    {
        int row = this.getClosestRowForLocation(x, y);
        if(this.isExpanded(row))
            this.collapseRow(row);
        else
            this.expandRow(row);
    }
    
    private void newSelection(int x, int y)
    {
        this.setSelectionRow(this.getClosestRowForLocation(x, y));
        
        
        /**
         * Setting the playerlist
         */
        ServerTreeNode sv = (ServerTreeNode)this.getSelectionPath().getLastPathComponent();
        Server svr = Server.get(sv.listIndex);
        
        //Don't clear the list when we collaps/expand a category.
        if(svr == null)
        {
            if(sv.isLeaf())
                PlayerDialog.getInstance().setList(null, null);
            
            return;
        }
        
        ServerStatus st = svr.status;
        PlayerDialog.getInstance().setList(st.playerList, Game.get(svr.game));
    }
    
    public void initData()
    {
        Map<String, Map<Integer, String>> treeData = new TreeMap(new Comparator<String>(){
            @Override
            public int compare(String o1, String o2) {              
                return o1.compareToIgnoreCase(o2);
            }
        });
        
        root = new ServerTreeNode("", -1);
        
        //Load data into a sorted map!
        for(int i = 0; i < Server.getServerCount(); i++)
        {
            Server sv = Server.get(i);
            
            if(treeData.get(sv.game) == null)
            {
                treeData.put(sv.game, new TreeMap());
            }
            
            treeData.get(sv.game).put(i, sv.altName);
        }
        
        
        
        //Create nodes for the data.
        for (Map.Entry<String, Map<Integer, String>> entry : treeData.entrySet())
        {
            ServerTreeNode game = new ServerTreeNode(entry.getKey(), -1);
            root.add(game);
            
            for(Map.Entry<Integer, String> games : entry.getValue().entrySet())
            {
                game.add(new ServerTreeNode(games.getValue(), games.getKey()));
            }
        }
        
        model = new DefaultTreeModel(root);
        this.setModel(model);
    }
}