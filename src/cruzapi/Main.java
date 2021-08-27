package cruzapi;

import java.io.IOException;
import java.util.Scanner;

import command.Cd;
import command.Command;
import command.Exit;
import command.Ls;
import command.Mkdir;
import command.Pwd;

public class Main
{
	public static boolean loop = true;
	private static Disk disk;
	
	public static void main(String[] arg) throws IOException
	{
		System.out.println("Creating disk...");
		
		long time = System.currentTimeMillis();
		
		disk = new Disk();
		disk.create();
		
		System.out.println("Done! (" + (System.currentTimeMillis() - time) + " ms)");
		
		for(int i = 0; i < 120; i++)
		{
			System.out.println(i);
			disk.mkdir("test" + i);
		}
		
		
		Command.register(new Exit("exit"));
		Command.register(new Mkdir("mkdir"));
		Command.register(new Ls("ls"));
		Command.register(new Cd("cd"));
		Command.register(new Pwd("pwd"));
		
		Scanner scanner = new Scanner(System.in);
		
		while(loop)
		{
			String[] line = scanner.nextLine().split(" ");
			String[] args = new String[line.length - 1];
			
			String name = line[0];
			
			for(int i = 1; i < line.length; i++)
			{
				args[i - 1] = line[i];
			}
			
			Command command = Command.getCommand(name);
			
			if(command != null)
			{
				command.execute(args);
			}
			else
			{
				System.out.println("Unknown command.");
			}
		}
		
		scanner.close();
	}
	
	public static Disk getDisk()
	{
		return disk;
	}
}