import java.util.TreeSet;

public class Word {
	public String name;
	public int block;
	public TreeSet<Integer> ids;

	public Word(String n, int b, TreeSet<Integer> id) {
		this.name = n;
		this.block = b;
		this.ids = id;
	}
}
