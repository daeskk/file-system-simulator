package cruzapi;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import cruzapi.Disk.BitmapType;

public class Inode
{
	public enum Type
	{
		FILE((byte) 1), DIR((byte) 0);
		
		private byte value;
		
		private Type(byte value)
		{
			this.value = value;
		}
	}
	
	public static final int INODE_SIZE = 49;
	
	private final int index;
	private byte type;
	private final int[] pointer = new int[12];
	
	public Inode(int index, boolean readFully) throws IOException
	{
		this(index);
		
		if(readFully)
		{
			readFully();
		}
	}
	
	public Inode(int index)
	{
		this.index = index;
	}
	
	public int index()
	{
		return index;
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
	
	public void readFully() throws IOException
	{
		Disk disk = Main.getDisk();
		SuperBlock sb = disk.getSuperBlock();
		
		try(RandomAccessFile access = new RandomAccessFile(disk.getFile(), "rw");)
		{
			access.skipBytes(sb.getSize() + sb.getInodeBitmapSize() + sb.getBlockBitmapSize() + (index - 1) * INODE_SIZE);
			type = access.readByte();
			
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
	
	
	public void setInUse(boolean value) throws IOException
	{
		Main.getDisk().rwBitmap(BitmapType.INODE, index - 1, value);
	}
	
	public boolean isInUse() throws IOException
	{
		return Main.getDisk().getBitmap(BitmapType.INODE)[index - 1];
	}
	
	public void rw() throws IOException
	{
		Disk disk = Main.getDisk();
		SuperBlock sb = disk.getSuperBlock();
		
		try(RandomAccessFile access = new RandomAccessFile(disk.getFile(), "rw");)
		{
			access.seek(sb.getSize() + sb.getInodeBitmapSize() + sb.getBlockBitmapSize() + (long) (index - 1) * INODE_SIZE);
			access.writeByte(type);
			
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
	
	public void clear()
	{
		type = 0;

		Arrays.fill(pointer, 0);
	}
	
	public Type getType()
	{
		return type == 0 ? Type.DIR : Type.FILE;
	}
	
	@Override
	public String toString()
	{
		return "Inode [index=" + index + ", pointer=" + Arrays.toString(pointer) + "]";
	}
	
	public void setType(Type type)
	{
		this.type = type.value;
	}
}
