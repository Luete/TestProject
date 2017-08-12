package windowmanager;

import java.awt.Container;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * With this class it is possible to dock one window to another.
 * There's a "main" and a "sub" window. The sub windows size and location depends on the main ones.
 */
public class Dock
{
    private Container main;
    private Container sub;
    private int     mainyPos;
    private int     subDock=0;
    
    /**
     * Sets the Window as main.
     * Listens for moves and resizes, if moved or resized the update method gets called.
     * 
     * @param wnd The window 
     */
    public void setMain(Container wnd)
    {
        main = wnd;
        main.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentMoved(ComponentEvent evt)
            {
                update(true);
            }
            @Override
            public void componentResized(ComponentEvent evt)
            {
                update(false);
            }
        });
    }
    
    /**Container
     * Sets the window as second window.
     * Adds listeners for the window, if moved or resized the update method gets called.
     * 
     * @param wnd The window 
     */
    public void setSub(Container wnd)
    {
        sub = wnd;
        sub.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentMoved(ComponentEvent evt)
            {
                update(true);
            }
            @Override
            public void componentResized(ComponentEvent evt)
            {
                if(evt.getComponent() == sub)
                    update(false); //only call if event source == sub
            }
        });
    }
    
    private void update(boolean moved)
    {
        if(!(main.isVisible()
        && sub.getLocation().y + sub.getSize().height > main.getLocation().y
        && sub.getLocation().y < main.getLocation().y + main.getSize().height))
            return;
        
        int yPos;
        
        //weird errors if we directly access those variables.
        int mainLoc = main.getLocation().y;
        int mainHeight = main.getSize().height;
        
        
        if((subDock & (1 << 1)) == 0 && (subDock & (1 << 2)) == 0) //if not already docked. just spawn at standard position
            yPos = sub.getLocation().y;
        else
            yPos = sub.getLocation().y-(mainyPos-mainLoc);
        
        if((!sub.hasFocus() && (subDock & (1 << 1)) != 0)
        || (main.getLocation().x + main.getSize().width+10 >= sub.getLocation().x
        && main.getLocation().x + main.getSize().width-10 <= sub.getLocation().x))
        { //dock right of main
            sub.setLocation(main.getLocation().x + main.getSize().width, yPos);
            subDock |= 1 << 1;
        }
        else
        {
            subDock &= ~(1 << 1);
            
            if((!sub.hasFocus() && (subDock & (1 << 2)) != 0)
            || (main.getLocation().x >= sub.getLocation().x + sub.getSize().width-10
            && main.getLocation().x <= sub.getLocation().x + sub.getSize().width+10))
            { //dock left of main
                sub.setLocation(main.getLocation().x - sub.getSize().width, yPos);
                subDock |= 1 << 2;
            }
            else
            {
                subDock &= ~(1 << 2);
                
                subDock &= ~(1 << 3);
                subDock &= ~(1 << 4);
                return;
            }
        }
        
        if((subDock & (1 << 3)) != 0 || (sub.getLocation().y+10 >= mainLoc && sub.getLocation().y-10 <= mainLoc))
        {
            sub.setLocation(sub.getLocation().x, mainLoc);
            subDock |= 1 << 3;
        }
        else
            subDock &= ~(1 << 3);

        if((subDock & (1 << 4)) != 0 || (sub.getLocation().y+sub.getSize().height+10 >= mainLoc+mainHeight && sub.getLocation().y+sub.getSize().height-10 <= mainLoc+mainHeight))
        {
            subDock |= 1 << 4;
            
            if((subDock & (1 << 3)) == 0) sub.setLocation(sub.getLocation().x, mainLoc+mainHeight-sub.getSize().height);
        }
        else
            subDock &= ~(1 << 4);
        
        if((subDock & (1 << 3)) != 0 && (subDock & (1 << 4)) != 0 && moved == false)
        {
            sub.setSize(sub.getSize().width, mainHeight);
            
            ComponentEvent e = new ComponentEvent(main, ComponentEvent.COMPONENT_RESIZED);
            
            for(ComponentListener a: sub.getComponentListeners())
            {
                a.componentResized(e);
            }
        }
        
        mainyPos = mainLoc;
    }
}
