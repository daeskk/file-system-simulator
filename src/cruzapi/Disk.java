package cruzapi;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Disk
{
	private final File file;
	
	private Inode inode;
	private FileOutputStream fout;
	
	public Disk()
	{
		file = new File(System.getProperty("user.home") + "/Desktop", "disco.dsc");;
	}
	
	public boolean create()
	{
		try
		{
			fout = new FileOutputStream(file);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			
			
			SuperBlock sb = new SuperBlock();
			
			dos.writeInt(sb.getMagicNumber());
			dos.writeInt(sb.getBlocks());
			dos.writeInt(sb.getBlockSize());
			
			fout.write(bos.toByteArray());
			
			
			byte[] bitmap = new byte[sb.getBlocks() / 8];
			
			for(int i = 0; i < bitmap.length; i++)
			{
				bitmap[i] = Byte.MAX_VALUE;
			}
			
			fout.write(bitmap);
			
			
			bos.reset();
			
			for(int i = 0; i < sb.getInodes(); i++)
			{
				Inode inode = new Inode();
				
				if(i == 0)
				{
					this.inode = inode;
				}
				
				dos.writeInt(inode.previous());
				dos.writeChars(inode.getName());
				
				for(int j : inode.pointer())
				{
					dos.writeInt(j);
				}
			}
			
			fout.write(bos.toByteArray());
			
			
//			
//			for(int i = 0; i < sb.getBlocks(); i++)
//			{
//				fout.write(new byte[4096]);
//			}
			
			
			
			System.out.println("a");
			
			getSuperBlock();
			getBitmap();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean[] getBitmap() throws IOException
	{
		FileInputStream fin = new FileInputStream(file);
		DataInputStream din = new DataInputStream(fin);
		
		din.skipBytes(12);
		byte[] data = din.readAllBytes();
		
		boolean[] bitmap = new boolean[data.length * 8];
		
		int n = 0;
		
		for(byte b : data)
		{
			int i = b + 128;
			
			for(int j = 7; j >= 0; j--)
			{
				bitmap[n + j] = i % 2 == 1;
				i /= 2;
				n++;
			}
		}
		
		fin.close();
		
		return null;
	}
	
	public SuperBlock getSuperBlock() throws IOException
	{
		FileInputStream fin = new FileInputStream(file);
		DataInputStream din = new DataInputStream(fin);
		
		final int magicNumber = din.readInt();
		final int blocks = din.readInt();
		final int blockSize = din.readInt();
		fin.close();
		
		return new SuperBlock(magicNumber, blocks, blockSize);
	}
	
	
	
	
	public void mkdir(String dir)
	{
		try
		{
			RandomAccessFile access = new RandomAccessFile(file, "rw");
			access.seek(2L);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}