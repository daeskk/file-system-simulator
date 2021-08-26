package command;

import java.io.IOException;

import cruzapi.Disk;
import cruzapi.Inode;
import cruzapi.Main;

public class Cd extends Command
{
	public Cd(String name)
	{
		super(name);
	}
	
	@Override
	public void execute(String[] args)
	{
//		if(args.length == 1)
//		{
//			String dir = args[0];
//			
//			Disk disk = Main.getDisk();
//			
//			Inode current = disk.getCurrentInode();
//			
//			if(dir.equals(".."))
//			{
////				try
////				{
//////					Inode previous = new Inode(current.previous());
//////					previous.readFully();
//////					disk.setCurrentInode(previous);
////				}
////				catch(IOException e)
////				{
////					e.printStackTrace();
////				}
//			}
//			else
//			{
//				for(int pointer : current.pointer())
//				{
//					Inode next = new Inode(pointer);
//					
//					try
//					{
//						next.readName();
//						
//						if(next.getBeautifulName().equalsIgnoreCase(dir))
//						{
//							next.readFully();
//							disk.setCurrentInode(next);
//							return;
//						}
//					}
//					catch(IOException e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				System.out.println(String.format("Directory \"%s\" not found.", dir));
//			}
//		}
//		else
//		{
//			System.out.println("Wrong syntax! Try: cd <dir> | cd ..");
//		}
	}
}