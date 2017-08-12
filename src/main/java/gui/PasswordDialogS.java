package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import windowmanager.Resizable;
import windowmanager.Resizer;

/**
 * A dialog to enter the server txtPassword.
 * You can use the getPassword() function to return it.
 */
public class PasswordDialogS extends JDialog implements Resizable
{
    private JTextField  txtPassword;
    private boolean     confirmedPassword;
    
    public PasswordDialogS()
    {
        super(Window.getInstance(), "Enter password", true);
        
        SpringLayout layout = new SpringLayout();
        this.setLayout(layout);
        
        this.setSize(new Dimension(180, 65));
        
        //Center dialog
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        
        
        txtPassword = new JTextField();
        
        
        layout.putConstraint(SpringLayout.NORTH, txtPassword,
                             5,
                             SpringLayout.NORTH, this.getContentPane());
        layout.putConstraint(SpringLayout.WEST, txtPassword,
                             5,
                             SpringLayout.WEST, this.getContentPane());
        
        add(txtPassword);
        
        txtPassword.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                dispose();
                setVisible(false);
                confirmedPassword = true;
            }
        });
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                confirmedPassword = false;
            }
        });
        
        Resizer.addToResizer(this);
    }
    
    
    /**
     * Displays the dialog and clears the JTextField.
     */
    public void display()
    {
        txtPassword.setText("");
        
        this.setVisible(true);
    }
    
    /**
     * If the pw is confirmed it returns the txtPassword. Elsewise null.
     * The txtPassword counts as confirmed if the dialog was closed by hitting enter in the JTextField.
     * @return Password 
     */
    public String getPassword()
    {
        if(confirmedPassword)
            return txtPassword.getText();
        else
            return null;
    }
    
    @Override
    public void resize()
    {
        txtPassword.setPreferredSize(Resizer.calculateResize(this.getContentPane(), null, null, null, null, new Dimension(10, 10), null));
        txtPassword.revalidate();
        this.setSize(this.getSize().width, 65);
        this.revalidate();
    }
}
