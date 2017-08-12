package gui.servergametree;
 
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

import datatypes.Server;
import de.luete.gamebrowser.main.ApplicationSettings;
 
/**
 * Extended BasicTreeUI with alternating colored rows.
 */
public class ServerTreeUI extends BasicTreeUI
{
    @Override
    protected void paintRow( Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds,
        TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf )
    {
        boolean isSelected = tree.isRowSelected( row );
        
        ServerTreeNode sv = (ServerTreeNode)path.getLastPathComponent();
        
        bounds.x = 10;
        clipBounds.x = 10;
        
        Graphics g2 = g.create();
        
        if(isLeaf )
        {
            if(sv.listIndex >= 0 && Server.get(sv.listIndex).status.reachable)
            {
                if(isSelected)
                    g2.setColor(ApplicationSettings.colors.getColor("reachableselected"));
                else
                    g2.setColor(ApplicationSettings.colors.getColor("reachable"));
            }
            else
            {
                if(isSelected)
                    g2.setColor(ApplicationSettings.colors.getColor("unreachableselected"));
                else
                    g2.setColor(ApplicationSettings.colors.getColor("unreachable"));
            }
        }
        else
        {
            bounds.x = 5;
            clipBounds.x = 5;
            g2.setColor(ApplicationSettings.colors.getColor("category"));
        }
        
        g2.fillRect( 0, bounds.y, tree.getWidth(), bounds.height );
        g2.dispose();
        
        super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
    }
}