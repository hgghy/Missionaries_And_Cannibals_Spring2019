
import java.util.ArrayList;
import java.util.List;

public class MissionariesAndCannibalsNode implements INode, Comparable<INode>
{
	// Note that the following code is designed to work for any number of missionaries and cannibals and any number of landmasses, 
	// and bigger boats can be implemented by changing boatSize
	// but does not allow for more boats, or landmasses impassable by boat, or landmasses that can only hold a limited number of people.
	// landmasses - i.e. riverbanks or islands in the river
	
	private String state; // represents the state of the puzzle at this node. e.g. the starting node for the original problem is _uMMCC
	private Integer level; // the lowest level at which this node has been reached
	private List<INode> parents = new ArrayList<INode>(); // list of parents from which this node has been reached. the first one will be the one with the lowest level, the rest are unsorted
	private int boatSize = 2; // how many passengers the boat can carry
	
	public MissionariesAndCannibalsNode(String state)
	{
		//new MissionariesAndCannibalsNode(state, 0, null);
		this.state = state;
		this.level = 0;
	}
	
	private MissionariesAndCannibalsNode(String state, int level, INode parent)
	{
		this.state = state;
		this.level = level;
		parents.add(parent);
	}
	
	@Override
	public int compareTo(INode otherNode) // this method is used to sort a priority queue in MissionariesAndCannibalsPuzzleSearch
	{
		return this.getLowestLevel() - otherNode.getLowestLevel();
	}
	
	@Override
	public void addParent(INode parent)
	{
		// the first parent has to be one of those with the lowest level
		if(parent.getLowestLevel() + 1 < level)
		{
			level = parent.getLowestLevel() + 1;
			parents.add(0, parent);
		}
		else
			parents.add(parent);
	}
	
	private boolean isLoss()
	{
		String[] tokens = state.split("_");
		
		// note that despite the double for loop the complexity is O(N) where N is the length of the state string
		for(int i = 0; i < tokens.length; i++)
		{
			int missionaries = 0;
			int cannibals = 0;
			for(int j = 0; j < tokens[i].length(); j++)
			{
				switch(tokens[i].charAt(j))
				{
					case 'M':
						missionaries++;
						break;
					case 'C':
						cannibals++;
						break;
				}
			}
			if(cannibals > missionaries && missionaries > 0)
				return true;
		}
		return false;
	}
	
	@Override
	public List<INode> getChildren() 
	{
		List<INode> children = new ArrayList<INode>();
		
		if(isLoss()) // and therefore has no children
			return children;
		
		String[] tokens = state.split("_", -1); // the -1 is necessary so that trailing empty tokens aren't discarded
		
		int boatIndex = -1; // index of the landmass where the boat is
		for(int i = 0; i < tokens.length && boatIndex == -1; i++)
		{
			if(tokens[i].contains("u"))
				boatIndex = i;
		}
			
		List<String> possibleTransports = calculatePossibleTransports(tokens[boatIndex]);
		for(String transport : possibleTransports)
		{
			children.addAll(calculateAllChildrenForGivenTransport(tokens, boatIndex, transport));
		}
		
		return children;
	}
	
	private List<String> calculatePossibleTransports(String island)
	{
		List<String> possibleTransports = new ArrayList<String>();
		
		// figure out who is on this island and thus the possible transports
		// in the starting position _uMMCC the possible transports are: uM, uMM, uMC, uC, uCC
		// in the position uMM_CC the possible transports are: uM, uMM
		
		int missionaries = 0;
		int cannibals = 0;
		for(int i = 0; i < island.length(); i++)
		{
			switch(island.charAt(i))
			{
				case 'M':
					missionaries++;
					break;
				case 'C':
					cannibals++;
					break;
			}
		}
		
		// for each possible transport
		for(int missionariesOnThisTransport = 0; missionariesOnThisTransport <= missionaries; missionariesOnThisTransport++)
		{
			for(int cannibalsOnThisTransport = 0; cannibalsOnThisTransport <= cannibals; cannibalsOnThisTransport++)
			{
				if(cannibalsOnThisTransport + missionariesOnThisTransport > 0
					&& cannibalsOnThisTransport + missionariesOnThisTransport <= boatSize)
				{
					String transport = "u";
					
					
					// i know java 11 has the repeat function, but this is written in an earlier java, and the other ways to do this are less readable and no prettier.
					for(int i = 0; i < missionariesOnThisTransport; i++)
					{
						transport += "M";
					}
					
					for(int i = 0; i < cannibalsOnThisTransport; i++)
					{
						transport += "C";
					}
					
					possibleTransports.add(transport);
				}
			}
		}
		
		return possibleTransports;
	}
	
