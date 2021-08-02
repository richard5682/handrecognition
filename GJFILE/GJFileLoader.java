package GJFILE;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import GJSWING.GJWindow;

public class GJFileLoader {
	public static BufferedImage LoadImage(String link){
		File file = new File(link);
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static void SaveImage(BufferedImage bi,String directory){
		File outputfile = new File(directory);
		try {
			ImageIO.write(bi, "jpg", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void SaveText(String text,String directory) throws IOException{
		File file = new File(directory);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(text);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static String OpenSaveChooser(String initial_directory,GJWindow window){
		JFileChooser filechooser = new JFileChooser(initial_directory);
		if(filechooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION){
			return filechooser.getSelectedFile().getAbsolutePath();
		}else{
			return null;
		}
	}
	public static String OpenLoadChooser(String initial_directory,GJWindow window){
		JFileChooser filechooser = new JFileChooser(initial_directory);
		if(filechooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION){
			return filechooser.getSelectedFile().getAbsolutePath();
		}else{
			return null;
		}
	}
	public static String OpenDirChooser(String initial_directory,GJWindow window){
		JFileChooser filechooser = new JFileChooser(initial_directory);
		filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(filechooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION){
			return filechooser.getSelectedFile().getAbsolutePath();
		}else{
			return null;
		}
	}
	public static String LoadText(String directory){
		File file = new File(directory);
		String line = null;
		if(file.exists()){
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				try {
					line = br.readLine();
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return line;
	}
}
