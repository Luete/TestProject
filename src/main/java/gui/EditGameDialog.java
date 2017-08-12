package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import datatypes.Colorset;
import datatypes.Game;
import main.Controller;
import windowmanager.Resizable;
import windowmanager.Resizer;

/**
 * This class represents the dialog used to edit a game.
 */
public class EditGameDialog extends JDialog implements Resizable
{
    private SpringLayout    layout;
    private JScrollPane     scrlTxt;
    private JTextArea       txtArea;
    
    private JLabel          lblQueryPort, lblStandardPort, lblName, lblExecutable, lblProtocols, lblColorSets;
    private JTextField      txtQueryPort, txtStandardPort, txtName, txtExecutable;
    
    private JComboBox       cbProtocols, cbColorSets;
    
    private JButton         btnConfirm, btnCancel, btnBrowse;
    private Game            game;
    
    private boolean         editMode;
    
    public EditGameDialog(Game g)
    {
        super(Window.getInstance(), "Edit game", true);
        game = g;
        
        initComponents();
        
        if(g != null)
        {
            editMode = true;
            
            txtName.setText(game.name);
            txtExecutable.setText(game.executable);
            
            txtArea.setText(game.script);
            txtQueryPort.setText(game.queryPort);
            if(game.standardPort != -1) txtStandardPort.setText(String.valueOf(game.standardPort));
            else txtStandardPort.setText("");
            
            txtName.setEditable(false);
        }
        else
        {
            editMode = false;
            
            game = new Game();
            txtArea.setText(game.script); //Use the standard script defined in the game class.
            
            txtName.setEditable(true);
        }
        
        initProtocolBox();
        initColorBox();
        
        this.setSize(600, 350);
        
        //Center dialog
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        
        
        Resizer.addToResizer(this);
        this.setVisible(true);
    }
    
    private void initProtocolBox()
    {
        DefaultComboBoxModel cModel = new DefaultComboBoxModel();
        ArrayList<String> protocols = new ArrayList(Controller.protocols.keySet());
        
        
        cModel.addElement("");
        
        for(int i = 0; i < protocols.size(); i++)
            cModel.addElement(protocols.get(i));
        
        //Not in the list yet. So add it.
        if(!Controller.protocols.containsKey(game.protocol))
            cModel.addElement(game.protocol);
        
        cbProtocols.setModel(cModel);
        cbProtocols.setSelectedItem(game.protocol);
    }
    
    private void initColorBox()
    {
        DefaultComboBoxModel cModel = new DefaultComboBoxModel();
        ArrayList<String> colors = new ArrayList(Colorset.getSetNames());
        
        
        cModel.addElement("");
        
        for(int i = 0; i < colors.size(); i++)
            cModel.addElement(colors.get(i));
        
        //Not in the list yet. So add it.
        if(Colorset.getColorSet(game.colorSet) == null)
            cModel.addElement(game.colorSet);
        
        cbColorSets.setModel(cModel);
        cbColorSets.setSelectedItem(game.colorSet);
    }
    
    private void initComponents()
    {
        txtArea = new JTextArea();
        scrlTxt = new JScrollPane(txtArea);
        
        btnConfirm = new JButton("Confirm");
        btnCancel = new JButton("Cancel");
        btnBrowse = new JButton("Browse");
        btnBrowse.setPreferredSize(new Dimension(btnBrowse.getPreferredSize().width, 25));
        
        lblQueryPort = new JLabel("Query port");
        txtQueryPort = new JTextField();
        
        lblProtocols = new JLabel("Protocol");
        cbProtocols = new JComboBox();
        cbProtocols.setPreferredSize(new Dimension(100, 25));
        
        
        lblColorSets = new JLabel("Colorset");
        cbColorSets = new JComboBox();
        cbColorSets.setPreferredSize(new Dimension(100, 25));
        
        lblName = new JLabel("Name");
        txtName = new JTextField();
        lblExecutable = new JLabel("Executable");
        txtExecutable = new JTextField();
        
        lblStandardPort = new JLabel("Standard port");
        txtStandardPort = new JTextField();
        initLayout();
        
        add(lblName);
        add(txtName);
        add(lblExecutable);
        add(txtExecutable);
        add(btnBrowse);
        add(lblStandardPort);
        add(txtStandardPort);
        add(lblQueryPort);
        add(txtQueryPort);
        add(lblProtocols);
        add(cbProtocols);
        add(lblColorSets);
        add(cbColorSets);
        add(scrlTxt);
        add(btnConfirm);
        add(btnCancel);
        
        initListener();
    }
    
