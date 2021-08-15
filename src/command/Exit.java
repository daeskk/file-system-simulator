package command;

import cruzapi.Main;

public class Exit extends Command
{
	public Exit(String name)
	{
		super(name);
	}
	
	@Override
	public void execute(String[] args)
	{
		Main.loop = false;
		System.out.println("Bye bye!");
	}
}