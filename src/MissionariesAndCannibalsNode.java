
import java.util.ArrayList;
import java.util.List;

public class MissionariesAndCannibalsNode implements INode, Comparable<INode>
{
	// Note that the following code is designed to work for any number of missionaries and cannibals and any number of landmasses, 
	// and bigger boats can be implemented by changing boatSize
	// but does not allow for more boats, or impassable landmasses, or landmasses that can only hold a limited number of people.
	// landmasses - i.e. riverbanks or islands in the river
	
	private String state; // represents the state of the puzzle at this node. e.g. the starting node for the original problem is _uMMCC
	private Integer level; // the lowest level at which this node has been reached
	private List<INode> parents = new ArrayList<INode>(); // list of parents from which this node has been reached. the first one will be the one with the lowest level, the rest are unsorted
	private int boatSize = 2;
	
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
    public int compareTo(INode otherNode)
	{
        return this.getLowestLevel() - otherNode.getLowestLevel();
    }
	
	@Override
	public void addParent(INode parent)
	{
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
		
		for(int i = 0; i < tokens.length; i++)
		{
			int missionaries = 0;
			int cannibals = 0;
			for(int j = 0; j < tokens[i].length(); j++)
			{
				if(tokens[i].charAt(j) == 'M')
					missionaries++;
				else if(tokens[i].charAt(j) == 'C')
					cannibals++;
			}
			if(cannibals > missionaries && missionaries > 0)
				return true;
		}
		return false;
	}
	
	@Override
	public List<INode> getChildren() 
	{
		List<INode> results = new ArrayList<INode>();
		
		if(isLoss()) // and therefore has no children
			return results;
			
		String[] tokens = state.split("_", -1);
		
		int boatIndex = -1; // index of the landmass where the boat is
		for(int i = 0; i < tokens.length && boatIndex == -1; i++)
		{
			if(tokens[i].contains("u"))
				boatIndex = i;
		}
		
		// figure out who is on this island and thus the possible transports
		// in the starting position _uMMCC the possible transports are: uM, uMM, uMC, uC, uCC
		// in the position uMM_CC the possible transports are: uM, uMM
		
		int missionaries = 0;
		int cannibals = 0;
		for(int i = 0; i < tokens[boatIndex].length(); i++)
		{
			if(tokens[boatIndex].charAt(i) == 'M')
				missionaries++;
			else if(tokens[boatIndex].charAt(i) == 'C')
				cannibals++;
		}
		
		// for each possible transport
		for(int missionariesOnThisTransport = 0; missionariesOnThisTransport <= missionaries; missionariesOnThisTransport++)
		{
			for(int cannibalsOnThisTransport = 0; cannibalsOnThisTransport <= cannibals 
					&& cannibalsOnThisTransport + missionariesOnThisTransport <= boatSize; cannibalsOnThisTransport++)
			{
				if(cannibalsOnThisTransport + missionariesOnThisTransport > 0)
				{
					String transport = "u";
					
					for(int i = 0; i < missionariesOnThisTransport; i++)
					{
						transport += "M";
					}
					
					for(int i = 0; i < cannibalsOnThisTransport; i++)
					{
						transport += "C";
					}
					results.addAll(calculateChildrenForGivenTransport(tokens, boatIndex, transport));
				}
			}
		}
		
		// found a better way to do this. (above)
//		if(missionaries >= 1)
//		{
//			results.addAll(calculateChildrenForGivenTransport(tokens, boatIndex, "uM"));
//			
//			if(missionaries >= 2)
//				results.addAll(calculateChildrenForGivenTransport(tokens, boatIndex, "uMM"));
//			
//			if(cannibals >= 1)
//				results.addAll(calculateChildrenForGivenTransport(tokens, boatIndex, "uMC"));
//		}
//		
//		if(cannibals >= 1)
//		{
//			results.addAll(calculateChildrenForGivenTransport(tokens, boatIndex, "uC"));
//			
//			if(cannibals >= 2)
//				results.addAll(calculateChildrenForGivenTransport(tokens, boatIndex, "uCC"));
//		}
		
		return results;
	}
	
	private List<INode> calculateChildrenForGivenTransport(String[] tokens, int boatIndex, String transport)
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
				String newNodesState = "";
				
				// create the state of the new node
				for(int j = 0; j < tokens.length; j++)
				{
					if(j == boatIndex)
						newNodesState += oldLandmass;
					else if(j != i)
						newNodesState += tokens[j];
					else
						newNodesState += sort(transport + tokens[j], j, tokens.length);
					newNodesState += "_";
				}
				results.add(new MissionariesAndCannibalsNode(newNodesState.substring(0, newNodesState.length() - 1), level + 1, this));
			}
		}
		
		return results;
	}
	
	private String sort(String str, int index, int maxIndex) // sorts the string so that 'u' comes before 'M' which comes before 'C', unless this is the leftmost landmass, in which case 'u' comes last
	{
		String result = "u";
		int missionaries = 0;
		int cannibals = 0;
		for(int i = 0; i < str.length(); i++)
		{
			if(str.charAt(i) == 'M')
				missionaries++;
			else if(str.charAt(i) == 'C')
				cannibals++;
		}
		
		for(int i = 0; i < missionaries; i++)
			result += 'M';
		for(int i = 0; i < cannibals; i++)
			result += 'C';
		
		if(index < maxIndex / 2)
			result = result.substring(1) + "u";
		
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
		String[] tokens = state.split("_");
		
		// if missionaries are dead, then this is not the solution
		if(isLoss())
			return false;
		
		// if not everyone is on the near side of the river, then this is not the solution
		for(int i = 1; i < tokens.length; i++) // note that we are skipping the first one
		{
			if(!tokens[i].equals(""))
				return false;
		}
		
		return true;
	}

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
	public int hashCode()
	{	
		return state.hashCode();
	}
	
	@Override
	public boolean equals(Object otherNode)
	{	
		return state.equals(otherNode.toString());
	}
}
