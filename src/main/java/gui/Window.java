package gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import datatypes.Server;
import de.luete.gamebrowser.main.ApplicationSettings;
import de.luete.gamebrowser.main.CheckThread;
import gui.servergametree.ServerTree;
import gui.servergametree.ServerTreeNode;
import windowmanager.Dock;
import windowmanager.Resizable;
import windowmanager.Resizer;

/**
 * The main window...
 * This is class uses the Singleton pattern, this means there can only exist one object.
 */
public class Window extends javax.swing.JFrame implements Resizable
{
    private Container contentPane;
    private JScrollPane scrlServers;
    private ServerTree lstServer;
    private JButton btnUpdate, btnAdd, btnDel, btnEdit, btnOptions, btnPlayers;
    private SpringLayout layout;
    private Dock    docker;
    private boolean isActive = true;
    private final String TITLE = "GameBrowser";

    private Window()
    {
        super.setTitle(TITLE);

        getContentPane().setBackground(Color.decode(ApplicationSettings.colors.getBackground()));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(250, 300));
        this.pack();

        if (ApplicationSettings.frameBounds == null)
        { 
            this.setSize(new Dimension(250, 500));

            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        }
        else
        {
            this.setSize(new Dimension(ApplicationSettings.frameBounds.width, ApplicationSettings.frameBounds.height));
            this.setLocation(ApplicationSettings.frameBounds.x, ApplicationSettings.frameBounds.y);
        }

        initComponents();
        initListener();
        update();
        
