package command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cruzapi.Block;
import cruzapi.DirEntry;
import cruzapi.Disk;
import cruzapi.Inode;
import cruzapi.Main;

public class Pwd extends Command
{
	public Pwd(String name)
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
				
				List<String> list = new ArrayList<>();
				
				Block block = new Block(current.pointer()[0]);
				block.readFully();
				
				DirEntry d = block.getEntry(0);
				DirEntry dd = block.getEntry(1);
				
				while(dd.getIndex() != 1)
				{
					list.add(d.getName());
					Inode previous = new Inode(dd.getIndex());
					previous.readFully();
					current = previous;
					block = new Block(current.pointer()[0]);
					block.readFully();
					d = block.getEntry(0);
					dd = block.getEntry(1);
				}
				
				Collections.reverse(list);
				
				String pwd = "";
				
				for(String dir : list)
				{
					pwd += "/" + dir;
				}
				
				if(pwd.isEmpty())
				{
					pwd = "/";
				}
				
				System.out.println(pwd);
			}
			else
			{
				System.out.println("Wrong syntax! Try: pwd");
			}
			
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
}