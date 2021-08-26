package cruzapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import cruzapi.Disk.BitmapType;

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
	
	public void setInUse(boolean value) throws IOException
	{
		Main.getDisk().rwBitmap(BitmapType.BLOCK, index - 1, value);
	}
	
	public boolean isInUse() throws IOException
	{
		return Main.getDisk().getBitmap(BitmapType.BLOCK)[index - 1];
	}
	
	public void addEntry(DirEntry entry) throws IOException
	{
		try(ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout))
		{
			dout.writeInt(entry.getIndex());
			
			for(char c : entry.name)
			{
				dout.writeChar(c);
			}
			
			byte[] data = bout.toByteArray();
			
			for(int i = getEmptySlot(), j = 0; j < data.length; i++, j++)
			{
				this.data[i] = data[j];
			}
			
			getEmptySlot();
		}
		catch(IOException ex)
		{
			throw ex;
		}
	}
	
	public DirEntry getEntry(int index) throws IOException
	{
		try(ByteArrayInputStream bin = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(bin);)
		{
			din.skipBytes(index * 32);
			
			index = din.readInt();
			
			if(index == 0)
			{
				return null;
			}
			
			DirEntry entry = new DirEntry(index);
			
			for(int i = 0; i < entry.name.length; i++)
			{
				entry.name[i] = din.readChar();
			}
			
			return entry;
		}
		catch(IOException ex)
		{
			throw ex;
		}
	}
	
	
	public List<DirEntry> getEntries() throws IOException
	{
		try(ByteArrayInputStream bin = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(bin);)
		{
			for(int i = 0; i < getSize(); i += 32)
			{
				din.readInt();
				din.skipBytes(28);
			}
			
			return null;
		}
		catch(IOException ex)
		{
			throw ex;
		}
	}
	
	public int getEmptySlot() throws IOException
	{
		try(ByteArrayInputStream bin = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(bin);)
		{
			for(int i = 0; i < getSize(); i += 32)
			{
				if(din.readInt() == 0)
				{
					return i;
				}
				
				din.skipBytes(28);
			}
			
			return -1;
		}
		catch(IOException ex)
		{
			throw ex;
		}
	}
	
	public void readFully() throws IOException
	{
		Disk disk = Main.getDisk();
		SuperBlock sb = disk.getSuperBlock();
		
		try(RandomAccessFile access = new RandomAccessFile(disk.getFile(), "rw");)
		{
			access.skipBytes(sb.getSize() + sb.getInodeBitmapSize() + sb.getBlockBitmapSize() + sb.getInodesSize() + (index - 1) * sb.getBlockSize());
			
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
	
	public void rw() throws IOException
	{
		Disk disk = Main.getDisk();
		
		try(RandomAccessFile file = new RandomAccessFile(disk.getFile(), "rw"))
		{
			SuperBlock sb = disk.getSuperBlock();
			
			file.seek(sb.getSize() + sb.getInodeBitmapSize() + sb.getBlockBitmapSize() + sb.getInodesSize() + (index - 1) * getSize());
			file.write(data);
		}
		catch(IOException ex)
		{
			throw ex;
		}
	}
}