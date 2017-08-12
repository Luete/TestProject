package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import windowmanager.Resizable;
import windowmanager.Resizer;

/**
 * A dialog to enter the server txtPassword.
 * You can use the getPassword() function to return it.
 */
public class PasswordDialog extends JDialog implements Resizable
{
    private JLabel      lblUsername, lblPassword;
    private JTextField  txtUsername, txtPassword;
    private boolean     confirmed;
    
    public PasswordDialog()
    {
        super(Window.getInstance(), "Login data", true);
        
        this.setSize(new Dimension(220, 100));
        
        initComponents();
        initListener();
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        
        Resizer.addToResizer(this);
    }
    
    /**
     * Displays the dialog and clears the JTextFields.
     */
    public void display()
    {
        txtUsername.setText("");
        txtPassword.setText("");
        
        this.setVisible(true);
    }
    
    private void initComponents()
    {
        lblUsername = new JLabel("Username");
        lblPassword = new JLabel("Password");
        
        txtUsername = new JTextField();
        txtPassword = new JTextField();
        
        
        add(lblUsername);
        add(lblPassword);
        add(txtUsername);
        add(txtPassword);
        
        initLayout();
    }
    
    private void initLayout()
    {
        SpringLayout layout = new SpringLayout();
        this.setLayout(layout);
        
        layout.putConstraint(SpringLayout.NORTH, lblUsername,
                             11,
                             SpringLayout.NORTH, this.getContentPane());
        layout.putConstraint(SpringLayout.WEST, lblUsername,
                             5,
                             SpringLayout.WEST, this.getContentPane());
        
        layout.putConstraint(SpringLayout.SOUTH, lblPassword,
                             -11,
                             SpringLayout.SOUTH, this.getContentPane());
        layout.putConstraint(SpringLayout.WEST, lblPassword,
                             5,
                             SpringLayout.WEST, this.getContentPane());
        
        
        layout.putConstraint(SpringLayout.NORTH, txtUsername,
                             5,
                             SpringLayout.NORTH, this.getContentPane());
        layout.putConstraint(SpringLayout.EAST, txtUsername,
                             -5,
                             SpringLayout.EAST, this.getContentPane());
        
        layout.putConstraint(SpringLayout.NORTH, txtPassword,
                             30,
                             SpringLayout.NORTH, txtUsername);
        layout.putConstraint(SpringLayout.EAST, txtPassword,
                             -5,
                             SpringLayout.EAST, this.getContentPane());
    }
    
    private void initListener()
    {
        txtUsername.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                confirm();
            }
        });
        
        txtPassword.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                confirm();
            }
        });
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                confirmed = false;
            }
        });
    }
    
    /**
     * If the the user hast hit Enter in a textfield it is confirmed it returns the txtPassword, elsewise null.
     * The txtPassword counts as confirmed if the dialog was closed by hitting enter in the JTextField.
     * @return Content of txtPassword
     */
    public String getPassword()
    {
        if(confirmed)
            return txtPassword.getText();
        else
            return null;
    }
    
    /**
     * If the user has hit Enter in a textfield it is confirmed it returns the txtUsername, elsewise null.
     * The txtUsername counts as confirmed if the dialog was closed by hitting enter in the JTextField.
     * @return Content of txtUsername 
     */
    public String getUsername()
    {
        if(confirmed)
            return txtUsername.getText();
        else
            return null;
    }
    
    private void confirm()
    {
        dispose();
        setVisible(false);
        confirmed = true;
    }
    
    @Override
    public void resize()
    {
        txtUsername.setPreferredSize(Resizer.calculateResize(this.getContentPane(), lblPassword, null, null, null, new Dimension(20, 10), new Dimension(-1, 25)));
        txtPassword.setPreferredSize(Resizer.calculateResize(this.getContentPane(), lblPassword, null, null, null, new Dimension(20, 10), new Dimension(-1, 25)));
        
        lblUsername.revalidate();
        lblPassword.revalidate();
        
        txtUsername.revalidate();
        txtPassword.revalidate();
        
        this.setSize(this.getSize().width, 100);
        this.revalidate();
    }
}
