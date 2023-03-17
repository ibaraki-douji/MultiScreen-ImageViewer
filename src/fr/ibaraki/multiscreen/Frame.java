package fr.ibaraki.multiscreen;

import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRootPane;

import fr.ibaraki.libs.GifDecoder;
import fr.ibaraki.libs.GifEncoder;
import fr.ibaraki.libs.Image;

public class Frame extends JFrame {

	private static final long serialVersionUID = 5127442640290926224L;
	private JFrame frame;
	private Rectangle screen;
	private JLabel label;
	private BufferedImage image;

	public Frame(String title) throws HeadlessException {
		super(title);
		frame = this;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
	    screen = defaultScreen.getDefaultConfiguration().getBounds();
	    
	    frame.setBackground(Color.BLACK);
	    frame.setSize(200, 200);
	    frame.setVisible(true);
	    
	    frame.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {}
			
			@Override
			public void componentResized(ComponentEvent e) {
				resize();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				resize();
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
	}
	
	private void resize() {
		try {
			label.setIcon(new ImageIcon(crop(frame.getLocation().x, frame.getLocation().y + 10, (frame.getSize().width <= screen.width-frame.getLocation().x ? frame.getSize().width : screen.width-frame.getLocation().x), (frame.getSize().height <= screen.height-frame.getLocation().y - 10 ? frame.getSize().height : screen.height-frame.getLocation().y))));
		} catch (IOException e1) {}
	}
	
	public void removeAll() {
		for (Component c : frame.getComponents()) {
			if (c instanceof JRootPane) {} else {
				frame.remove(c);
			}
		}
	}
	
	public JLabel getImageComponant() {
		for (Component c : frame.getComponents()) {
			if (c instanceof JLabel) {
				return (JLabel) c;
			}
		}
		return null;
	}
	
	public void setupGIF(File GIF) throws IOException {
		removeAll();

		GifDecoder d = new GifDecoder();
		d.read(GIF.getPath());
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean main = false;
				boolean first = true;
				while (true) {
					if (Main.gifCount == -1 || main == true) {
						main = true;
						for (int i = 0; i < d.getFrameCount(); i++) {
							Main.gifCount = i;
							image = d.getFrame(Main.gifCount);
							if (first) {
								Icon icon = new ImageIcon(image);
							    label = new JLabel(icon);
							    frame.add(label);
							    first = false;
							}
							try {
								resize();
							} catch (NullPointerException | RasterFormatException e) {}
							try {
								Thread.sleep(d.getDelay(i));
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} else {
						image = d.getFrame(Main.gifCount);
						if (first) {
							Icon icon = new ImageIcon(image);
						    label = new JLabel(icon);
						    frame.add(label);
						    first = false;
						}
						try {
							resize();
						} catch (NullPointerException | RasterFormatException e) {}
					}
				}
			}
		}).start();
		
	}
	
	public void setupImage(File image) throws IOException {
		removeAll();
		
		Image img = new Image(image);
		img.resize(screen.width, screen.height);
		this.image = img.getImage();
		Icon icon = new ImageIcon(img.getImage());
	    label = new JLabel(icon);
	    frame.add(label);
	}
	
	public BufferedImage crop(int x, int y, int w, int h) throws IOException {
        BufferedImage croppedImage = image.getSubimage(
                        x, 
                        y,
                        w, // widht
                        h // height
        );
        return croppedImage;
    }

}
