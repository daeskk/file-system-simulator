package cruzapi;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Disk
{
	private final File file;
	
	private Inode currentInode;
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
			
			for(int i = 1; i <= sb.getInodes(); i++)
			{
				Inode inode = new Inode(i);
				
				if(i == 1)
				{
					this.currentInode = inode;
					inode.previous(1);
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
			getEmptyInode();
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
		
		din.skipBytes(SuperBlock.SUPER_BLOCK_SIZE);
		
		byte[] data = din.readNBytes(getSuperBlock().getBitmapSize());
		boolean[] bitmap = new boolean[data.length * 8];
		
		int n = 0;
		
		for(byte b : data)
		{
			int i = b + 128;
			
			for(int j = 7; j >= 0; j--)
			{
				bitmap[n + j] = (i % 2) == 1;
				i /= 2;
			}
			
			n += 8;
		}
		
		fin.close();
				
		return null;
	}
	
	public Inode getEmptyInode() throws IOException
	{
		try(FileInputStream fin = new FileInputStream(file);
			DataInputStream din = new DataInputStream(fin))
		{
			SuperBlock sb = getSuperBlock();
			din.skipBytes(SuperBlock.SUPER_BLOCK_SIZE + sb.getBitmapSize());
			
			for(int index = 1; index <= sb.getInodes(); index++)
			{
				int previous = din.readInt();
				
				if(previous == 0)
				{
					return new Inode(index);
				}
				
				din.skipBytes(Inode.INODE_SIZE - 4);
			}
		}
		catch(IOException e)
		{
			throw e;
		}
		
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
	
	public File getFile()
	{
		return file;
	}
	
	public Inode getCurrentInode()
	{
		return currentInode;
	}
	
	public void setCurrentInode(Inode inode)
	{
		currentInode = inode;
	}
	
	public void mkdir(String dir) throws IOException
	{
		Inode target = getEmptyInode();
		
		if(target == null)
		{
			System.out.println("Inodes full.");
			return;
		}
		
		if(!currentInode.addPointer(target.index()))
		{
			System.out.println("Current inode's pointer is full.");
			return;
		}
		
		target.setName(dir);
		target.previous(currentInode.index());
		
		System.out.println(target);
		
		currentInode.rw();
		target.rw();
	}
}