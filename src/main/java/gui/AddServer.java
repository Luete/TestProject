package gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import datatypes.Game;
import datatypes.Server;
import de.luete.gamebrowser.main.ApplicationSettings;
import main.Controller;
import windowmanager.Resizable;
import windowmanager.Resizer;

/**
 * This is the Addserver AND Editserver dialog.
 * This is class uses the Singleton pattern, this means there can only exist one object.
 */
public class AddServer extends JDialog implements Resizable
{
    private JButton      btnSave, btnHelp;
    private SpringLayout layout;
    private JLabel       lblName, lblIP, lblPort;
    private JTextField   txtName, txtIP, txtPort;
    private JTextArea    txtBatch;
    private JScrollPane  scrlBatch;
    private int          svID = -1;
    private JComboBox    cbGame;
    private DefaultComboBoxModel model;
    
    private AddServer()
    {
        super(Window.getInstance(), "", true);
        
        this.setMinimumSize(new Dimension(350, 300));
        
        if(ApplicationSettings.addBounds == null)
        {
            this.setSize(new Dimension(450, 300));
            
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        }
        else
        {
            this.setSize(new Dimension(ApplicationSettings.addBounds.width, ApplicationSettings.addBounds.height));
            this.setLocation(ApplicationSettings.addBounds.x, ApplicationSettings.addBounds.y);
        }
        
        initComponents();
        initListener();
        
        Resizer.addToResizer(this);
    }
    
