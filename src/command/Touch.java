package command;

import java.io.IOException;
import java.util.Arrays;

import cruzapi.Block;
import cruzapi.DirEntry;
import cruzapi.Disk;
import cruzapi.Inode;
import cruzapi.Main;
import cruzapi.Inode.Type;

public class Touch extends Command
{
	public Touch(String name)
	{
		super(name);
	}
	
	@Override
	public void execute(String[] args)
	{
		try
		{
			if(args.length == 1)
			{
				Disk disk = Main.getDisk();
				
				Inode arg0 = args[0].startsWith("/") ? new Inode(1, true) : disk.getCurrentInode();
				
				DirEntry entry0 = disk.getEntryByPath(new Block(arg0.pointer()[0], true).getEntry(0), Arrays.stream(args[0].split("/")).filter(x -> !x.isEmpty()).toArray(String[]::new), 0);
				
				if(entry0 == null)
				{
					System.out.println("Directory not found.");
					return;
				}
				
				Inode inode1 = new Inode(entry0.getIndex(), true);
				
				Inode newInode = disk.getEmptyInode();
				
				if(newInode == null)
				{
					System.out.println("No empty inode available.");
					return;
				}
				
				boolean inodeFull = true;
				
				for(int i = 0; i < inode1.pointer().length; i++)
				{
					Block b = new Block(inode1.pointer()[i]);
					
					if(b.index() == 0)
					{
						b = disk.getEmptyBlock();
						
						if(b == null)
						{
							inodeFull = false;
							continue;
						}
					}
					else
					{
						b.readFully();
					}
					
					if(b.addEntry(entry0))
					{
						newInode.setInUse(true);
						newInode.setType(Type.FILE);
						newInode.rw();
						inode1.pointer()[i] = b.index();
						inode1.rw();
						b.rw();
						b.setInUse(true);
						System.out.println(String.format("File \"%s\" created.", args[0]));
						return;
					}
				}
				
				if(inodeFull)
				{
					System.out.println("Inode is full.");
				}
				else
				{
					System.out.println("No empty block available.");
				}
			}
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
}