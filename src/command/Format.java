package command;

import cruzapi.Main;

public class Format extends Command
{
	public Format(String name)
	{
		super(name);
	}
	
	@Override
	public void execute(String[] args)
	{
		long time = System.currentTimeMillis();
		
		System.out.println("Formating disk...");
		Main.getDisk().format();
		System.out.println("Done! (" + (System.currentTimeMillis() - time) + " ms)");
	}
}