    //clears the comboBox and adds the loaded games.
    private void loadGames()
    {
        model.removeAllElements();
        
        File dir = new File("./Games/");

        String[] fileList = dir.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File d, String name)
            {
                return name.endsWith(".game");
            }
        });
        
        for(int i = 0; i < fileList.length; i++)
        {
            model.addElement(fileList[i].replace(".game", ""));
        }
        cbGame.setModel(model);
    }
    
    /**
     * Opens a new dialog with empty fields.
     * The standard batch content will be set and the games loaded.
     */
    public void addServer()
    {
        this.setTitle("Add a new server");
        txtBatch.setText(new Game().script); //Set default script.
        txtBatch.setCaretPosition(0);
        svID = -1;
        
        loadGames();
        
        this.setVisible(true);
    }
    
    /**
     * Opens a new dialog and fills the fields.
     * @param sv 
     */
    public void editServer(Server sv)
    {
        this.setTitle("Edit server (ID: "+sv.id+")");
        
        loadGames();
        
        
        txtName.setText(sv.altName);
        txtIP.setText(sv.IP);
        txtPort.setText(String.valueOf(sv.PORT));
        txtBatch.setText(sv.script);
        txtBatch.setCaretPosition(0);
        svID = sv.id;
        
        if(Game.get(sv.game) == null && !Controller.loadProtocol(sv.game))
            model.addElement(sv.game);
        
        cbGame.setSelectedItem(sv.game);
        
        this.setVisible(true);
    }
    
    private void initComponents()
    {
        model = new DefaultComboBoxModel();
        
        btnSave = new JButton("Save");
        btnHelp = new JButton("Help");
        
        cbGame = new JComboBox();
        cbGame.setEditable(false);
        cbGame.setPreferredSize(new Dimension(125, btnSave.getPreferredSize().height));

        lblName = new JLabel("Display Name");
        lblIP = new JLabel("IP/Hostadress");
        lblPort = new JLabel("Port");

        lblName.setFont(new Font("Serif", Font.PLAIN, 13));
        lblIP.setFont(new Font("Serif", Font.PLAIN, 13));
        lblPort.setFont(new Font("Serif", Font.PLAIN, 13));

        txtBatch = new JTextArea();
        scrlBatch = new JScrollPane(txtBatch);
        
        txtName = new JTextField();
        
        txtIP = new JTextField();
        
        txtPort = new JTextField();
        
        initLayout();

        add(cbGame);
        add(lblName);
        add(lblIP);
        add(lblPort);

        add(txtName);
        add(txtIP);
        add(txtPort);

        add(scrlBatch);

        add(btnSave);
        add(btnHelp);
    }

    private void initListener()
    {
        btnSave.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                save();
            }
        });
        btnHelp.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(null, "Here you can add and edit server configurations.\n"
                        + "The big textarea is used to store batch/shell commands which can be executed when connecting to your game.\n"
                        + "You can use these self-explaining variables: %IP% %PORT% %GAMEPATH% %EXECUTABLE% %PASSWORD%\n\n"
                        + "If you have more questions RTFM. (Read the manual [readme.txt])", "Help!", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                close();
            }
        });
        
        cbGame.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                if(txtName.getText().isEmpty())
                {
                    Game g = Game.get((String)e.getItem());
                    if(g != null && g.loaded)
                    {
                        txtBatch.setText(g.script);
                        
                        if(g.standardPort != -1) txtPort.setText(String.valueOf(g.standardPort));
                        else txtPort.setText("");
                    }
                }
            }
        });
    }

    private void initLayout()
    {
        layout = new SpringLayout();
        this.setLayout(layout);
        Container contentPane = this.getContentPane();

        //Labels
        layout.putConstraint(SpringLayout.NORTH, lblName,
                             20,
                             SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.WEST, lblName,
                             10,
                             SpringLayout.WEST, contentPane);

        layout.putConstraint(SpringLayout.NORTH, lblIP,
                             30,
                             SpringLayout.NORTH, lblName);
        layout.putConstraint(SpringLayout.WEST, lblIP,
                             10,
                             SpringLayout.WEST, contentPane);

        layout.putConstraint(SpringLayout.NORTH, lblPort,
                             30,
                             SpringLayout.NORTH, lblIP);
        layout.putConstraint(SpringLayout.WEST, lblPort,
                             10,
                             SpringLayout.WEST, contentPane);


        //TextFields
        layout.putConstraint(SpringLayout.NORTH, txtName,
                             15,
                             SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.EAST, txtName,
                             -10,
                             SpringLayout.EAST, contentPane);

        layout.putConstraint(SpringLayout.NORTH, txtIP,
                             30,
                             SpringLayout.NORTH, txtName);
        layout.putConstraint(SpringLayout.EAST, txtIP,
                             -10,
                             SpringLayout.EAST, contentPane);

        layout.putConstraint(SpringLayout.NORTH, txtPort,
                             30,
                             SpringLayout.NORTH, txtIP);
        layout.putConstraint(SpringLayout.EAST, txtPort,
                             -10,
                             SpringLayout.EAST, contentPane);
        
        layout.putConstraint(SpringLayout.SOUTH, cbGame,
                             -10,
                             SpringLayout.SOUTH, contentPane);
        layout.putConstraint(SpringLayout.EAST, cbGame,
                             -(btnSave.getPreferredSize().width+10),
                             SpringLayout.EAST, btnSave);

        //Buttons
        layout.putConstraint(SpringLayout.SOUTH, btnSave,
                             -10,
                             SpringLayout.SOUTH, contentPane);
        layout.putConstraint(SpringLayout.EAST, btnSave,
                             -10,
                             SpringLayout.EAST, contentPane);

        layout.putConstraint(SpringLayout.SOUTH, btnHelp,
                             -10,
                             SpringLayout.SOUTH, contentPane);
        layout.putConstraint(SpringLayout.WEST, btnHelp,
                             10,
                             SpringLayout.WEST, contentPane);

        //Batch textarea
        layout.putConstraint(SpringLayout.NORTH, scrlBatch,
                             30,
                             SpringLayout.NORTH, txtPort);
        layout.putConstraint(SpringLayout.WEST, scrlBatch,
                             10,
                             SpringLayout.WEST, contentPane);
    }

    private void close()
    {
        txtName.setText("");
        txtIP.setText("");
        txtPort.setText("");
        txtBatch.setText("");
        cbGame.setSelectedItem(null);
        svID = -1;
        
        this.dispose();
    }
    
    private void save()
    {
        Server sv = new Server();

        sv.IP = txtIP.getText();
        sv.altName = txtName.getText();
        sv.script = txtBatch.getText();
        sv.game = (String)cbGame.getSelectedItem();
        sv.id = svID;
        
        
        if (sv.IP.isEmpty())
        {
            JOptionPane.showMessageDialog(this,
                                          "Enter server IP to continue.",
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (sv.altName.isEmpty())
        {
            JOptionPane.showMessageDialog(this,
                                          "Enter server name to continue.",
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }
        try
        {
            sv.PORT = Integer.parseInt(txtPort.getText());
        }
        catch (java.lang.NumberFormatException ex)
        {
            JOptionPane.showMessageDialog(this,
                                          "The port has to be a numerical value.",
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        
        sv.save();
        
        int index = Server.getSvIndexByID(svID);
        if(index != -1)
            Server.get(index).reload(true);
        else
            Server.add(sv);
        
        Game.loadGame(sv.game);
        
        close();
    }
    
    
    private static class SingletonHolder
    { 
        private static final AddServer INSTANCE = new AddServer();
    }

    public static AddServer getInstance()
    {
        return SingletonHolder.INSTANCE;
    }
    
    @Override
    public Object clone()
    {
        return null;
    }

    @Override
    public void resize()
    {
        txtIP.setPreferredSize(Resizer.calculateResize(this.getContentPane(), lblIP, null, null, null, new Dimension(40, 10), new Dimension(-1, 25)));
        txtName.setPreferredSize(txtIP.getPreferredSize());
        txtPort.setPreferredSize(txtIP.getPreferredSize());
        
        scrlBatch.setPreferredSize(Resizer.calculateResize(this.getContentPane(), null, null, txtPort, btnSave, new Dimension(20, 10), new Dimension(-1, -1)));
        
        txtIP.revalidate();
        txtName.revalidate();
        txtPort.revalidate();
        cbGame.revalidate();
        scrlBatch.revalidate();
        this.revalidate();
    }
}