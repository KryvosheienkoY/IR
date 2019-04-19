import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.kursx.parser.fb2.Element;
import com.kursx.parser.fb2.FictionBook;
import com.kursx.parser.fb2.Section;

import edu.princeton.cs.algs4.BTree;
import edu.princeton.cs.algs4.StdOut;

public class Dictionary {

	private static final String DELIMITERS = " ‘“”,.…\\1234567890/!?:;—_'+()\"";
	private int wordCounter;
	private int sizeCounter;
	private int counter = 0;
	private HashMap<Integer, String> fileIDlist;

	// TreeMap - word - set of doc ids
	private TreeMap<String, TreeSet<Integer>> wordMapInvIndex;
	// BTree - word - set of doc ids
	private BTree<String, TreeSet<Integer>> wordTree;
	//
	private TreeMap<String, TreeSet<Integer>> commutativeIndexMap;
	// 3gram index
	TreeMap<String, TreeSet<String>> threeGramIndexMap;

	private SPIMI spimi;
	private final String output_path;
	private long startTime;
	private long creatingBlocksTime;
	private long estimatedTime;
	private long stopTime;

	public Dictionary(ArrayList<File> files) {
		this.output_path = "C:\\Users\\Julia\\Desktop\\infosearch\\Practice5\\";
		spimi = new SPIMI(this.output_path);
		startTime = System.currentTimeMillis();
//		this.wordTree = new BTree<>();
//		this.commutativeIndexMap = new TreeMap<>();
//		this.threeGramIndexMap = new TreeMap<>();
//		this.wordMapInvIndex = new TreeMap<>();

		this.wordCounter = 0;
		this.sizeCounter = 0;
		this.fileIDlist = new HashMap<>();

		for (File file : files) {
			// create id for file
			// register it in map
			// read file and add words
			fillDictionary(file);
		}
		setCreatingBlocksTime(System.currentTimeMillis() - startTime);
		int num = this.spimi.getBlockNum();
		String[] input_loc = new String[num];
		for (int i = 0; i < num; i++) {
			input_loc[i] = this.output_path + String.valueOf(i + 1) + ".txt";
		}
		try {
			spimi.mergeBlocks(input_loc, "Volabulary_SPIMI.txt");
			setEstimatedTime(System.currentTimeMillis() - startTime);
		} catch (IOException e) {
			e.printStackTrace();
		}
		setStopTime(System.currentTimeMillis() - startTime);
	}

