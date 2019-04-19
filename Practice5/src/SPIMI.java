import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class SPIMI {

	private static final String STR_CRLF = "\r\n";
	private static final String STR_SPACE = " ";
	private static long BLOCK_SIZE = (long) (Runtime.getRuntime().totalMemory() * 0.9 );
	private int bytes_used = 0;
	private int counterBlock = 1;
	// word - ids
	private TreeMap<String, TreeSet<Integer>> wordMap;
	private final String vocabularyPath;

	public SPIMI(String path) {
		setWordMap(new TreeMap<String, TreeSet<Integer>>());
		this.vocabularyPath = path;
	}

	int getBlockNum() {
		return this.counterBlock;
	}

	/**
	 * Add word to the block
	 * 
	 * @param word, doc id
	 */
	public void addWordToBlock(String word, Integer id) {

		TreeSet<Integer> set = this.wordMap.computeIfAbsent(word, v -> {
			try {
				bytes_used += word.getBytes("UTF-8").length;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new TreeSet<Integer>();
		});

		set.add(id);
		bytes_used += 4;
		

		if (bytes_used >= BLOCK_SIZE) {
			flushBlock(); // close the block
			allocateNewBlock(); // new block
		}
	}

	/**
	 * Create new block
	 */
	public void allocateNewBlock() {
		System.out.println("Free memory - "+Runtime.getRuntime().freeMemory());
		this.wordMap.clear();
		bytes_used = 0;
		counterBlock++;
		System.out.println("Creating block " + this.counterBlock);
	}

	/**
	 * close block - write it to a file
	 */
	public void flushBlock() {
		if (bytes_used > 0) {
			writeBlockToDisk(vocabularyPath + String.valueOf(this.counterBlock) + ".txt");
		}
	}

	/**
	 * 
	 * @param location - location of file in which we should write a block
	 * @return if success
	 */
	public boolean writeBlockToDisk(String location) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(location));
			System.out.println("Writing "+wordMap.size()+" words to file "+location);
			for (Entry<String, TreeSet<Integer>> entry : wordMap.entrySet()) {
				writer.write(entry.getKey()); // word
				writer.write(' ');
				for (Integer docID : entry.getValue()) {
					writer.write(docID.toString());
					writer.write(' ');
				}
				writer.write(STR_CRLF);
			}
			writer.flush();
			writer.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}


	/**
	 * 
	 * @param inPath  location of all block files to be merged
	 * @param outPath
	 * @return true if the operation was successful
	 * @throws IOException
	 */
	public void mergeBlocks(String[] inPath, String outPath) throws IOException {
		// close block
		flushBlock();

		// start merging blocks
		BufferedWriter writer = new BufferedWriter(new FileWriter("dictionary_SPIMI.txt"));

		TreeMap<String, Word> mergeMap = new TreeMap<String, Word>();
		int length = inPath.length;
		BufferedReader[] readersAr = new BufferedReader[length];

		for (int i = 0; i < length; i++) {
			readersAr[i] = new BufferedReader(new FileReader(inPath[i]));
			String lineStr = readersAr[i].readLine();
			String[] arrWordsIds = lineStr.split(STR_SPACE);
			while (mergeMap.containsKey(arrWordsIds[0])) {
				TreeSet<Integer> idSet = new TreeSet<>();
				for (int j = 1; j < arrWordsIds.length; j++) {
					String s = arrWordsIds[j];
					Integer l = Integer.parseInt(s);
					idSet.add(l);
				}
				mergeMap.get(arrWordsIds[0]).ids.addAll(idSet);
				lineStr = readersAr[i].readLine();
				arrWordsIds = lineStr.split(STR_SPACE);
			}
			if (!mergeMap.containsKey(arrWordsIds[0])) {
				TreeSet<Integer> trSet = new TreeSet<>();
				for (int j = 1; j < arrWordsIds.length; j++) {
					trSet.add(Integer.parseInt(arrWordsIds[j]));
				}
				mergeMap.put(arrWordsIds[0], new Word(arrWordsIds[0], i, trSet));
			}
		}
		while (!mergeMap.isEmpty()) {
			String firstKey = mergeMap.firstKey();
			Word firstW = mergeMap.get(firstKey);

			writer.write(firstKey + " - ");
			for (Integer i : firstW.ids) {
				writer.write(i + STR_SPACE);
			}
			writer.write(STR_CRLF);

			mergeMap.remove(firstKey);

			String str = readersAr[firstW.block].readLine();
			if (str != null) {
				String[] elemAr = str.split(STR_SPACE);
				TreeSet<Integer> treeSetId = new TreeSet<>();
				for (int j = 1; j < elemAr.length; j++) {
					treeSetId.add(Integer.parseInt(elemAr[j]));
				}

				while (mergeMap.containsKey(elemAr[0])) {
					mergeMap.get(elemAr[0]).ids.addAll(treeSetId);
					str = readersAr[firstW.block].readLine();
					if (str != null) {
						elemAr = str.split(STR_SPACE);
					} else {
						break;
					}
				}
				if (!mergeMap.containsKey(elemAr[0])) {
					mergeMap.put(elemAr[0], new Word(elemAr[0], firstW.block, treeSetId));
				}
			}
		}

		writer.flush();
		writer.close();

		for (BufferedReader bufferedReader : readersAr) {
			bufferedReader.close();
		}

	}

	public TreeMap<String, TreeSet<Integer>> getWordMap() {
		return wordMap;
	}

	public void setWordMap(TreeMap<String, TreeSet<Integer>> wordMap) {
		this.wordMap = wordMap;
	}
}