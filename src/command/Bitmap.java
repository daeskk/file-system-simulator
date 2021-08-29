package command;

import java.io.IOException;

import cruzapi.Disk;
import cruzapi.Main;
import cruzapi.Disk.BitmapType;

public class Bitmap extends Command
{
    public Bitmap(String name)
    {
        super(name);
    }

    @Override
    public void execute(String[] args) throws IOException
    {
    	Disk disk = Main.getDisk();
    	
        if(args.length == 1)
        {
            if(args[0].equalsIgnoreCase("inode"))
            {
                boolean[] bitmap = disk.getBitmap(BitmapType.INODE);
                for(int i = 0; i < bitmap.length; i++)
                {
                    if(bitmap[i])
                    {
                        System.out.println(bitmap[i] + " " + i);
                    }
                }
            }
            else if(args[0].equalsIgnoreCase("block"))
            {
            	boolean[] bitmap = disk.getBitmap(BitmapType.BLOCK);
            	
            	for(int i = 0; i < bitmap.length; i++)
            	{
            		if(bitmap[i])
            		{
            			System.out.println(bitmap[i] + " " + i);
            		}
            	}
            }
        }
        else
        {
            System.out.println("Wrong syntax! Try: cd <dir> | cd ..");
        }
    }
}