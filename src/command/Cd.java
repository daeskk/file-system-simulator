package command;

public class Cd extends Command
{
	public Cd(String name)
	{
		super(name);
	}
	
	@Override
	public void execute(String[] args)
	{
		if(args.length == 1)
		{
			if(args[0].length() > 26)
			{
				System.out.println("Directory name is too large (max 26 chars).");
				return;
			}
		}
		else
		{
			System.out.println("Wrong syntax! Try: mkdir <name>");
		}
	}
}