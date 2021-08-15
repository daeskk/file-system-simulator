package cruzapi;

public class Inode
{
	public static final int INODE_SIZE = 104;
	
	private final int index;
	private int previous;
	private final char[] name = new char[26];
	private final int[] pointer = new int[12];
	
	public Inode(int index)
	{
		this.index = index;
	}
	
	public int index()
	{
		return index;
	}
	
	public void previous(int index)
	{
		previous = index;
	}
	
	public int previous()
	{
		return previous;
	}
	
	public char[] name()
	{
		return name;
	}
	
	public String getName()
	{
		return String.valueOf(name);
	}
	
	public int[] pointer()
	{
		return pointer;
	}
	
	public boolean isInUse()
	{
		return previous != 0;
	}
}
