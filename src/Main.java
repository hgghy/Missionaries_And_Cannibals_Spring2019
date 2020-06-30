
public class Main
{
	public static void main(String[] args)
	{
		// Modify this string to change the program's input
		String startingState = "_uMMCC";
		
		MissionariesAndCannibalsPuzzleSearch searcher = new MissionariesAndCannibalsPuzzleSearch();
		
		String solutionPath = searcher.search(new MissionariesAndCannibalsNode(startingState));
		
		System.out.println(solutionPath);
	}
}
