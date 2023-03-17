package fr.ibaraki.libs;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fr.ibaraki.multiscreen.Frame;
import fr.ibaraki.multiscreen.Main;

public class Tray {
	
	private SystemTray sysTray;
	private TrayIcon tray;
	private JPopupMenu menu;
	private Runtime runtime;

	
	/**
	 * 
	 * <h2>for the icon</h2>
	 * you can use :<br>
	 * {@code Image image = Toolkit.getDefaultToolkit().createImage(new File("icon.png").getAbsolutePath());}<br>
	 * if the icon is not compiled with the jar<br><br>
	 * you can use :<br>
	 * {@code Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));}<br>
	 * if the icon is compiled with the jar<br>
	 * 
	 * @param name : Name of the tray when hover
	 * @param image : Image of the tray
	 * 
	 * @author Yugo
	 * 
	 * 
	 */
	public Tray(String name, Image image) throws AWTException {
		sysTray = SystemTray.getSystemTray();
        //If the icon is a file
        //Image image = Toolkit.getDefaultToolkit().createImage(new File("icon.png").getAbsolutePath());
		
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));
        
        tray = new TrayIcon(image, "Tray");
        
        menu = new JPopupMenu();
        
        tray.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		if (e.getButton() == MouseEvent.BUTTON1) {
        			Frame f = new Frame("MultiScreen " + Main.framei++);
        			try {
						f.setupImage(Main.image);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
        			return;
        		}
        		
        		if (menu.isVisible()) {
              	  	menu.setVisible(false);
                } else {
              	  	menu.show(e.getComponent(), e.getX(), e.getY());
                }/**/
        	}
		});
        
        tray.setImageAutoSize(true);
        tray.setToolTip(name);
        sysTray.add(tray);
	}
	
	/**
	 * 
	 * Add an item in the popup menu
	 * 
	 * @param name : name of the item
	 * @param run : action when click
	 * @author Yugo
	 */
	public void addMenuItem(String name, Run run) {
		JMenuItem i = new JMenuItem(name);
        i.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				run.getRunnable().run();
				menu.setVisible(false);
			}
		});
        menu.add(i);
	}
	
	/**
	 * Init custom push notification icon
	 * @throws IOException
	 */
	public void initCustomIcon(Runtime runtime) throws IOException {
		this.runtime = runtime;
		
		if (isCustomIcon()) return;
		Process p = runtime.exec("PowerShell.exe -NoProfile -Command \"& {Start-Process PowerShell.exe -ArgumentList '-NoProfile -ExecutionPolicy Bypass -Command \"\"& {Install-Module BurntToast -Force}\"\"' -Verb RunAs}\"");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    String line = "";
	    String fLine = "";
	    while ((line = reader.readLine()) != null) {
	        System.out.println(line);
	        if (fLine == "") fLine = line;
	    }
	    
	    if (fLine != "") {
	    	notif("Error", fLine, MessageType.ERROR);
	    }
		
		while (!isCustomIcon()) {}
	}
	
	/**
	 * 
	 * @return true if you can use custom icon for your push notification else it's false
	 * @throws IOException
	 */
	public boolean isCustomIcon() throws IOException {
		Process p = runtime.exec("powershell.exe -Command \"& {Get-InstalledModule}\"");
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    String line = "";
	    while ((line = reader.readLine()) != null) {
	        if (line.contains("BurntToast")) return true;
	    }
		
		return false;
	}
	
	/**
	 * Add a black space in the popup menu
	 * @author Yugo
	 */
	public void addMenuSpace() {
		JMenuItem i = new JMenuItem("");
		i.setSize(1, 1);
		i.setBackground(new Color(0, 0, 0));
		menu.add(i);
	}
	
	/**
	 * 
	 * Show a windows notification
	 * 
	 * @param title : title of the notification (always bold)
	 * @param desc : description of the notification (always normal)
	 * @param type : icon of the notification
	 * @author Yugo
	 */
	public void notif(String title, String desc, MessageType type) {
		tray.displayMessage(title, desc, type);
	}
	
	/**
	 * You must use the initCustomIcon(); to use it
	 * @param title : title of the notification (always bold)
	 * @param desc : description of the notification (always normal)
	 * @param icon : icon of the notification
	 * @throws IOException
	 */
	public void notif(String title, String desc, File icon) throws IOException {
		if (!isCustomIcon()) {
			notif(title, desc, MessageType.NONE);
			return;
		}
		
		title = title.replaceAll("\"", "\\\\\"");
		desc = desc.replaceAll("\"", "\\\\\"");
		title = title.replaceAll("'", "'\\'");
		desc = desc.replaceAll("'", "'\\'");
		runtime.exec("powershell.exe -Command \"& {New-BurntToastNotification -AppLogo '" + icon.getAbsolutePath() + "' -Text '" + title + "' , '" + desc + "'}\"");
	}
	
	/**
	 * Change the name the the tray
	 * @param name : Name of the tray
	 * @author Yugo
	 */
	public void setName(String name) {
		tray.setToolTip(name);
	}
	
	/**
	 * 
	 * @return the name of the tray
	 */
	public String getName() {
		return tray.getToolTip();
	}
	
	/**
	 * remove ALL items and space in the popup menu
	 */
	public void removeAllTrayMenu() {
		menu.removeAll();
	}
	
	/**
	 * 
	 * @return the popup menu
	 */
	public JPopupMenu getMenu() {
		return menu;
	}
	
}