	private List<INode> calculateAllChildrenForGivenTransport(String[] tokens, int boatIndex, String transport)
	{ // each transport has at least one possible destination. this method calculates the children for each of the resulting states.
		List<INode> results = new ArrayList<INode>();
		
		// remove the boat and people being transported from their old landmass (where they started this transport)
		String oldLandmass = tokens[boatIndex];
		for(int i = 0; i < transport.length(); i++)
		{
			oldLandmass = oldLandmass.replaceFirst("" + transport.charAt(i), "");
		}
		
		for(int i = 0; i < tokens.length; i++) // for each landmass in the game...
		{
			if(i != boatIndex) // except for the one that the boat is from
			{ // i.e. each possible destination for the transport
				String newNodeState = "";
				
				// create the state of the new node
				for(int j = 0; j < tokens.length; j++)
				{
					if(j == boatIndex) // this is the former location of the transport
						newNodeState += oldLandmass;
					else if(j == i) // this is the destination of the transport
						newNodeState += sort(transport + tokens[j], j, tokens.length);
					else // this is neither the former location of the transport nor it's destination
						newNodeState += tokens[j];
					newNodeState += "_"; // this will result in a trailing '_', which will be removed
				}
				results.add(new MissionariesAndCannibalsNode(newNodeState.substring(0, newNodeState.length() - 1), level + 1, this));
			}
		}
		
		return results;
	}
	
	private String sort(String landmassContents, int index, int maxIndex) // sorts the contents of the landmass so that 'u' comes before 'M' which comes before 'C', unless this landmass is the left of the middle, in which case 'u' comes last
	{
		String result = "";
		int missionaries = 0;
		int cannibals = 0;
		for(int i = 0; i < landmassContents.length(); i++)
		{
			switch(landmassContents.charAt(i))
			{
				case 'M':
					missionaries++;
					break;
				case 'C':
					cannibals++;
					break;
			}
		}
		
		// i know java 11 has the repeat function, but this is written in an earlier java, and the other ways to do this are less readable and no prettier.
		for(int i = 0; i < missionaries; i++)
			result += 'M';
		for(int i = 0; i < cannibals; i++)
			result += 'C';
		
		if(index < maxIndex / 2) // this landmass is left of the middle
			result += "u";
		else
			result = "u" + result;
		
		return result;
	}

	@Override
	public int getLowestLevel()
	{
		return level;
	}

	@Override
	public INode getLowestLevelParent() // i.e. get the parent with the lowest level
	{
		if(parents.isEmpty())
			return null;
		
		return parents.get(0);
	}
	
	@Override
	public List<INode> getParents() 
	{
		return parents;
	}

	@Override
	public boolean isSolution() 
	{
		// if missionaries are dead, then this is not the solution
		if(isLoss())
			return false;
		
		// if not everyone is on the near side of the river, then this is not the solution
		String[] tokens = state.split("_");
		for(int i = 1; i < tokens.length; i++) // note that we are skipping the first one
		{
			if(!tokens[i].equals(""))
				return false;
		}
		
		return true;
	}
	
	// the professor told us to include the following two methods, though we were not to implement them in this assignment.
	// interestingly, we never did end up making them.
	@Override
	public double getHValue() throws Exception 
	{
		throw new Exception("This method has not yet been implemented!");
	}

	@Override
	public double getGValue() throws Exception 
	{
		throw new Exception("This method has not yet been implemented!");
	}
	
	@Override
	public String toString()
	{
		return state;
	}
	
	@Override
	public int hashCode() // this will be stored in a hash set, which can be searched efficiently
	{	
		return state.hashCode();
	}
	
	@Override
	public boolean equals(Object otherNode) // this will be stored in a hash set, which can be searched efficiently
	{	
		return state.equals(otherNode.toString());
	}
}
