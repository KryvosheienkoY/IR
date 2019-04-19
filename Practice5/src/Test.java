import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Kryvosheienko Yulia
 *
 */

public class Test {

	public static void main(String args[]) {

		System.out.println("Enter name of folder: ");
		Scanner sc = new Scanner(System.in);

		// Read all files from a directory to fill a dictionary
		// C:\Users\Julia\Desktop\infosearch
		File folder = new File("C:\\Users\\Julia\\Desktop\\infosearch\\FB2");
		File[] listOfFiles = folder.listFiles();
		ArrayList<File> files = new ArrayList<>();

		for (File file : listOfFiles) {
			if (file.isFile()) {
				String extension = "";
				String fileName = file.getName();
				int i = fileName.lastIndexOf('.');
				if (i >= 0) {
					extension = fileName.substring(i + 1);
				}
				if (extension.equals("fb2"))
					files.add(file);
			}
		}
		Dictionary myDictionary = new Dictionary(files);

		// Form text files to write a vocabulary
		BufferedWriter myWriter1 = null;

		try {
			myWriter1 = new BufferedWriter(new FileWriter(new File("Results.txt")));
			myWriter1.write("The number of words in the collection: " + myDictionary.getCollectionWordSize() + "\r\n");
			myWriter1.write("The size of the collection: " + myDictionary.getCollectionSize() + " kilobytes " + "\r\n");
			myWriter1.write("Time for creating blocks: " + myDictionary.getEstimatedTime()/1000.0 + "seconds \r\n");
			myWriter1.write("Time for creating dict: " + myDictionary.getEstimatedTime()/1000.0 + " seconds \r\n");
			myWriter1.write("Time - end of work: " + myDictionary.getEstimatedTime()/1000.0 + " seconds \r\n");
			myWriter1.flush();
			myWriter1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Vocabularies were created!");

	}

}
