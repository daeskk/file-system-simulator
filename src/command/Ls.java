package command;

import java.io.IOException;

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
		if(args.length == 0)
		{
			Disk disk = Main.getDisk();
			
			Inode current = disk.getCurrentInode();
			
			for(int pointer : current.pointer())
			{
				if(pointer <= 0)
				{
					continue;
				}
				
				Inode inode = new Inode(pointer);
				
				try
				{
					inode.readName();
					System.out.print(inode.getBeautifulName() + " ");
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				
			}
			
			System.out.println();
		}
		else
		{
			System.out.println("Wrong syntax! Try: ls");
		}
	}
}