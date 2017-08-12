/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.servergametree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Pascal Schmidt
 */
public class ServerTreeNode extends DefaultMutableTreeNode
{
    int listIndex;
    
    public ServerTreeNode(String txt, int id)
    {
        super(txt);
        
        listIndex = id;
    }
}
