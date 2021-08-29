package command;

import java.io.IOException;
import java.util.Arrays;

import cruzapi.*;
import cruzapi.Inode.Type;

public class Cd extends Command
{
    public Cd(String name)
    {
        super(name);
    }

    @Override
    public void execute(String[] args) throws IOException
    {
    	Disk disk = Main.getDisk();
		
    	if(args.length == 0)
		{
			Inode root = new Inode(1, true);
			disk.setCurrentInode(root);
		}
		else if(args.length == 1)
		{
			String dir = args[0];
			
			Inode current = disk.getCurrentInode();
			
			if(dir.equals("."))
			{
				return;
			}
			else if(dir.equals(".."))
			{
				Block block = new Block(current.pointer()[0], true);
				
				disk.setCurrentInode(new Inode(block.getEntry(1).getIndex(), true));
			}
			else
			{
				Inode arg0 = args[0].startsWith("/") ? new Inode(1, true) : disk.getCurrentInode();
				
				DirEntry entry0 = disk.getEntryByPath(new Block(arg0.pointer()[0], true).getEntry(0), Arrays.stream(args[0].split("/")).filter(x -> !x.isEmpty()).toArray(String[]::new), 0);
				
				if(entry0 == null)
				{
					System.out.printf("bash: cd: %s: No such file or directory%n", dir);
				}
				else
				{
					current = new Inode(entry0.getIndex(), true);
					
					if(current.getType() == Type.FILE)
					{
						System.out.printf("bash: cd: %s: Not a directory%n", dir);
					}
					else
					{
						disk.setCurrentInode(current);
					}
				}
			}
		}
		else
		{
			System.out.println("Wrong syntax! Try: cd --help");
		}
    }
}