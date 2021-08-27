package command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cruzapi.Block;
import cruzapi.DirEntry;
import cruzapi.Disk;
import cruzapi.Inode;
import cruzapi.Main;

public class Ls extends Command
{
	public Ls(String name)
	{
		super(name);
	}
	
	@Override
	public void execute(String[] args)
	{
		try
		{
			if(args.length == 0)
			{
				Disk disk = Main.getDisk();
				
				Inode current = disk.getCurrentInode();
				
				List<DirEntry> entries = new ArrayList<>();
				
				for(int i = 0; i < current.pointer().length; i++)
				{
					int index = current.pointer()[i];
					
					if(index == 0)
					{
						break;
					}
					
					Block b = new Block(index);
					
					b.readFully();
					
					entries.addAll(b.getEntries());
				}
				
				entries.remove(0);
				entries.remove(0);
				
				System.out.println(entries.stream().map(name -> name.getName()).collect(Collectors.toList()));
			}
			else
			{
				System.out.println("Wrong syntax! Try: ls");
			}
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
}