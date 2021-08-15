package command;

import java.util.HashSet;
import java.util.Set;

public abstract class Command
{
	public static final Set<Command> SET = new HashSet<>();
	
	private final String name;
	private final String[] aliases;
	
	public Command(String name)
	{
		this(name, new String[0]);
	}
	
	public static void register(Command cmd)
	{
		SET.add(cmd);
	}
	
	public static Command getCommand(String name)
	{
		for(Command cmd : SET)
		{
			if(cmd.getName().equalsIgnoreCase(name))
			{
				return cmd;
			}
			
			for(String aliases : cmd.getAliases())
			{
				if(aliases.equalsIgnoreCase(name))
				{
					return cmd;
				}
			}
		}
		
		return null;
	}
	
	public Command(String name, String[] aliases)
	{
		this.name = name;
		this.aliases = aliases;
	}
	
	public abstract void execute(String[] args);
	
	public String getName()
	{
		return name;
	}
	
	public String[] getAliases()
	{
		return aliases;
	}
}