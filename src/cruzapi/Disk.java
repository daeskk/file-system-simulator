package cruzapi;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Disk
{
	public enum BitmapType
	{
		INODE, BLOCK;
		
		public int getPosition() throws IOException
		{
			switch(this)
			{
			case BLOCK:
				return Main.getDisk().getSuperBlock().getBlockBitmapPosition();
			case INODE:
				return Main.getDisk().getSuperBlock().getInodeBitmapPosition();
			}
			
			throw new IOException();
		}
		
		public int getSize() throws IOException
		{
			switch(this)
			{
			case BLOCK:
				return Main.getDisk().getSuperBlock().getBlockBitmapSize();
			case INODE:
				return Main.getDisk().getSuperBlock().getInodeBitmapSize();
			}
			
			throw new IOException();
		}
	}
	
	private final File file;
	
	private Inode currentInode;
	
	public Disk()
	{
		file = new File(System.getProperty("user.home") + "/Desktop", "disco.dsc");
	}
	
	public boolean create()
	{
		try(RandomAccessFile file = new RandomAccessFile(this.file, "rw"))
		{
			SuperBlock sb = new SuperBlock();
			
			file.writeInt(sb.getMagicNumber());
			file.writeInt(sb.getBlocks());
			file.writeInt(sb.getBlockSize());
			
			
			byte[] inodeBitmap = new byte[sb.getInodeBitmapSize()];
			
			for(int i = 0; i < inodeBitmap.length; i++)
			{
				inodeBitmap[i] = Byte.MIN_VALUE;
			}
			
			file.write(inodeBitmap);
			
			
			byte[] blockBitmap = new byte[sb.getBlockBitmapSize()];
			
			for(int i = 0; i < blockBitmap.length; i++)
			{
				blockBitmap[i] = Byte.MIN_VALUE;
			}
			
			file.write(blockBitmap);
			
			
			for(int i = 1; i <= sb.getInodes(); i++)
			{
				Inode inode = new Inode(i);
				inode.rw();
			}
			
			
			for(int i = 0; i < sb.getBlocks(); i++)
			{
				file.write(new byte[4096]);
			}
			
			
			currentInode = getEmptyInode();
			Block block = getEmptyBlock();
			
			currentInode.addPointer(block.index());
			block.addEntry(new DirEntry(currentInode.index(), "/"));
			block.addEntry(new DirEntry(currentInode.index(), "/"));
			
			currentInode.rw();
			block.rw();
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
	
	private Block getEmptyBlock() throws IOException
	{
		try(RandomAccessFile file = new RandomAccessFile(this.file, "rw"))
		{
			boolean[] bitmap = getBitmap(BitmapType.BLOCK);
			
			for(int i = 0; i < bitmap.length; i++)
			{
				if(!bitmap[i])
				{
					return new Block(i + 1);
				}
			}
			
			return null;
		}
		catch(IOException e)
		{
			throw e;
		}
	}

	public void rwBitmap(final BitmapType type, final int index, final boolean value) throws IOException
	{
		try(RandomAccessFile file = new RandomAccessFile(this.file, "rw"))
		{
			SuperBlock sb = getSuperBlock();
			
			file.seek(type.getPosition() + index / 8);
			
			final int bit = 7 - index % 8;
			
			int b1 = file.readByte() + 128;
			int b2 = Byte.MIN_VALUE;
			
			for(int j = 0; j < 8; j++)
			{
				b2 += bit == j ? value ? 1 << j : 0 : (b1 % 2) == 1 ? 1 << j : 0;
				b1 /= 2;
			}
			
			file.seek(sb.getSize() + index / 8);
			file.write(b2);
		}
		catch(IOException ex)
		{
			throw ex;
		}
	}
	
	public boolean[] getBitmap(BitmapType type) throws IOException
	{
		try(RandomAccessFile file = new RandomAccessFile(this.file, "rw"))
		{
			file.seek(type.getPosition());
			
			final boolean[] bitmap = new boolean[type.getSize() * 8];
			
			int n = 0;
			
			for(int i = 0; i < type.getSize(); i++)
			{
				int b = file.readByte() + 128;
				
				for(int j = 7; j >= 0; j--)
				{
					bitmap[n + j] = (b % 2) == 1;
					b /= 2;
				}
				
				n += 8;
			}
			
			return bitmap;
		}
		catch(IOException ex)
		{
			throw ex;
		}
	}
	
	public Inode getEmptyInode() throws IOException
	{
		try(RandomAccessFile file = new RandomAccessFile(this.file, "rw"))
		{
			boolean[] bitmap = getBitmap(BitmapType.INODE);
			
			for(int i = 0; i < bitmap.length; i++)
			{
				if(!bitmap[i])
				{
					return new Inode(i + 1);
				}
			}
			
			return null;
		}
		catch(IOException e)
		{
			throw e;
		}
	}
	
	public SuperBlock getSuperBlock() throws IOException
	{
		try(RandomAccessFile file = new RandomAccessFile(this.file, "rw"))
		{
			final int magicNumber = file.readInt();
			final int blocks = file.readInt();
			final int blockSize = file.readInt();
			
			return new SuperBlock(magicNumber, blocks, blockSize);
		}
		catch(IOException e)
		{
			throw e;
		}
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
	
	public boolean rm(Inode inode)
	{
		return true;
	}

	public void mkdir(String dir) throws IOException
	{
//		Inode target = getEmptyInode();
//		
//		if(target == null)
//		{
//			System.out.println("Inodes full.");
//			return;
//		}
//		
//		if(!currentInode.addPointer(target.index()))
//		{
//			System.out.println("Current inode's pointer is full.");
//			return;
//		}
//		
//		target.setName(dir);
//		target.previous(currentInode.index());
//		
//		System.out.println(target);
//		
//		currentInode.rw();
//		target.rw();
	}
}