package de.luete.gamebrowser.main;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import datatypes.Game;
import datatypes.Server;
import datatypes.ServerStatus;
import main.Controller;
import protocols.BaseProtocol;

/**
 * Is an extra thread class to get Server informations for later use. The job of
 * this class is to start an extra thread and query a server. It will call the
 * plugins prepareServerStatus() method to gather information. The information
 * will be saved in Server.status
 */
public class CheckThread implements Runnable
{

    /**
     * The servers index in svArray
     */
    private int index = 0;
    /**
     * Server buffer.
     */
    private Thread thread;

    /**
     * Puts the server index in a buffer and starts a new Thread. A new Thread
     * is started to call the plugin functions and prepare the ServerStatus.
     *
     * @param svIndex The index the server has in svArray
     */
    public CheckThread(int svIndex) {
        index = svIndex;

        thread = new Thread(this, "Connection Check Thread");
        thread.start();
    }

    /**
     * Simply calls the thread.join() method of 'thread'.
     *
     * @throws InterruptedException
     */
    public void join() throws InterruptedException {
        thread.join();
    }

    /**
     * Calls the protocols prepareServerStatus() method and saves the return
     * value.
     */
    @Override
    public void run() {
        List<Server> serverList = Server.getServerArray();
        ServerStatus serverStatus = new ServerStatus();
        
        Server server = Server.get(index);
        Game game = Game.get(server.game);

        if (game == null || !Controller.protocols.containsKey(game.protocol)) {
            return;
        }


        try
        {
            BaseProtocol protocol = (BaseProtocol)Controller.protocols.get(game.protocol).newInstance();
                    
            int port = -1;
            if (game.queryPort.isEmpty()) {
                port = server.PORT;
            } else if (game.queryPort.startsWith("+")) {
                port = server.PORT + Integer.parseInt(game.queryPort.substring(1, game.queryPort.length()));
            } else if (game.queryPort.startsWith("-")) {
                port = server.PORT - Integer.parseInt(game.queryPort.substring(1, game.queryPort.length()));
            } else {
                port = Integer.parseInt(game.queryPort);
            }

            serverStatus = protocol.prepareServerStatus(server.IP, port);
        }
        catch (Exception ex)
        {
            Logger.getLogger(CheckThread.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Couldn't check Serverstatus.\n" + ex.toString());
        }
        finally
        {
            serverList.get(index).status = serverStatus;
        }
    }
}