package command;

public class Bitmap extends Command
{
    public Bitmap(String name)
    {
        super(name);
    }

    @Override
    public void execute(String[] args)
    {
        if(args.length == 1)
        {
            if(args[0].equalsIgnoreCase("inode"))
            {
            	
            }
            else if(args[0].equalsIgnoreCase("block"))
            {
            	
            }
        }
        else
        {
            System.out.println("Wrong syntax! Try: cd <dir> | cd ..");
        }
    }
}