        Resizer.addToResizer(this);
        this.setIconImage(new ImageIcon("./Icons/icon.png").getImage());
        this.setVisible(true);
    }

    private void updateServerStatus()
    {
        int svCount = Server.getServerCount();
        
        CheckThread t[] = new CheckThread[svCount];

        for (int i = 0; i < svCount; i++)
        {
            t[i] = new CheckThread(i);
        }

        for (int i = 0; i < svCount; i++)
        {
            try
            {
                t[i].join();
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void initComponents()
    {
        contentPane = this.getContentPane();
        
        btnAdd = new JButton("Add");
        btnDel = new JButton("Del");
        btnEdit = new JButton("Edit");
        btnUpdate = new JButton("Update");
        btnOptions = new JButton("Options");
        btnPlayers = new JButton("Players");
        
        btnAdd.setOpaque(false);
        btnDel.setOpaque(false);
        btnEdit.setOpaque(false);
        btnUpdate.setOpaque(false);
        btnOptions.setOpaque(false);
        btnPlayers.setOpaque(false);
        
        
        lstServer = new ServerTree(new ServerTreeNode("", 0));
        //lstServer.setPreferredSize(new Dimension(contentPane.getSize().width, contentPane.getSize().height - 25));
        

        scrlServers = new JScrollPane(lstServer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrlServers.setPreferredSize(new Dimension(contentPane.getSize().width, contentPane.getSize().height - btnAdd.getPreferredSize().height * 2));
        
        initLayout();

        add(btnUpdate);
        add(btnAdd);
        add(btnDel);
        add(btnEdit);
        add(btnOptions);
        add(btnPlayers);
        add(scrlServers);
        
        docker = new Dock();
        docker.setMain(this);
        docker.setSub(PlayerDialog.getInstance());
    }

    private void update()
    {
        this.setTitle("Updating List");
        
        updateServerStatus();

        lstServer.initData();
        lstServer.repaint();
        
        this.setTitle(TITLE);
    }

    private void initLayout()
    {
        layout = new SpringLayout();
        this.setLayout(layout);

        //Serverlist
        layout.putConstraint(SpringLayout.NORTH, scrlServers,
                             5,
                             SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.WEST, scrlServers,
                             5,
                             SpringLayout.WEST, contentPane);
        
        //First button row
        layout.putConstraint(SpringLayout.SOUTH, btnAdd,
                             -btnAdd.getPreferredSize().height,
                             SpringLayout.SOUTH, btnUpdate);
        layout.putConstraint(SpringLayout.WEST, btnAdd,
                             5,
                             SpringLayout.WEST, contentPane);

        layout.putConstraint(SpringLayout.SOUTH, btnEdit,
                             -btnAdd.getPreferredSize().height,
                             SpringLayout.SOUTH, btnUpdate);
        layout.putConstraint(SpringLayout.WEST, btnEdit,
                             btnAdd.getPreferredSize().width,
                             SpringLayout.WEST, btnUpdate);

        layout.putConstraint(SpringLayout.SOUTH, btnDel,
                             -btnAdd.getPreferredSize().height,
                             SpringLayout.SOUTH, btnUpdate);
        layout.putConstraint(SpringLayout.WEST, btnDel,
                             btnEdit.getPreferredSize().width,
                             SpringLayout.WEST, btnEdit);
        
        //Second button row
        layout.putConstraint(SpringLayout.SOUTH, btnUpdate,
                             -5,
                             SpringLayout.SOUTH, this.getContentPane());
        layout.putConstraint(SpringLayout.WEST, btnUpdate,
                             5,
                             SpringLayout.WEST, contentPane);

        layout.putConstraint(SpringLayout.SOUTH, btnPlayers,
                             -5,
                             SpringLayout.SOUTH, this.getContentPane());
        layout.putConstraint(SpringLayout.WEST, btnPlayers,
                             btnUpdate.getPreferredSize().width,
                             SpringLayout.WEST, btnUpdate);

        layout.putConstraint(SpringLayout.SOUTH, btnOptions,
                             -5,
                             SpringLayout.SOUTH, this.getContentPane());
        layout.putConstraint(SpringLayout.WEST, btnOptions,
                             btnPlayers.getPreferredSize().width,
                             SpringLayout.WEST, btnPlayers);
    }

    private void initListener()
    {
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                ApplicationSettings.save();
            }
            @Override
            public void windowIconified(WindowEvent e)
            { //If the main window gets minimized, we have to "minimize" the playerlist promatically
                PlayerDialog.getInstance().setVisible(false);
            }
            @Override
            public void windowDeiconified(WindowEvent e)
            {
                if(ApplicationSettings.playerListVisible) PlayerDialog.getInstance().setVisible(true);
            }
            @Override
            public void windowActivated(WindowEvent e)
            {
                if(isActive && PlayerDialog.getInstance().isVisible())
                {
                    PlayerDialog.getInstance().toFront();
                    isActive = false;
                    toFront();
                }
                else
                    isActive = true;
            }
        });
        lstServer.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    showMenu(evt);
                }
            }
        });
        btnUpdate.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                update();
            }
        });
        btnAdd.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                AddServer.getInstance().addServer();
                update();
            }
        });
        btnDel.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int n = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to delete the Server?",
                        "Server deletion",
                        JOptionPane.YES_NO_OPTION);
                if (n == 0)
                {
                    Server.get(lstServer.getSelectedServerIndex()).delete();
                    update();
                }
            }
        });
        btnEdit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int i = lstServer.getSelectedServerIndex();
                
                if (i >= 0 && i < Server.getServerCount())
                {
                    AddServer.getInstance().editServer(Server.get(i));
                    update();
                }
            }
        });
        btnOptions.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                OptionsGameDialog.getInstance().display();
                
                update();
            }
        });
        btnPlayers.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PlayerDialog plrList = PlayerDialog.getInstance();
                
                if (!plrList.isVisible())
                {
                    plrList.setVisible(true);
                    ApplicationSettings.playerListVisible = true;
                }
                else
                {
                    plrList.setVisible(false);
                    ApplicationSettings.playerListVisible = false;
                }
            }
        });
    }

    @Override
    public void resize()
    {
        scrlServers.setPreferredSize(new Dimension(Resizer.calculateResize(this.getContentPane(), null, null, null, btnAdd, new Dimension(10, 5), null)));
        //lstServer.setPreferredSize(new Dimension(Resizer.calculateResize(this.getContentPane(), null, null, null, btnAdd, new Dimension(10, 10), null)));

        int btnWidth = (contentPane.getSize().width - 10) / 3;
        int stillNeeded = (contentPane.getSize().width - 10) % 3;

        btnAdd.setPreferredSize(new Dimension(btnWidth, btnAdd.getPreferredSize().height));
        btnEdit.setPreferredSize(new Dimension(btnWidth, btnAdd.getPreferredSize().height));
        btnDel.setPreferredSize(new Dimension(btnWidth + stillNeeded, btnAdd.getPreferredSize().height));
        btnUpdate.setPreferredSize(new Dimension(btnWidth, btnAdd.getPreferredSize().height));
        btnPlayers.setPreferredSize(new Dimension(btnWidth, btnAdd.getPreferredSize().height));
        btnOptions.setPreferredSize(new Dimension(btnWidth + stillNeeded, btnAdd.getPreferredSize().height));

        initLayout();

        scrlServers.revalidate();
        lstServer.revalidate();
        this.revalidate();
    }
    
    public void showMenu(MouseEvent evt)
    {
       JPopupMenu menu = new JPopupMenu();
       JMenuItem searchItem = new JMenuItem("Search player");
       
       searchItem.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
                new PlayerSearch();
           }
       });
       
       menu.add(searchItem);
       menu.show(lstServer, evt.getX(), evt.getY()); 
    }
    
    
    private static class SingletonHolder
    { 
        private static final Window INSTANCE = new Window();
    }

    public static Window getInstance()
    {
        return SingletonHolder.INSTANCE;
    }
}