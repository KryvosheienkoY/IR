import java.util.ArrayList;

public class Description {
	private String title;
	private ArrayList<Author> authorsList;
	private String annotation;

	public Description(String description) {
		authorsList = new ArrayList<>();
		title = "";
		annotation = "";
		String[] info = description.split("\n");

		boolean titleF = false;
		boolean authorF = false;
		boolean annotationF = false;

		String nickname = "nickname";
		String first_name = "first-name";
		String middle_name = "middle-name";
		String last_name = "last-name";
		String book_title = "<book-title>";
		String p = "<p>";

		for (int i = 0; i < info.length; i++) {
			// author
			if (info[i].contains("<author>")) {
				Author author = new Author();
				if (info[i].contains(nickname)) {
					author.setNickname(info[i].substring(info[i].indexOf(nickname) + nickname.length() + 1,
							info[i].indexOf("</nickname>")));
					authorF = true;
				}

				if (info[i].contains(first_name)) {
					author.setFirstName(info[i].substring(info[i].indexOf(first_name) + first_name.length() + 1,
							info[i].indexOf("</first-name>")));
					authorF = true;
				}
				if (info[i].contains(middle_name)) {
					author.setMiddleName(info[i].substring(info[i].indexOf(middle_name) + middle_name.length() + 1,
							info[i].indexOf("</middle-name>")));

				}
				if (info[i].contains(last_name)) {
					author.setLastName(info[i].substring(info[i].indexOf(last_name) + last_name.length() + 1,
							info[i].indexOf("</last-name>")));
				}
				this.authorsList.add(author);
			}
			// title
			if (info[i].contains("<book-title>")) {
				title = info[i].substring(info[i].indexOf(book_title) + book_title.length(),
						info[i].indexOf("</book-title>"));
				titleF = true;
			}
			// annotation
			if (info[i].contains("<annotation>")) {
				int end;
				if (info[i].contains("</annotation>"))
					end = info[i].indexOf("</annotation>") - p.length();
				else
					end = info[i].length() - p.length() - 1;
				annotation = info[i].substring(info[i].indexOf("<annotation>") + p.length() + 1, end);
				annotationF = true;
			}
			if (titleF && authorF && annotationF || info[i].contains("document-info"))
				break;
		}
	}

	public String getTitle() {
		return title;
	}

	public ArrayList<Author> getAuthors() {
		return authorsList;
	}

	public String getAnnotation() {
		return annotation;
	}
}
