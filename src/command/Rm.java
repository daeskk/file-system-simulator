package command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cruzapi.Block;
import cruzapi.DirEntry;
import cruzapi.Disk;
import cruzapi.Inode;
import cruzapi.Main;

public class Rm extends Command
{
	public Rm(String name)
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
				String dir = args[0];
				
				Disk disk = Main.getDisk();
				
				Inode current = disk.getCurrentInode();
				
				for(int i = 0; i < current.pointer().length; i++)
				{
					Block b = new Block(current.pointer()[i]);
					
					if(b.index() == 0)
					{
						continue;
					}
					
					b.readFully();
					
					List<DirEntry> list = b.getEntries();
					
					for(int j = i == 0 ? 2 : 0; j < list.size(); j++)
					{
						DirEntry entry = list.get(j);
						
						if(dir.equalsIgnoreCase(entry.getName()))
						{
							Inode target = new Inode(entry.getIndex());
							target.readFully();
							
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
									System.out.println("Directory isn't empty.");
									return;
								}
								
							}
							
							Block b1 = new Block(target.pointer()[0]);
							
							b1.rw();
							b1.setInUse(false);
							
							target.clear();
							target.rw();
							target.setInUse(false);
							
							b.setEntry(j, new DirEntry(0));
							b.rw();
							
							System.out.println(String.format("Directory \"%s\" removed.", dir));
							return;
						}
					}
				}
				
				System.out.println(String.format("Directory \"%s\" not found.", dir));
			}
			else
			{
				System.out.println("Wrong syntax! Try: mkdir <name>");
			}
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
}