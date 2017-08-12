package gui.servergametree;

 
import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import datatypes.Server;
import de.luete.gamebrowser.main.ApplicationSettings;
 
/**
 * TreeCellRenderer with alternating colored rows.
 */
public class ServerTreeCellRenderer extends DefaultTreeCellRenderer
{
    DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
    private ImageIcon icCollapsed, icExpanded, hasPassword, hasNoPassword;
    
    public ServerTreeCellRenderer()
    {
        //Loads icons
        icCollapsed = new ImageIcon("./Icons/TreeCollapsed.png");
        icExpanded = new ImageIcon("./Icons/TreeExpanded.png");
        hasPassword = new ImageIcon("./Icons/cross.png");
        hasNoPassword = new ImageIcon("./Icons/tick.png");
    }
    
    @Override
    public Component getTreeCellRendererComponent( JTree aTree, Object aValue, boolean isSelected,
        boolean isExpanded, boolean isLeaf, int aRow, boolean aHasFocus )
    {
        ServerTreeNode node = (ServerTreeNode)aValue;
        Server sv = Server.get(node.listIndex);
        
        setFont(new Font("TimesRoman", Font.PLAIN, 14));
        if(sv != null) setText(prepareStatusString(sv));
        else           setText((String)node.getUserObject());
        
        setEnabled(aTree.isEnabled());
        setOpaque(true);
            
       
        if(isLeaf && sv != null)
        {
            if(Server.get(node.listIndex).status.reachable)
            {
                if(isSelected)
                    setBackground(ApplicationSettings.colors.getColor("reachableselected"));
                else
                    setBackground(ApplicationSettings.colors.getColor("reachable"));
                
                if(Server.get(node.listIndex).status.hasPassword)
                    this.setIcon(hasPassword);
                else
                    this.setIcon(hasNoPassword);
            }
            else
            {
                this.setIcon(null);
                
                if(isSelected)
                    setBackground(ApplicationSettings.colors.getColor("unreachableselected"));
                else
                    setBackground(ApplicationSettings.colors.getColor("unreachable"));
            }
        }
        else
        {
            if(isExpanded)
                this.setIcon(icExpanded);
            else
                this.setIcon(icCollapsed);
            
            setBackground(ApplicationSettings.colors.getColor("category"));
        }
        
        
        return this;
    }
    
    private String prepareStatusString(Server sv)
    {
        String text = "<html>";
        if (ApplicationSettings.displayMaps && sv.status.reachable)
            text += "<font size = 3>(" + sv.status.getNumPlayers(ApplicationSettings.onlyRealPlayers) + ") " + sv.altName + "</font><br><font size = 2 color=\"#001EA6\">"
                    + ("000" + sv.status.ping).substring(String.valueOf(sv.status.ping).length()) + " " + sv.status.getMap() + "</font>";
        else
            text += sv.altName;

        text += "</html>";
        
        return text;
    }
}