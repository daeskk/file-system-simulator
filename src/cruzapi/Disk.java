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
			
			
			byte[] inodeBitmap = new byte[sb.getInodeBitmapSize()];
			
			for(int i = 0; i < inodeBitmap.length; i++)
			{
				inodeBitmap[i] = Byte.MIN_VALUE;
			}
			
			fout.write(inodeBitmap);
			
			
			byte[] blockBitmap = new byte[sb.getBlockBitmapSize()];
			
			for(int i = 0; i < blockBitmap.length; i++)
			{
				blockBitmap[i] = Byte.MIN_VALUE;
			}
			
			fout.write(blockBitmap);
			
			
			bos.reset();
			
			for(int i = 1; i <= sb.getInodes(); i++)
			{
				Inode inode = new Inode(i);
				
//				if(i == 1)
//				{
//					this.currentInode = inode;
//					inode.previous(1);
//				}
//				
//				dos.writeInt(inode.previous());
//				dos.writeChars(inode.getName());
//				
//				for(int j : inode.pointer())
//				{
//					dos.writeInt(j);
//				}
			}
			
			fout.write(bos.toByteArray());
			
			
			
			for(int i = 0; i < sb.getBlocks(); i++)
			{
				fout.write(new byte[4096]);
			}
			
			
			rwInodeBitmap(0, true);
			rwInodeBitmap(1, true);
			rwInodeBitmap(5, true);
			rwInodeBitmap(6, true);
			rwInodeBitmap(7, false);
			rwInodeBitmap(8, false);
			
			for(boolean bool : getInodeBitmap())
			{
				System.out.println(bool);
			}
			
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
	
	public void rwInodeBitmap(final int index, final boolean value) throws IOException
	{
		try(RandomAccessFile file = new RandomAccessFile(this.file, "rw"))
		{
			SuperBlock sb = getSuperBlock();
			
			file.seek(sb.getSize() + index / 8);
			
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
	
	public boolean[] getInodeBitmap() throws IOException
	{
		try(RandomAccessFile file = new RandomAccessFile(this.file, "rw"))
		{
			SuperBlock sb = getSuperBlock();
			
			file.skipBytes(sb.getSize());
			
			final int bitmapSize = sb.getInodeBitmapSize();
			final boolean[] bitmap = new boolean[bitmapSize * 8];
			
			int n = 0;
			
			for(int i = 0; i < bitmapSize; i++)
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
	
	public boolean[] getBitmap() throws IOException
	{
		FileInputStream fin = new FileInputStream(file);
		DataInputStream din = new DataInputStream(fin);
		
		din.skipBytes(SuperBlock.SUPER_BLOCK_SIZE);
		
		byte[] data = din.readNBytes(getSuperBlock().getBlockBitmapSize());
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
			din.skipBytes(SuperBlock.SUPER_BLOCK_SIZE + sb.getBlockBitmapSize());
			
			for(int index = 1; index <= sb.getInodes(); index++)
			{
				int previous = din.readInt();
				
				if(previous == 0)
				{
					return new Inode(index);
				}
				
				din.skipBytes(Inode.INODE_SIZE - 4);
			}
			
			return null;
		}
		catch(IOException e)
		{
			throw e;
		}
	}
	
	public void writeInodeBitmap()
	{
		
	}
	
	public SuperBlock getSuperBlock() throws IOException
	{
		try(FileInputStream fin = new FileInputStream(file);
			DataInputStream din = new DataInputStream(fin))
		{
			final int magicNumber = din.readInt();
			final int blocks = din.readInt();
			final int blockSize = din.readInt();
			
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