	/**
	 * Reads file and fills dictionary with found words
	 * 
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public void fillDictionary(File file) {
		FictionBook fb = null;
		try {
			fb = new FictionBook(file);
			this.sizeCounter += (file.length() / 1024);
			this.fileIDlist.put(++this.counter, file.getName());
			System.out.println("Reading file num " + this.counter + " \" " + file.getName() + " \" ");
		} catch (OutOfMemoryError | ParserConfigurationException | IOException | SAXException e1) {
			e1.printStackTrace();
			return;
		}

		ArrayList<Section> sections = fb.getBody().getSections();
		for (int j = 0; j < sections.size(); j++) {
			ArrayList<Element> elements = sections.get(j).getElements();
			for (int k = 0; k < elements.size(); k++) {
				String text = elements.get(k).getText();
				if (text != null) {
					StringTokenizer st = new StringTokenizer(text, DELIMITERS);
					while (st.hasMoreTokens()) {
						String word = st.nextToken().toLowerCase();
						if (isWord(word)) {
							wordCounter++;
							spimi.addWordToBlock(word, counter);
						}
					}
				}
			}
		}
//			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//				this.sizeCounter += (file.length() / 1024);
//				// create id for file and add this pair to hashmap (key -id, value - name)
//				String s;
//				while ((s = reader.readLine()) != null) {
//					// fillTwoIndex(s);
//					// fillCoordinateIndex(s);
//					StringTokenizer st = new StringTokenizer(s, DELIMITERS);
//					while (st.hasMoreTokens()) {
//						String word = st.nextToken().toLowerCase();
//						if (isWord(word)) {
//							wordCounter++;
////						addWordToTree(word);
////						addWordToCommutativeMap(word);
////						addWordToThreeGramSet(word);
////						addWordToInvertedIndexMap(word);
//							spimi.addWordToBlock(word, counter);
//						}
//					}
//				}
//
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//				System.out.println("File wasn`t found!");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
	}

	private void addWordToInvertedIndexMap(String word) {
		if (this.wordMapInvIndex.containsKey(word)) {
			TreeSet<Integer> set = this.wordMapInvIndex.get(word);
			set.add(counter);
			this.wordMapInvIndex.put(word, set);
		} else {
			TreeSet<Integer> set = new TreeSet<>();
			set.add(counter);
			this.wordMapInvIndex.put(word, set);
		}
	}

	private void addWordToThreeGramSet(String word) {
		String[] gramArray = makeThreeGramAr(word);
		for (String gram : gramArray) {
			if (this.threeGramIndexMap.containsKey(gram)) {
				this.threeGramIndexMap.get(gram).add(word);
			} else {
				TreeSet<String> set = new TreeSet<>();
				set.add(word);
				this.threeGramIndexMap.put(gram, set);
			}
		}
	}

	private String[] makeThreeGramAr(String word) {
		int length = word.length();
		String w = "$" + word + "$";
		String[] gramArray = new String[length];
		char ar[] = new char[3];
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < 3; j++) {
				ar[j] = w.charAt(i + j);
			}
			gramArray[i] = new String(ar);
		}
		return gramArray;
	}

	private void addWordToCommutativeMap(String word) {
		// create all variants of the word
		int length = word.length() + 1;
		String w$ = word + "$";
		String[] strAr = new String[length];
		char[] wordCh = new char[length];
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				int delta = i + j;
				if (delta >= length)
					delta = delta - length;
				wordCh[j] = w$.charAt(delta);
			}
			strAr[i] = new String(wordCh);
		}
		TreeSet<Integer> set;
		// check if is alredy added
		if (this.commutativeIndexMap.containsKey(w$)) {
			set = this.commutativeIndexMap.get(w$);
		} else {
			set = new TreeSet<>();
		}
		set.add(counter);
		for (String str : strAr) {
			this.commutativeIndexMap.put(str, set);
		}

	}

	private void addWordToTree(String word) {
//			TreeSet<Integer> set = this.wordTree.get(w);
		if (this.wordTree.get(word) != null) {
			// set.add(counter);
			this.wordTree.get(word).add(counter);
		} else {
			TreeSet<Integer> set = new TreeSet<>();
			set.add(counter);
			this.wordTree.put(word, set);
		}
	}

	/**
	 * Checks if the string is a word
	 * 
	 * @param word
	 * @return true if the string is a word
	 */
	private boolean isWord(String word) {
		// first and last symbols of the word must be letters
		if (!(Character.isAlphabetic(word.charAt(0)) && Character.isAlphabetic(word.charAt(word.length() - 1)))) {
			return false;
		}
		for (int i = 1; i < word.length() - 1; i++) {
			char c = word.charAt(i);
			if (!(Character.isAlphabetic(c) || c == '-' || c == '’')) {
				return false;
			}
		}
		return true;
	}

	public StringBuilder showWordsTreeMap() {
		StringBuilder statistics = new StringBuilder();
		statistics.append(this.wordTree.toString());
		return statistics;
	}

	public StringBuilder showCommutativeIndexMap() {
		StringBuilder statistics = new StringBuilder();
		for (String word : this.commutativeIndexMap.keySet()) {
			statistics.append("\n Word: ").append(word).append("\r\n");
			TreeSet<Integer> set = this.commutativeIndexMap.get(word);
			for (Integer key : set) {
				statistics.append(" File: ").append(key).append("\r\n");
			}
			statistics.append("\r\n");
		}
		return statistics;
	}

	public StringBuilder showThreeGramIndexMap() {
		StringBuilder statistics = new StringBuilder();
		for (String gr : this.threeGramIndexMap.keySet()) {
			statistics.append("\n 3Gram: ").append(gr).append("\r\n");
			TreeSet<String> set = this.threeGramIndexMap.get(gr);
			for (String key : set) {
				statistics.append(" Word: ").append(key).append("\r\n");
			}
			statistics.append("\r\n");
		}
		return statistics;
	}

	public int getSize() {
		return this.wordMapInvIndex.size();
	}

	public int getCollectionWordSize() {
		return this.wordCounter;
	}

	public int getCollectionSize() {
		return this.sizeCounter;
	}

