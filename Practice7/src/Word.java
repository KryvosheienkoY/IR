
public class Word implements Comparable<Word> {
	public int id;
	public double coeff;

	public Word(int id, double coeff) {
		this.id = id;
		this.coeff = coeff;
	}

	@Override
	public int compareTo(Word a) {
		if (this.coeff > a.coeff)
			return -1;
		if (this.coeff < a.coeff)
			return 1;
		return 0;
	}

}
