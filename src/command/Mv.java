package command;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
				
				String[] path0 = Arrays.stream(args[0].split("/")).filter(x -> !x.isEmpty()).toArray(String[]::new);
				String[] path1 = Arrays.stream(args[1].split("/")).filter(x -> !x.isEmpty()).toArray(String[]::new);
				
				DirEntry entry0 = disk.getEntryByPath(new Block(arg0.pointer()[0], true).getEntry(0), path0, 0);
				DirEntry entry1 = disk.getEntryByPath(new Block(arg1.pointer()[0], true).getEntry(0), path1, 0);
				
				if(entry0 == null)
				{
					System.out.printf("mv: cannot stat '%s': No such file or directory%n", args[0]);
					return;
				}
				
				if(entry1 == null)
				{
					entry1 = disk.getEntryByPath(new Block(arg1.pointer()[0], true).getEntry(0), Arrays.copyOf(path1, path1.length - 1), 0);
				}
				
				if(entry1 == null)
				{
					System.out.printf("mv: cannot move '%s' to '%s': No such file or directory", args[0], args[1]);
					return;
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
							b.setEntry(j, new DirEntry(0));
							
							if(b.isClear())
							{
								previous0.pointer()[i] = 0;
								previous0.rw();
								b.rw();
								b.setInUse(false);
							}
							
							return;
						}
					}
				}
				
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
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
}