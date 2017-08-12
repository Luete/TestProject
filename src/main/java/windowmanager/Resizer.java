package windowmanager;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;


public class Resizer
{
    public static void addToResizer(final Resizable r)
    {
        Component c = (Component)r;
        
        c.addComponentListener(new ComponentListener()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                r.resize();
            }

            @Override
            public void componentMoved(ComponentEvent e)
            {
            }

            @Override
            public void componentShown(ComponentEvent e)
            {
            }

            @Override
            public void componentHidden(ComponentEvent e)
            {
            }
        });
    }

    /**
     * Calculates the size of a component depending on other components.
     * This resizes UI-components. It will fail if a components location depends on another components size.
     * You can set a fixed size for an axis and how much border space should be left.
     *
     * @param contentPane The contentPane that holds the components.
     * @param left The left component.
     * @param right The right component.
     * @param top The top component.
     * @param bottom The bottom component.
     * @param borders The space there is between the components (multiply by 2).
     * @param fixedSize If there is a fixedSize set for an axis, it won't stretch.
     * @return Returns the calculated size for the element.
     */
    public static Dimension calculateResize(Container contentPane, Component left, Component right, Component top, Component bottom, Dimension borders, Dimension fixedSize)
    {
        int w;
        int h;
        if (fixedSize == null || fixedSize.width < 0) {
            if (left == null && right == null) {
                w = contentPane.getSize().width;
            } else if (left != null && right == null) {
                w = contentPane.getSize().width - (left.getLocation().x + left.getPreferredSize().width);
            } else if (left == null && right != null) {
                w = contentPane.getSize().width - (contentPane.getSize().width - right.getLocation().x);
            } else {
                w = right.getLocation().x - (left.getLocation().x + left.getPreferredSize().width);
            }
            w -= borders.width;
        } else {
            w = fixedSize.width;
        }
        if (fixedSize == null || fixedSize.height < 0) {
            if (top == null && bottom == null) {
                h = contentPane.getSize().height;
            } else if (top != null && bottom == null) {
                h = contentPane.getSize().height - (top.getLocation().y + top.getPreferredSize().height);
            } else if (top == null && bottom != null) {
                h = contentPane.getSize().height - (contentPane.getSize().height - bottom.getLocation().y);
            } else {
                h = bottom.getLocation().y - (top.getLocation().y + top.getPreferredSize().height);
            }
            h -= borders.height;
        } else {
            h = fixedSize.height;
        }
        return new Dimension(w, h);
    }
}
