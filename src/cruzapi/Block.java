package cruzapi;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Block
{
	private final int index;
	private byte[] data = new byte[10 << 12];
	
	public Block(int index)
	{
		this.index = index;
	}
	
	public int index()
	{
		return index;
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	public int getSize()
	{
		return data.length;
	}
	
	public void readFully() throws IOException
	{
		Disk disk = Main.getDisk();
		SuperBlock sb = disk.getSuperBlock();
		
		try(RandomAccessFile access = new RandomAccessFile(disk.getFile(), "rw");)
		{
			access.skipBytes(sb.getSize() + sb.getBlockBitmapSize() + sb.getInodesSize() + (index - 1) * sb.getBlockSize());
			
			for(int i = 0; i < data.length; i++)
			{
				data[i] = access.readByte();
			}
		}
		catch(IOException e)
		{
			throw e;
		}
	}
}