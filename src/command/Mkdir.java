package command;

import java.io.IOException;

import cruzapi.Main;

public class Mkdir extends Command
{
	public Mkdir(String name)
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
			
			try
			{
				Main.getDisk().mkdir(args[0]);
				System.out.println(String.format("Directory \"%s\" created.", args[0]));
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Wrong syntax! Try: mkdir <name>");
		}
	}
}