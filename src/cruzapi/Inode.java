package cruzapi;

public class Inode
{
	public static final int INODE_SIZE = 104;
	
	private int previous;
	private final char[] name = new char[26];
	private final int[] pointer = new int[12];
	
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
}
