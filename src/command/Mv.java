package command;

import java.io.IOException;

import cruzapi.Block;
import cruzapi.DirEntry;
import cruzapi.Disk;
import cruzapi.Inode;
import cruzapi.Main;

public class Mv extends Command
{
	public Mv(String name)
	{
		super(name);
	}
	
	@Override
	public void execute(String[] args)
	{
		try
		{
			if(args.length == 2)
			{
				Disk disk = Main.getDisk();
				
				Inode arg0 = args[0].startsWith("/") ? new Inode(1, true) : disk.getCurrentInode();
				Inode arg1 = args[1].startsWith("/") ? new Inode(1, true) : disk.getCurrentInode();
				
				DirEntry entry0 = get(new Block(arg0.pointer()[0], true).getEntry(0), args[0].split("/"), 0);
				DirEntry entry1 = get(new Block(arg1.pointer()[0], true).getEntry(0), args[1].split("/"), 0);
				
				Inode inode1 = new Inode(entry1.getIndex(), true);
				
				for(int i = 0; i < inode1.pointer().length; i++)
				{
					Block b = new Block(inode1.pointer()[i]);
					
					if(b.index() == 0)
					{
						b = disk.getEmptyBlock();
					}
					
					b.readFully();
					
					b.addEntry(entry0);
				}
			}
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public DirEntry get(DirEntry entry, String[] path, int i) throws IOException
	{
		if(i == path.length)
		{
			return entry;
		}
		
		Inode inode = new Inode(entry.getIndex(), true);
		
		for(int j = 0; j < inode.pointer().length; j++)
		{
			Block b = new Block(inode.pointer()[j]);
			
			if(b.index() == 0)
			{
				continue;
			}
			
			for(DirEntry entries : b.getEntries(j == 0 ? 2 : 0))
			{
				if(entries.getName().equalsIgnoreCase(path[i]))
				{
					return get(entries, path, i++);
				}
			}
		}
		
		return null;
	}
}

//				Inode inode1 = new Inode(entry1.getIndex(), true);
//
//				for(int i = 0; i < inode1.pointer().length; i++)
//				{
//					Block b = new Block(inode1.pointer()[i]);
//
//					if(b.index() == 0)
//					{
//						b = disk.getEmptyBlock();
//					}
//					else
//					{
//						b.readFully();
//					}
//
//					if(b.addEntry(entry0))
//					{
//						inode1.pointer()[i] = b.index();
//						inode1.rw();
//						b.rw();
//						b.setInUse(true);
//						System.out.println(String.format("File \"%s\" copied to directory \"%s\".", args[0], args[1]));
//						return;
//					}
//				}
//
//				System.out.println("Inode is full.");