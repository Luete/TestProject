package de.luete.gamebrowser.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import datatypes.Server;
import de.luete.gamebrowser.main.CheckThread;
import gui.Window;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class MainView
{
	@FXML
	private ListView<String> serverList;

	public void initialize()
	{
		updateServerStatus();
		initData();
	}
	
	private void initData()
	{
		// Load data into a sorted map!
		for (int i = 0; i < Server.getServerCount(); i++)
		{
			Server sv = Server.get(i);

			serverList.getItems().add(sv.game + " - " + sv.altName);
		}

	}

	private void updateServerStatus()
	{
		int serverCount = Server.getServerCount();

		CheckThread updaterThread[] = new CheckThread[serverCount];

		for (int i = 0; i < serverCount; i++)
		{
			updaterThread[i] = new CheckThread(i);
		}

		for (int i = 0; i < serverCount; i++)
		{
			try
			{
				updaterThread[i].join();
			} catch (InterruptedException ex)
			{
				Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