    private void initLayout()
    {
        layout = new SpringLayout();
        this.setLayout(layout);
        
        int txtHorOffset =  lblStandardPort.getPreferredSize().width+15;
        int txtVertOffset = txtName.getPreferredSize().height+10;
        final short border = 5;
        
        int labelOffset = txtName.getPreferredSize().height + lblStandardPort.getPreferredSize().height/2 + border;
        
        
        /**
         * Name
         */
        layout.putConstraint(SpringLayout.NORTH, lblName,
                             labelOffset-txtName.getPreferredSize().height,
                             SpringLayout.NORTH, this.getContentPane());
        layout.putConstraint(SpringLayout.WEST, lblName,
                             5,
                             SpringLayout.WEST, this.getContentPane());
        
        layout.putConstraint(SpringLayout.NORTH, txtName,
                             5,
                             SpringLayout.NORTH, this.getContentPane());
        layout.putConstraint(SpringLayout.WEST, txtName,
                             txtHorOffset,
                             SpringLayout.WEST, lblName);
        
        
        /**
         * Executable
         */
        layout.putConstraint(SpringLayout.NORTH, lblExecutable,
                             labelOffset,
                             SpringLayout.NORTH, txtName);
        layout.putConstraint(SpringLayout.WEST, lblExecutable,
                             5,
                             SpringLayout.WEST, this.getContentPane());
        
        layout.putConstraint(SpringLayout.NORTH, txtExecutable,
                             txtVertOffset,
                             SpringLayout.NORTH, txtName);
        layout.putConstraint(SpringLayout.WEST, txtExecutable,
                             txtHorOffset,
                             SpringLayout.WEST, lblExecutable);
        
        layout.putConstraint(SpringLayout.NORTH, btnBrowse,
                             txtVertOffset,
                             SpringLayout.NORTH, txtName);
        layout.putConstraint(SpringLayout.EAST, btnBrowse,
                             -5,
                             SpringLayout.EAST, this.getContentPane());
        
        
        /**
         * Standard port
         */
        layout.putConstraint(SpringLayout.NORTH, lblStandardPort,
                             labelOffset,
                             SpringLayout.NORTH, txtExecutable);
        layout.putConstraint(SpringLayout.WEST, lblStandardPort,
                             5,
                             SpringLayout.WEST, this.getContentPane());
        
        layout.putConstraint(SpringLayout.NORTH, txtStandardPort,
                             txtVertOffset,
                             SpringLayout.NORTH, txtExecutable);
        layout.putConstraint(SpringLayout.WEST, txtStandardPort,
                             txtHorOffset,
                             SpringLayout.WEST, lblQueryPort);
        
        
        /**
         * Query port
         */
        layout.putConstraint(SpringLayout.NORTH, lblQueryPort,
                             labelOffset,
                             SpringLayout.NORTH, txtStandardPort);
        layout.putConstraint(SpringLayout.WEST, lblQueryPort,
                             5,
                             SpringLayout.WEST, this.getContentPane());
        
        layout.putConstraint(SpringLayout.NORTH, txtQueryPort,
                             txtVertOffset,
                             SpringLayout.NORTH, txtStandardPort);
        layout.putConstraint(SpringLayout.WEST, txtQueryPort,
                             txtHorOffset,
                             SpringLayout.WEST, lblQueryPort);

        
        /**
         * Protocol
         */
        layout.putConstraint(SpringLayout.NORTH, lblProtocols,
                             labelOffset,
                             SpringLayout.NORTH, txtQueryPort);
        layout.putConstraint(SpringLayout.WEST, lblProtocols,
                             5,
                             SpringLayout.WEST, this.getContentPane());
        
        layout.putConstraint(SpringLayout.NORTH, cbProtocols,
                             txtVertOffset,
                             SpringLayout.NORTH, txtQueryPort);
        layout.putConstraint(SpringLayout.WEST, cbProtocols,
                             txtHorOffset,
                             SpringLayout.WEST, lblProtocols);
        
        
        /**
         * Colorsets
         */
        layout.putConstraint(SpringLayout.NORTH, lblColorSets,
                             cbProtocols.getPreferredSize().height+5,
                             SpringLayout.NORTH, cbProtocols);
        layout.putConstraint(SpringLayout.WEST, lblColorSets,
                             5,
                             SpringLayout.WEST, this.getContentPane());
        
        layout.putConstraint(SpringLayout.NORTH, cbColorSets,
                             cbProtocols.getPreferredSize().height+5,
                             SpringLayout.NORTH, cbProtocols);
        layout.putConstraint(SpringLayout.WEST, cbColorSets,
                             txtHorOffset,
                             SpringLayout.WEST, lblColorSets);
        
        
        
        /**
         * Scriptarea
         */
        layout.putConstraint(SpringLayout.NORTH, scrlTxt,
                             cbColorSets.getPreferredSize().height+10,
                             SpringLayout.NORTH, cbColorSets);
        layout.putConstraint(SpringLayout.WEST, scrlTxt,
                             5,
                             SpringLayout.WEST, this.getContentPane());
        
        
        
        
        /**
         * Buttons
         */
        layout.putConstraint(SpringLayout.SOUTH, btnConfirm,
                             -5,
                             SpringLayout.SOUTH, this.getContentPane());
        layout.putConstraint(SpringLayout.EAST, btnConfirm,
                             -5,
                             SpringLayout.EAST, this.getContentPane());
        
        layout.putConstraint(SpringLayout.SOUTH, btnCancel,
                             -5,
                             SpringLayout.SOUTH, this.getContentPane());
        layout.putConstraint(SpringLayout.EAST, btnCancel,
                             -(btnConfirm.getPreferredSize().width+10),
                             SpringLayout.EAST, this.getContentPane());
    }
    
