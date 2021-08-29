package command;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cruzapi.Block;
import cruzapi.DirEntry;
import cruzapi.Disk;
import cruzapi.FileException;
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
				DirEntry entry2 = entry1;
				
				boolean rename = false;
				
				if(entry0 == null)
				{
					System.out.printf("mv: cannot stat '%s': No such file or directory%n", args[0]);
					return;
				}
				
				if(entry1 == null)
				{
					rename = true;
					entry1 = disk.getEntryByPath(new Block(arg1.pointer()[0], true).getEntry(0), Arrays.copyOf(path1, path1.length - 1), 0);
				}
				
				if(entry1 == null)
				{
					System.out.printf("mv: cannot move '%s' to '%s': No such file or directory%n", args[0], args[1]);
					return;
				}

				
				try
				{
					Rm.rm(disk, path1[path1.length - 1] + "/" + args[0]);
				}
				catch(FileException e)
				{
					if(e.getErrorCode() == 1001)
					{
						System.out.print(e.getMessage());
						return;
					}
				}
				catch(ArrayIndexOutOfBoundsException ex)
				{
					System.out.println("Permission denied.");
					return;
				}
				
				Inode previous0 = new Inode(new Block(new Inode(entry0.getIndex(), true).pointer()[0], true).getEntry(1).getIndex(), true);
				
				for(int k = 0; k < previous0.pointer().length; k++)
				{
					Block b1 = new Block(previous0.pointer()[k]);
					
					if(b1.index() == 0)
					{
						continue;
					}
					
					b1.readFully();
					
					List<DirEntry> list = b1.getEntries();
					
					for(int j = k == 0 ? 2 : 0; j < list.size(); j++)
					{
						DirEntry entry = list.get(j);
						
						if(entry0.getName().equalsIgnoreCase(entry.getName()))
						{
							b1.setEntry(j, new DirEntry(0));
							b1.rw();
							
							if(b1.isClear())
							{
								previous0.pointer()[k] = 0;
								previous0.rw();
								b1.setInUse(false);
							}
							
							break;
						}
					}
				}
				
				Block dd = new Block(new Inode(entry0.getIndex(), true).pointer()[0], true);
				
				if(rename)
				{
					dd.setEntry(0, new DirEntry(entry0.getIndex(), path1[path1.length - 1]));
				}
				
				dd.setEntry(1, entry1);
				
				Inode inode1 = new Inode(entry1.getIndex(), true);
				
				Integer[] pointer = Arrays.stream(inode1.pointer()).boxed().toArray(Integer[]::new);
				Arrays.sort(pointer, Collections.reverseOrder());
				
				for(int i = 0; i < pointer.length; i++)
				{
					Block b = new Block(inode1.pointer()[i]);
					
					if(b.index() == 0)
					{
						b = disk.getEmptyBlock();
						
						if(b == null)
						{
							System.out.println("No such block available.");
							return;
						}
						
						inode1.addPointer(b.index());
					}
					
					b.readFully();
					
					if(b.addEntry(new DirEntry(entry0.getIndex(), rename ? path1[path1.length - 1] : dd.getEntry(0).getName())))
					{
						b.setInUse(true);
						b.rw();
						break;
					}
				}
				
				inode1.rw();
				
				dd.setInUse(true);
				dd.rw();
			}
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
}