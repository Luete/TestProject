package datatypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;
import java.util.Random;

import javax.swing.JOptionPane;

/**
 * Holds server information that needs to be saved (ip/port) and its status.
 * 
 * It uses the Java serialization API for loading and saving.
 */
public class Server
{
	/** Carries all the loaded servers. */
	private static ArrayList<Server> svArray = new ArrayList();
	public String altName, IP, script, game;
	public int PORT;
	public int id = -1;
	/** Holds the server status. */
	public ServerStatus status = new ServerStatus();

	public Server()
	{
	}

	/**
	 * Creates server Object and loads from file.
	 * 
	 * @param file
	 *            The path to the .server file.
	 */
	public Server(String file)
	{
		load(file);
	}

	/**
	 * Saves the server information in a file.
	 */
	public void save()
	{
		if (id == -1)
			id = generateID();

		try (FileOutputStream stream = new FileOutputStream("./Servers/" + id + ".server"))
		{
			Properties buf = new Properties();

			buf.setProperty("name", altName);
			buf.setProperty("ip", IP);
			buf.setProperty("port", String.valueOf(PORT));
			buf.setProperty("game", game);
			buf.setProperty("script", script);

			buf.store(stream, null);

			stream.close();
		} catch (IOException ex)
		{
			System.out.println("Couldn't save server file. (" + altName + "[" + id + "]" + ")\n" + ex);
		}
	}

	private void load(String file)
	{
		try (FileInputStream stream = new FileInputStream("./Servers/" + file + ".server"))
		{
			Properties buf = new Properties();
			buf.load(stream);

			altName = buf.getProperty("name");
			IP = buf.getProperty("ip");
			script = buf.getProperty("script");
			game = buf.getProperty("game");
			PORT = Integer.parseInt(buf.getProperty("port", "-1"));
			id = Integer.parseInt(file);

			Game.loadGame(game);

			stream.close();
		} catch (IOException ex)
		{
			System.out.println("Couldn't read server file. (" + altName + "[" + id + "]" + ")\n" + ex);
		}
	}

	/**
	 * Adds a Server to the svArray list.
	 * 
	 * @param sv
	 *            The Server-object.
	 */
	public static void add(Server sv)
	{
		svArray.add(sv);
	}

	/**
	 * Reloads the Server information if id != -1.
	 * 
	 * @param updateList
	 *            If true and the server is not in the list it will be added.
	 */
	public void reload(boolean updateList)
	{
		if (id == -1)
			return;

		if (updateList && !svArray.contains(this))
		{
			int index = Server.getSvIndexByID(id);
			if (index >= 0)
				svArray.remove(id);

			System.out.println("Index: " + index + "   ID: " + id);

			load(String.valueOf(id));
			svArray.add(this);
			return;
		}

		load(String.valueOf(id));
	}

	private int generateID()
	{
		Random randomGenerator = new Random();

		int nID;

		while (true)
		{
			nID = randomGenerator.nextInt(10000);

			File check = new File("./Servers/" + nID + ".server");
			if (!check.exists())
				break;
		}

		return nID;
	}

	/**
	 * Removes the Server from the list and the harddisk.
	 */
	public void delete()
	{
		File f = new File("./Servers/" + id + ".server");

		if (!f.delete())
		{
			JOptionPane.showMessageDialog(null, "Error deleting server " + altName + " with ID " + id, "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		svArray.remove(this);
	}

	/**
	 * Returns a server from the list at the specified index.
	 * 
	 * @param index
	 *            The index the server has in the server list.
	 * @return The server at the specified index.
	 */
	public static Server get(int index)
	{
		if (index < 0 || index >= svArray.size())
			return null;

		return svArray.get(index);
	}

	/**
	 * Returns the amount of servers in the list (equals svArray.size()).
	 */
	public static int getServerCount()
	{
		return svArray.size();
	}

	/**
	 * Returns the server array that contains all Server objects.
	 */
	public static ArrayList<Server> getServerArray()
	{
		return svArray;
	}

	/**
	 * Searches for the Server index in the list and returns it. It simply goes
	 * through the array and compares the Server.id with id.
	 * 
	 * @param id
	 *            Server ID
	 * @return Returns the server index in svArray
	 */
	public static int getSvIndexByID(int id)
	{
		for (int i = 0; i < svArray.size(); i++)
		{
			if (Server.get(i).id == id)
				return i;
		}

		return -1;
	}

	/**
	 * Loads all Servers. Clears the svArray and loads then all servers from
	 * ./Servers to re-add them to the list.
	 *
	 * @return List with server names.
	 */
	public static ArrayList<String> loadFavs()
	{
		// Contains the server names so they can be easily
		// set in the JList
		ArrayList<String> listBuf = new ArrayList();

		File dir = new File("./Servers/");

		String[] fileList = dir.list(new FilenameFilter()
		{
			@Override
			public boolean accept(File d, String name)
			{
				return name.endsWith(".server");
			}
		});

		svArray.clear();

		if (fileList != null)
		{
			for (int i = 0; i < fileList.length; i++)
			{
				Server buf = new Server(fileList[i].replace(".server", ""));

				svArray.add(buf);
				listBuf.add(buf.altName);
			}
		}

		Collections.sort(listBuf, String.CASE_INSENSITIVE_ORDER);
		Collections.sort(svArray, new Comparator<Server>()
		{
			@Override
			public int compare(Server s1, Server s2)
			{
				return s1.altName.compareToIgnoreCase(s2.altName);
			}
		});

		return listBuf;
	}
}
