package command;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import cruzapi.Block;
import cruzapi.DirEntry;
import cruzapi.Disk;
import cruzapi.FileException;
import cruzapi.Inode;
import cruzapi.Main;

public class Rm extends Command
{
	public Rm(String name)
	{
		super(name);
	}
	
	@Override
	public void execute(String[] args) throws IOException
	{
		Disk disk = Main.getDisk();
		
		if(args.length == 1)
		{
			try
			{
				rm(disk, args[0]);
			}
			catch(FileException e)
			{
				System.out.print(e.getMessage());
			}
		}
		else
		{
			System.out.println("Wrong syntax! Try: mkdir <name>");
		}
	}
	
	public static void rm(Disk disk, String path) throws IOException, FileException
	{
		Inode arg0 = path.startsWith("/") ? new Inode(1, true) : disk.getCurrentInode();
		
		String[] path0 = Arrays.stream(path.split("/")).filter(x -> !x.isEmpty()).toArray(String[]::new);
		
		DirEntry entry0 = disk.getEntryByPath(new Block(arg0.pointer()[0], true).getEntry(0), path0, 0);
		
		if(entry0 == null)
		{
			throw new FileException(String.format("bash: rm: %s: No such file or directory%n", path), 1000);
		}
		
		Inode previous0 = new Inode(new Block(new Inode(entry0.getIndex(), true).pointer()[0], true).getEntry(1).getIndex(), true);
		
		for(int i = 0; i < previous0.pointer().length; i++)
		{
			Block b = new Block(previous0.pointer()[i]);
			
			if(b.index() == 0)
			{
				continue;
			}
			
			b.readFully();
			
			List<DirEntry> list = b.getEntries();
			
			for(int j = i == 0 ? 2 : 0; j < list.size(); j++)
			{
				DirEntry entry = list.get(j);
				
				if(entry0.getName().equalsIgnoreCase(entry.getName()))
				{
					Inode target = new Inode(entry.getIndex(), true);
					
					for(int k = 0; k < target.pointer().length; k++)
					{
						Block b1 = new Block(target.pointer()[k]);
						
						if(b1.index() == 0)
						{
							continue;
						}
						
						b1.readFully();
						
						if(b1.getEntries(k == 0 ? 2 : 0).size() > 0)
						{
							throw new FileException(String.format("rm: failed to remove '%s': Directory not empty%n", path), 1001);
						}
					}
					
					Block b1 = new Block(target.pointer()[0]);
					
					b1.rw();
					b1.setInUse(false);
					
					target.clear();
					target.rw();
					target.setInUse(false);
					
					b.setEntry(j, new DirEntry(0));
					
					if(b.isClear())
					{
						b.setInUse(false);
						previous0.pointer()[i] = 0;
						previous0.rw();
					}
					
					b.rw();
					disk.setCurrentInode(previous0);
					return;
				}
			}
		}
		
		throw new FileException(String.format("bash: rm: %s: No such file or directory%n", path), 1000);
	}
}