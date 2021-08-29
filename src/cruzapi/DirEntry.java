package cruzapi;

public class DirEntry
{
	private int index;
	public final char[] name = new char[14];
	
	public DirEntry(int index)
	{
		this.index = index;
	}
	
	public DirEntry(int index, String name)
	{
		this.index = index;
		
		for(int i = 0; i < this.name.length && i < name.length(); i++)
		{
			this.name[i] = name.charAt(i);
		}
	}
	
	public void setName(String name)
	{
		for(int i = 0; i < this.name.length && i < name.length(); i++)
		{
			this.name[i] = name.charAt(i);
		}
	}
	
	public String getName()
	{
		return String.valueOf(name).trim();
	}
	
	public int getIndex()
	{
		return index;
	}
	
	@Override
	public String toString()
	{
		return "[index=" + index + ", name=" + getName() + "]";
	}
}