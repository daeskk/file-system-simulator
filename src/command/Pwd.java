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
				System.out.println(pwd(Main.getDisk()));
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
	
	public static String pwd(Disk disk) throws IOException
	{
		Inode current = disk.getCurrentInode();
		
		List<String> list = new ArrayList<>();
		
		Block block = new Block(current.pointer()[0]);
		block.readFully();
		
		DirEntry d = block.getEntry(0);
		DirEntry dd = block.getEntry(1);
		
		while(d.getIndex() != 1)
		{
			list.add(d.getName());
			current = new Inode(dd.getIndex(), true);
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
		
		return pwd;
	}
}