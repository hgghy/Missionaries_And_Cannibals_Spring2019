
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class MissionariesAndCannibalsPuzzleSearch implements IPuzzleSearch
{
	@Override
	public String search(INode startNode) // searches the list of INodes with BFS and prints out the solution
	{
		HashSet<INode> alreadyReached = new HashSet<INode>(); // we will use this to make sure that we aren't searching the same node multiple times
		PriorityQueue<INode> nodesInQueue = new PriorityQueue<INode>(); // list of things to search sorted by level. by searching the ones with the lowest level first, we know that the first solution found is among those tied with the lowest level

		nodesInQueue.add(startNode);
		alreadyReached.add(startNode);
		
		INode currentlyBeingSearched;
		while(!nodesInQueue.isEmpty())
		{
			currentlyBeingSearched = nodesInQueue.poll();
			
			if(!currentlyBeingSearched.isSolution())
			{
				// add each child to nodesInQueue...
				
				List<INode> children = currentlyBeingSearched.getChildren();
				
				// ...if it hasn't already been found
				// note that we can't do for(INode node : children) because then we wouldn't know which node in alreadyReached is the one to which we add the parent. HashSet doesn't have a get()
				for(INode node : alreadyReached)
				{
					if(children.contains(node))
					{
						node.addParent(currentlyBeingSearched);
						children.remove(node);
					}
				}
				
				nodesInQueue.addAll(children);
				alreadyReached.addAll(children);
			}
			else
			{
				return "The solution is: \n" + calculatePath(startNode, currentlyBeingSearched);
			}
		}
		
		return "No solution possible for: " + startNode;
	}
	
	// once the solution has been found, all we need to do to find the path is iterate through the lowet level parent until we find the startNode
	// i don't like using actual recursion for fear of StackOverflowExceptions so this just mimics it by resetting the endnode to the parent.
	private String calculatePath(INode startNode, INode endNode)
	{
		String solutionPath = "";
		while(endNode != null)
		{
			solutionPath = endNode.toString() + "\n" + solutionPath;
			if(endNode.equals(startNode))
				endNode = null;
			else
				endNode = endNode.getLowestLevelParent();
		}
		
		return solutionPath;
	}
}
