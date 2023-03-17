package fr.ibaraki.multiscreen;

import java.awt.Toolkit;
import java.io.File;

import fr.ibaraki.libs.Run;
import fr.ibaraki.libs.Tray;

public class Main {
	public static int framei = 0;
	public static int gifCount = -1;
	public static File image = new File("C:\\Users\\Ibaraki\\Desktop\\images", "70424422_upscaled.gif");
	
	public static void main(String[] args) throws Exception {
		Tray t = new Tray("MultiScreen", Toolkit.getDefaultToolkit().createImage("C:\\Users\\Ibaraki\\Desktop\\images\\icon.png"));
		t.addMenuItem("Exit", new Run() {
			@Override
			public void run() {
				System.exit(0);
			}
		});
	}
	
}
