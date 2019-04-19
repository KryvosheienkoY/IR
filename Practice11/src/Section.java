import java.util.ArrayList;

public class Section {
	private ArrayList<String> paragraphs;
	private String title;

	public Section(String str) {
		paragraphs = new ArrayList<>();
		analizeSection(str);

	}

	private void analizeSection(String str) {
		String[] lines = str.split("\n");
		boolean strong = false;
		boolean strongRead = false;
		boolean titleBool = false;
		boolean titleRead = false;
		for (String line : lines) {
			
			if (titleRead) {
				titleRead = false;
				continue;
			}
			if (titleBool) {
				titleBool = false;
				titleRead = true;
				this.title=line.substring(line.indexOf("<p>") + 3, line.length() - 4);
				paragraphs.add(this.title);
				continue;
			}
			if (strongRead) {
				strongRead = false;
				continue;
			}
			if (strong) {
				strong = false;
				strongRead = true;
				paragraphs.add(line.substring(line.indexOf("<strong>") + 8, line.length() - 10));
			}
			if (line.contains("<p>")) {

				if (!line.contains("</p>")) {
					strong = true;
					continue;
				}
				String cur = line.substring(line.indexOf("<p>") + 3, line.length() - 4);
				while (cur.contains("<emphasis>")) {

					cur = cur.substring(0, cur.indexOf("<emphasis>"))
							+ cur.substring(cur.indexOf("<emphasis>") + 11, cur.indexOf("</emphasis>"))
							+ cur.substring(cur.indexOf("</emphasis>") + 11);

				}
				while (cur.contains("<strong>")) {

					cur = cur.substring(0, cur.indexOf("<strong>"))
							+ cur.substring(cur.indexOf("<strong>") + 9, cur.indexOf("</strong>"))
							+ cur.substring(cur.indexOf("</strong>") + 9);

				}
				paragraphs.add(cur);
			}
			if (line.contains("<title>")) {
				titleBool = true;
				continue;
			}
		}
	}

	public ArrayList<String> getParagraphs() {
		return paragraphs;
	}

	public String getTitle() {
		return title;
	}

	
}