    private void initListener()
    {
        btnConfirm.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                save();
            }
        });
        
        btnCancel.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                game = null;
                close();
            }
        });
        
        btnBrowse.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fc = new JFileChooser();
                
                fc.setSelectedFile(new File(game.executable));
                fc.setMultiSelectionEnabled(false);
                
                int returnVal = fc.showOpenDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION)
                    txtExecutable.setText(fc.getSelectedFile().getAbsolutePath());
                
            }
        });
    }
    
    /**
     * Save logic and checks go here.
     */
    private void save()
    {
        if(!editMode)
        {
            if(txtName.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(null,
                        "You have to enter a name for the game.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            else if(txtName.getText().contains("<") || txtName.getText().contains(">")
                 || txtName.getText().contains("?") || txtName.getText().contains("\"")
                 || txtName.getText().contains(":") || txtName.getText().contains("|")
                 || txtName.getText().contains("\\") || txtName.getText().contains("/")
                 || txtName.getText().contains("*"))
            {
                JOptionPane.showMessageDialog(null,
                        "Name contains illegal characters. Don't use these < > ? \" : | \\ / *",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            else
                game.name = txtName.getText();
        }
        game.executable = txtExecutable.getText();

        String portText = txtStandardPort.getText();
        game.script = txtArea.getText();
        game.queryPort = txtQueryPort.getText();
        game.protocol = (String)cbProtocols.getSelectedItem();
        game.colorSet = (String)cbColorSets.getSelectedItem();

        try
        {
            if(portText.isEmpty())
                game.standardPort = -1;
            else
                game.standardPort = Integer.parseInt(portText);
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null,
                                          "The port has to be a numerical value.",
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        game.save();

        if(!editMode)
            Game.loadGame(game.name);

        close();
    }
    
    /**
     * Returns the edited Game object.
     * 
     * @return The Game object
     */
    public Game getGame()
    {
        return game;
    }
    
    private void close()
    {
        this.dispose();
    }

    @Override
    public void resize()
    {
        Dimension textFieldSize = Resizer.calculateResize(this.getContentPane(), lblStandardPort, null, null, null, new Dimension(20, 10), new Dimension(-1, 25));
        
        scrlTxt.setPreferredSize(Resizer.calculateResize(this.getContentPane(), null, null, cbColorSets, btnConfirm, new Dimension(10, 20), null));
        
        txtName.setPreferredSize(textFieldSize);
        txtExecutable.setPreferredSize(Resizer.calculateResize(this.getContentPane(), lblStandardPort, btnBrowse, null, null, new Dimension(20, 10), new Dimension(-1, 25)));
        txtStandardPort.setPreferredSize(textFieldSize);
        txtQueryPort.setPreferredSize(textFieldSize);
        
        cbProtocols.setPreferredSize(new Dimension(100, 25));
        
        lblColorSets.revalidate();
        cbColorSets.revalidate();
        lblProtocols.revalidate();
        cbProtocols.revalidate();
        
        lblName.revalidate();
        lblExecutable.revalidate();
        lblStandardPort.revalidate();
        lblQueryPort.revalidate();
        
        txtName.revalidate();
        txtExecutable.revalidate();
        txtStandardPort.revalidate();
        txtQueryPort.revalidate();

        scrlTxt.revalidate();
        this.revalidate();
    }
}
