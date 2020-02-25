
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class MissionariesAndCannibalsPuzzleSearch implements IPuzzleSearch
{
	@Override
	public void search(INode start) // searches the list of INodes with BFS and prints out the solution
	{
		HashSet<INode> alreadyReached = new HashSet<INode>();
		PriorityQueue<INode> nodesInQueue = new PriorityQueue<INode>();

		nodesInQueue.add(start);
		alreadyReached.add(start);
		
		INode currentlyBeingSearched;
		boolean solutionFound = false;
		while(!nodesInQueue.isEmpty() && !solutionFound)
		{
			currentlyBeingSearched = nodesInQueue.poll();
			
			if(!currentlyBeingSearched.isSolution())
			{
				List<INode> children = currentlyBeingSearched.getChildren();
				
				alreadyReached.forEach(i -> {
					if(children.contains(i))
					{
						i.addParent(children.get(0).getLowestLevelParent()); // children.get(0).getLowestLevelParent() is a hack to get at currentlyBeingSearched. "Local variable currentlyBeingSearched defined in an enclosing scope must be final or effectively final"
						children.remove(i);
					}
				});
				
				nodesInQueue.addAll(children);
				alreadyReached.addAll(children);
			}
			else
			{
				solutionFound = true;
				
				String solutionPath = "";
				while(currentlyBeingSearched != null)
				{
					solutionPath = currentlyBeingSearched.toString() + "\n" + solutionPath;
					if(currentlyBeingSearched.equals(start))
						currentlyBeingSearched = null;
					else
						currentlyBeingSearched = currentlyBeingSearched.getLowestLevelParent();
				}
				
				System.out.println(solutionPath);
			}
		}
		
		if(!solutionFound)
		{
			System.out.println("No solution possible for: " + start);
		}
	}
}
