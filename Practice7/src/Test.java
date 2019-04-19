import java.util.Scanner;

public class Test {

	public static void main(String[] args) {

		AnalyzerFB2 fb = new AnalyzerFB2("C:\\Users\\Julia\\Desktop\\infosearch\\FB2_B");
		fb.readEntry();
		String answer;
		// "я был"
		String request;
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Enter request:");
			request = sc.nextLine();
			System.out.println(fb.searchRangedFiles(request));
			System.out.println("Continue? (y/n): ");
			answer = sc.nextLine();
			if (!(answer.equals("y")))
				break;
		}
		System.out.println("exit..");
	}

}
