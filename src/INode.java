
import java.util.List;

public interface INode
{
	List<INode> getChildren(); // gets all possible nodes that can be reached from this node (in one hop)
	int getLowestLevel(); // gets an integer representing the lowest level at which this node was encountered. For example, the root is 0, a node discovered three steps away from the root is 3, etc.
	INode getLowestLevelParent(); // gets the node through which this node was discovered at the shortest level
	List<INode> getParents(); // gets all nodes through which this node was discovered
	void addParent(INode parent); // adds a parent to an existing node
	boolean isSolution(); // true if this node is a solution of the puzzle, false otherwise
	
	// the professor told us to include the following two methods, though we were not to implement them in this assignment.
	// interestingly, we never did end up making them.
	double getHValue() throws Exception; // not yet implemented
	double getGValue() throws Exception; // not yet implemented
}
