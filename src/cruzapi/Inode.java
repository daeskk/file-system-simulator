package cruzapi;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Inode
{
	public static final int INODE_SIZE = 105;
	
	private final int index;
	private byte type;
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
	
	public void setName(String name)
	{
		char[] arr = name.toCharArray();
		
		for(int i = 0; i < arr.length; i++)
		{
			this.name[i] = arr[i];
		}
	}
	
	public String getName()
	{
		return String.valueOf(name);
	}
	
	public int[] pointer()
	{
		return pointer;
	}
	
	public boolean addPointer(int index)
	{
		for(int i = 0; i < pointer.length; i++)
		{
			if(pointer[i] == 0)
			{
				pointer[i] = index;
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isInUse()
	{
		return previous != 0;
	}
	
	public void readName() throws IOException
	{
		Disk disk = Main.getDisk();
		SuperBlock sb = disk.getSuperBlock();
		
		try(RandomAccessFile access = new RandomAccessFile(disk.getFile(), "rw");)
		{
			access.skipBytes(sb.getSize() + sb.getBitmapSize() + (index - 1) * INODE_SIZE + 4);
			
			for(int i = 0; i < name.length; i++)
			{
				name[i] = access.readChar();
			}
		}
		catch(IOException e)
		{
			throw e;
		}
	}
	
	public void readFully() throws IOException
	{
		Disk disk = Main.getDisk();
		SuperBlock sb = disk.getSuperBlock();
		
		try(RandomAccessFile access = new RandomAccessFile(disk.getFile(), "rw");)
		{
			access.skipBytes(sb.getSize() + sb.getBitmapSize() + (index - 1) * INODE_SIZE);
			previous = access.readInt();
			type = access.readByte();
			
			for(int i = 0; i < name.length; i++)
			{
				name[i] = access.readChar();
			}
			
			for(int i = 0; i < pointer.length; i++)
			{
				pointer[i] = access.readInt();
			}
		}
		catch(IOException e)
		{
			throw e;
		}
	}
	
	public void rw() throws IOException
	{
		Disk disk = Main.getDisk();
		SuperBlock sb = disk.getSuperBlock();
		
		try(RandomAccessFile access = new RandomAccessFile(disk.getFile(), "rw");)
		{
			access.skipBytes(sb.getSize() + sb.getBitmapSize() + (index - 1) * INODE_SIZE);
			access.writeInt(previous);
			access.write(type);
			access.writeChars(getName());
			
			for(int j : pointer)
			{
				access.writeInt(j);
			}
		}
		catch(IOException e)
		{
			throw e;
		}
	}
	
	public String getBeautifulName()
	{
		return getName().trim();
	}

	@Override
	public String toString()
	{
		return "Inode [index=" + index + ", previous=" + previous + ", name=" + getBeautifulName() + ", pointer="
				+ Arrays.toString(pointer) + "]";
	}
}