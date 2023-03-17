package fr.ibaraki.libs;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

public class Image {

	private BufferedImage img;
	
	public Image() {
		img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	}
	
	public Image(File file) throws IOException {
		img = ImageIO.read(file);
	}
	
	/**
	 * 
	 * @param url
	 * @param error : FALSE (true if getting a 403 error beacause client settings like discord)
	 * @throws IOException
	 */
	public Image(URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36 Edg/88.0.705.50");
		conn.connect();
		img = ImageIO.read(conn.getInputStream());
	}
	
	public Image(BufferedImage img) {
		this.img = img;
	}
	
	public BufferedImage getImage() {
		return img;
	}
	
	public void createImage(int width, int height) {
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}
	
	public void createImage(Point point) {
		img = new BufferedImage(point.x, point.y, BufferedImage.TYPE_INT_ARGB);
	}
	
	public static Point getTextSize(String text, Font font) {
		Graphics2D g2d = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();
        g2d.dispose();
        return new Point(width, height);
	}
	
	public void addImage(BufferedImage b, float opacity, int x, int y) {
	     Graphics2D g2d = img.createGraphics();
	     g2d.drawImage(b, x, y, null);
	     g2d.dispose();
	}
	
	public void addText(String text, Font font, Color color, int x, int y) {
	     Graphics2D g2d = img.createGraphics();
	     g2d.setFont(font);
	     FontMetrics fm = g2d.getFontMetrics();
	     g2d.setColor(color);
	     g2d.drawString(text, x, fm.getAscent()+y);
	     g2d.dispose();
	}
	
	public void addImage(Image image, float opacity, int x, int y) {
	     addImage(image.getImage(), opacity, x, y);
	}
	
	public void addImage(BufferedImage b, float opacity, Point p) {
	     Graphics2D g2d = img.createGraphics();
	     g2d.drawImage(b, p.x, p.y, null);
	     g2d.dispose();
	}
	
	public void addText(String text, Font font, Color color, Point p) {
	     Graphics2D g2d = img.createGraphics();
	     g2d.setFont(font);
	     FontMetrics fm = g2d.getFontMetrics();
	     g2d.setColor(color);
	     g2d.drawString(text, p.x, fm.getAscent()+p.y);
	     g2d.dispose();
	}
	
	public void addImage(Image image, float opacity, Point p) {
	     addImage(image.getImage(), opacity, p.x, p.y);
	}
	
	public File saveImage(String path) throws IOException {
		return saveImage(new File(path));
	}
	
	public File saveImage(File file) throws IOException {
		ImageIO.write(img, "png", file);
		return file;
	}
	
	public void resize(int width, int heigth) {
		BufferedImage resizedImage = new BufferedImage(width, heigth, BufferedImage.TYPE_INT_RGB);
	    Graphics2D graphics2D = resizedImage.createGraphics();
	    graphics2D.drawImage(img, 0, 0, width, heigth, null);
	    graphics2D.dispose();
	    img = resizedImage;
	}
}
