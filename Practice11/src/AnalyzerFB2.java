import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;


public class AnalyzerFB2 {
	static HashMap<String, TreeSet<Integer>> authors;
	static HashMap<String, TreeSet<Integer>> titles;
	static HashMap<String, TreeSet<Integer>> body;
	static String fileFolder = "";
	static ArrayList<String> filelist;

	public AnalyzerFB2(String folder) {
		authors = new HashMap<String, TreeSet<Integer>>();
		titles = new HashMap<String, TreeSet<Integer>>();
		body = new HashMap<String, TreeSet<Integer>>();
		filelist = new ArrayList<>();
		fileFolder = folder;
	}

	void readEntry() {
		File folder = new File(fileFolder);
		for (int i = 0; i < folder.listFiles().length; i++) {
			try {
				String path = folder.listFiles()[i].getPath();
				filelist.add(path);
				System.out.println(i + ") Path " + path);
				Book fb = new Book(new File(path));
				if (fb == null)
					break;

				// authors
				ArrayList<Author> authorList = fb.getDescription().getAuthors();
				for (int j = 0; j < authorList.size(); j++) {
					if (authorList.get(j).getFirstName() != null && authorList.get(j).getFirstName().length() > 0) {
						addWord(authors, correctWord(authorList.get(j).getFirstName()), i);
					}
					if (authorList.get(j).getMiddleName() != null && authorList.get(j).getMiddleName().length() > 0) {
						addWord(authors, correctWord(authorList.get(j).getMiddleName()), i);
					}
					if (authorList.get(j).getLastName() != null && authorList.get(j).getLastName().length() > 0) {
						addWord(authors, correctWord(authorList.get(j).getLastName()), i);
					}

				}

				// titles
				String[] title = fb.getDescription().getTitle().split(" ");
				for (int j = 0; j < title.length; j++) {
					addWord(titles, correctWord(title[j].toLowerCase()), i);
				}

				// words
				ArrayList<Section> sections = fb.getSections();
				for (int j = 0; j < sections.size(); j++) {
					ArrayList<String> paragraphs = sections.get(j).getParagraphs();
					for (int k = 0; k < paragraphs.size(); k++) {
						String[] content = paragraphs.get(k).split(" ");
						for (int m = 0; m < content.length; m++) {
							addWord(body, correctWord(content[m]), i);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

		addToFile(authors, "PR11Authors.txt");
		addToFile(titles, "PR11Titles.txt");
		addToFile(body, "PR11Bodies.txt");
	}

	public static String correctWord(String a) {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < a.length(); i++) {
			char ch = Character.toLowerCase(a.charAt(i));
			if (ch != '"' && (Character.isAlphabetic(ch) || (i > 0 && i < a.length() - 1
					&& Character.isAlphabetic(a.charAt(i - 1)) && Character.isAlphabetic(a.charAt(i + 1)))))
				res.append(ch);
		}
		return res.toString();
	}

	public ArrayList<Word> rangeBooks(String str) {

		String[] words = str.split(" ");
		double maxC = words.length;
		ArrayList<Word> wordAr = new ArrayList<>();

		HashMap<Integer, Double> coefficientMap = new HashMap<>();
		for (int i = 0; i < words.length; i++) {
			if (body.containsKey(correctWord(words[i])))
				for (int j : body.get(correctWord(words[i]))) {
					if (!coefficientMap.containsKey(j))
						coefficientMap.put(j, (0.5) / maxC);
					else {
						coefficientMap.put(j, coefficientMap.get(j) + (0.5) / maxC);
					}
				}
			if (titles.containsKey(correctWord(words[i])))
				for (int j : titles.get(correctWord(words[i]))) {
					if (!coefficientMap.containsKey(j))
						coefficientMap.put(j, (0.3) / maxC);
					else {
						coefficientMap.put(j, coefficientMap.get(j) + (0.3) / maxC);
					}
				}
			if (authors.containsKey(correctWord(words[i])))
				for (int j : authors.get(correctWord(words[i]))) {
					if (!coefficientMap.containsKey(j))
						coefficientMap.put(j, 0.2);
					else {
						coefficientMap.put(j, coefficientMap.get(j) + (0.2) / maxC);
					}
				}
		}
		for (int j : coefficientMap.keySet()) {
			wordAr.add(new Word(j, coefficientMap.get(j)));
		}
		Collections.sort(wordAr);
		return wordAr;
	}

	public String searchRangedFiles(String request) {
		System.out.println("Search request: " + request);
		ArrayList<Word> result = rangeBooks(request);
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < result.size(); i++) {
			res.append(i).append(") File ID: ").append(result.get(i).id).append("\n  Relevance coefficient: ")
					.append(result.get(i).coeff).append("\n  Path: ").append(filelist.get(result.get(i).id))
					.append('\n');
		}
		return res.toString();
	}

	public void addWord(HashMap<String, TreeSet<Integer>> map, String str, int id) {
		if (str.length() > 0)
			if (map.containsKey(str)) {
				map.get(str).add(id);
			} else {
				map.put(str, new TreeSet<>());
				map.get(str).add(id);
			}
	}

	public void addToFile(HashMap<String, TreeSet<Integer>> map, String filename) {
		File file = new File(filename);
		try {
			BufferedWriter bf = new BufferedWriter(new FileWriter(file));
			for (String st : map.keySet())
				bf.write(st + ' ' + map.get(st) + '\n');

			bf.flush();
			bf.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