	public StringBuilder showFilesID() {
		StringBuilder list = new StringBuilder();
		for (Integer id : this.fileIDlist.keySet()) {
			list.append("File: ").append(this.fileIDlist.get(id)).append(" - ID: ").append(id).append("\r\n");
		}
		return list;
	}

	public StringBuilder makeWildCardSearch(String line) {
		StringBuilder strbuilder = new StringBuilder();

		StringTokenizer tk = new StringTokenizer(line, DELIMITERS);
		String word = tk.nextToken();
		TreeSet<String> searchW = commutateWildCardSearch(word + "$");
		if (searchW.isEmpty())
			return strbuilder.append("No results.");
		for (String string : searchW) {
			strbuilder.append("Word - ").append(string).append('\n');
			for (Integer id : this.commutativeIndexMap.get(string + '$')) {
				strbuilder.append(" File - ").append(id).append('\n');
			}
		}
		return strbuilder;
	}

	private TreeSet<String> commutateWildCardSearch(String str) {
		TreeSet<String> res = new TreeSet<>();
		StringTokenizer tk = new StringTokenizer(str, "*");
		ArrayList<String> subWords = new ArrayList<>();
		while (tk.hasMoreTokens()) {
			String s = tk.nextToken();
			subWords.add(s);
		}
		if (subWords.isEmpty())
			return this.commutateWildCardSearch(str);
		Set<String> k = this.commutativeIndexMap.keySet();
		TreeSet<String> keys = new TreeSet<>(k);
		String lastStr = subWords.get(0);
		int length = lastStr.length();
		char lastCh = (char) ((lastStr.charAt(length - 1)) + 1);
		String lastS = lastStr.substring(0, length - 1) + lastCh;
		Set<String> subS;
		if (subWords.size() > 1) {
			String sw = subWords.get(subWords.size() - 1) + subWords.get(0);
			String sw2 = subWords.get(subWords.size() - 1) + lastS;

			subS = keys.subSet(sw, sw2);
		} else {
			subS = keys.subSet(lastStr, lastS);
		}

		for (String w : subS) {
			// String sub1 = w.substring(length, w.length());
			String normalized = normalize(w);
			boolean contains = true;
			for (String subw : subWords) {
				if (!w.contains(subw)) {
					contains = false;
					break;
				}
			}
			if (contains)
				res.add(normalized);
		}
		return res;
	}

	private String normalize(String w) {
		int i = 0;
		for (; i < w.length(); i++) {
			if (w.charAt(i) == '$')
				break;
		}
		return w.substring(i + 1, w.length()) + w.substring(0, i);
	}

	private String threegrammWildCardSearch(String word) {
		// String grams[] = makeThreeGramAr(word);
		int length = word.length();
		String w = "$" + word + "$";
		String[] gramArray = new String[length];
		char ar[] = new char[3];
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < 3; j++) {
				char c = w.charAt(i + j);
				ar[j] = c;
				if (c == '*') {
					if (j != 0)
						i++;
					break;
				}
			}
			gramArray[i] = new String(ar);
		}

		return null;
	}

	private TreeSet<Integer> makeANDoperationSet(Set<Integer> set1, Set<Integer> set2) {
		TreeSet<Integer> res = new TreeSet<>();
		if (set1 == null || set2 == null)
			return null;
		for (Integer integer : set1) {
			if (set2.contains(integer))
				res.add(integer);
		}
		return res;
	}

	/**
	 * Gathers and returns information in dictionary
	 * 
	 * @return StringBuilder
	 */
	public StringBuilder showWordsInvIndex() {
		StringBuilder statistics = new StringBuilder(128);
		for (String word : this.wordMapInvIndex.keySet()) {
			statistics.append("\n Word: ").append(word).append("\r\n");
			TreeSet<Integer> set = this.wordMapInvIndex.get(word);
			for (Integer key : set) {
				statistics.append(" File: ").append(key).append("\r\n");
			}
			statistics.append("\r\n");
		}
		return statistics;
	}

	public long getEstimatedTime() {
		return estimatedTime;
	}

	public void setEstimatedTime(long estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	public long getCreatingBlocksTime() {
		return creatingBlocksTime;
	}

	public void setCreatingBlocksTime(long creatingBlocksTime) {
		this.creatingBlocksTime = creatingBlocksTime;
	}

	public long getStopTime() {
		return stopTime;
	}

	public void setStopTime(long stopTime) {
		this.stopTime = stopTime;
	}
}
