package cruzapi;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class Block implements Serializable
{
	private byte[] data;
	
	public Block(int size)
	{
		data = new byte[size];
		
		for(int i = 0; i < data.length; i++)
		{
			data[i] = '0';
		}
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	public int getSize()
	{
		return data.length;
	}
}