package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import de.luete.gamebrowser.main.ApplicationSettings;

/**
 * A Panel used to display the options.
 */
public class OptionsPanel extends JPanel
{
    private JTextField      txtQuery;
    private JButton         btnSave;
    private JLabel          lblQuery;
    private SpringLayout    layout;
    private JCheckBox       plrListStartUp,
                            closeAtConnect,
                            plrAmountInList,
                            onlyRealPlayers,
                            displayMaps,
                            playerColor,
                            displayPW;
    
    OptionsPanel()
    {
        initComponents();
        initListener();
    }
    
    /**
     * Fills the UI "form" with the saved settings.
     */
    public void display()
    {
        plrListStartUp.setSelected(ApplicationSettings.playerListStartUp);
        closeAtConnect.setSelected(ApplicationSettings.closeAtConnect);
        plrAmountInList.setSelected(ApplicationSettings.plrAmountInList);
        onlyRealPlayers.setSelected(ApplicationSettings.onlyRealPlayers);
        displayMaps.setSelected(ApplicationSettings.displayMaps);
        playerColor.setSelected(ApplicationSettings.playerColor);
        displayPW.setSelected(ApplicationSettings.displayPW);
        txtQuery.setText(String.valueOf(ApplicationSettings.queryTimeout));
    }
    
    private void initComponents()
    {
        txtQuery = new JTextField();
        txtQuery.setPreferredSize(new Dimension(40, 20));
        
        lblQuery = new JLabel("Query timeout (ms)");
        
        btnSave = new JButton("Save");
        
        plrListStartUp = new JCheckBox("Start with player list open", false);
        closeAtConnect = new JCheckBox("Close JKConnector when the game starts", true);
        plrAmountInList = new JCheckBox("Show amount of connected players before the server name", true);
        onlyRealPlayers = new JCheckBox("Don't count bot's", true);
        displayMaps = new JCheckBox("Show current map and ping below the server name", true);
        playerColor = new JCheckBox("Display a colorful playerlist", true);
        displayPW = new JCheckBox("Tell me when a server needs a password", true);
        
        initLayout();
        
        add(btnSave);
        add(plrListStartUp);
        add(closeAtConnect);
        add(plrAmountInList);
        add(onlyRealPlayers);
        add(displayMaps);
        add(playerColor);
        add(displayPW);
        add(txtQuery);
        add(lblQuery);
    }
    
    private void initLayout()
    {
        layout = new SpringLayout();
        this.setLayout(layout);
        
        int checkBoxHeight = plrAmountInList.getPreferredSize().height;
        
        layout.putConstraint(SpringLayout.NORTH, plrListStartUp,
                             5,
                             SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.WEST, plrListStartUp,
                             5,
                             SpringLayout.WEST, this);
        
        layout.putConstraint(SpringLayout.NORTH, closeAtConnect,
                             checkBoxHeight,
                             SpringLayout.NORTH, plrListStartUp);
        layout.putConstraint(SpringLayout.WEST, closeAtConnect,
                             5,
                             SpringLayout.WEST, this);
        
        layout.putConstraint(SpringLayout.NORTH, plrAmountInList,
                             checkBoxHeight,
                             SpringLayout.NORTH, closeAtConnect);
        layout.putConstraint(SpringLayout.WEST, plrAmountInList,
                             5,
                             SpringLayout.WEST, this);
        
        layout.putConstraint(SpringLayout.NORTH, onlyRealPlayers,
                             checkBoxHeight,
                             SpringLayout.NORTH, plrAmountInList);
        layout.putConstraint(SpringLayout.WEST, onlyRealPlayers,
                             25,
                             SpringLayout.WEST, this);
        
        layout.putConstraint(SpringLayout.NORTH, displayMaps,
                             checkBoxHeight,
                             SpringLayout.NORTH, onlyRealPlayers);
        layout.putConstraint(SpringLayout.WEST, displayMaps,
                             5,
                             SpringLayout.WEST, this);
        
        layout.putConstraint(SpringLayout.NORTH, playerColor,
                             checkBoxHeight,
                             SpringLayout.NORTH, displayMaps);
        layout.putConstraint(SpringLayout.WEST, playerColor,
                             5,
                             SpringLayout.WEST, this);
        
        layout.putConstraint(SpringLayout.NORTH, displayPW,
                             checkBoxHeight,
                             SpringLayout.NORTH, playerColor);
        layout.putConstraint(SpringLayout.WEST, displayPW,
                             5,
                             SpringLayout.WEST, this);
        
        
        layout.putConstraint(SpringLayout.NORTH, lblQuery,
                             checkBoxHeight+5,
                             SpringLayout.NORTH, displayPW);
        layout.putConstraint(SpringLayout.WEST, lblQuery,
                             5,
                             SpringLayout.WEST, this);
        
        layout.putConstraint(SpringLayout.NORTH, txtQuery,
                             checkBoxHeight+5,
                             SpringLayout.NORTH, displayPW);
        layout.putConstraint(SpringLayout.WEST, txtQuery,
                             lblQuery.getPreferredSize().width + 15,
                             SpringLayout.WEST, this);
        
        
        
        layout.putConstraint(SpringLayout.SOUTH, btnSave,
                             -5,
                             SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.EAST, btnSave,
                             -5,
                             SpringLayout.EAST, this);
    }
    private void initListener()
    {
        btnSave.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ApplicationSettings.playerListStartUp = plrListStartUp.isSelected();
                ApplicationSettings.closeAtConnect = closeAtConnect.isSelected();
                ApplicationSettings.plrAmountInList = plrAmountInList.isSelected();
                ApplicationSettings.onlyRealPlayers = onlyRealPlayers.isSelected();
                ApplicationSettings.displayMaps = displayMaps.isSelected();
                ApplicationSettings.playerColor = playerColor.isSelected();
                ApplicationSettings.displayPW = displayPW.isSelected();
                
                try
                {
                    int timeout = Integer.parseInt(txtQuery.getText());
                    if(timeout > 0)
                        ApplicationSettings.queryTimeout = timeout;
                    else
                        txtQuery.setText(String.valueOf(ApplicationSettings.queryTimeout));
                }
                catch(NumberFormatException ex)
                {
                    txtQuery.setText(String.valueOf(ApplicationSettings.queryTimeout));
                }
                
                ApplicationSettings.save();
            }
        });
        plrAmountInList.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JCheckBox src = (JCheckBox)e.getSource();
                
                if(src.isSelected())
                    onlyRealPlayers.setEnabled(true);
                else
                    onlyRealPlayers.setEnabled(false);
            }
        });
    }
}