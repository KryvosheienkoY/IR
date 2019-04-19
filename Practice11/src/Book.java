import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Book {
	private ArrayList<Section> sections;
	private Description description;

	public Book(File file) {
		sections = new ArrayList<>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			StringBuilder descriptionBuilder = new StringBuilder();
			String line="";

			// finding start of description
			while (!line.contains("<description>"))
				line = br.readLine();

			descriptionBuilder.append(line).append("\n");
			
			while (!line.contains("</description>")) {
				line = br.readLine();
				descriptionBuilder.append(line).append("\n");
			}
		
			description = new Description(descriptionBuilder.toString());

			// reading "text" of book
			StringBuilder sectionBuilder = new StringBuilder();
			// finding start of sections
			while (!line.contains("<section>"))
				line = br.readLine();
			
			sectionBuilder.append(line).append("\n");
			while (!line.contains("</body>")) {
				line = br.readLine();
				if (!line.contains("</section")) {
					sectionBuilder.append(line).append("\n");
				} else {
					// next section
					sectionBuilder.append(line).append("\n");
					sections.add(new Section(sectionBuilder.toString()));
					sectionBuilder = new StringBuilder();
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("The file wasn`t found.");
		} catch (IOException e) {
			e.getMessage();
			e.printStackTrace();
		}
	}

	public Description getDescription() {
		return description;
	}

	public ArrayList<Section> getSections() {
		return sections;
	}
}
