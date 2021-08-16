package command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		if(args.length == 0)
		{
			Disk disk = Main.getDisk();
			Inode current = disk.getCurrentInode();
			
			List<String> list = new ArrayList<>();
			
			while(current.index() != 1)
			{
				try
				{
					list.add(current.getBeautifulName());
					Inode previous = new Inode(current.previous());
					previous.readFully();
					current = previous;
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
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
			System.out.println("Wrong syntax! Try: mkdir <name>");
		}
	}
}