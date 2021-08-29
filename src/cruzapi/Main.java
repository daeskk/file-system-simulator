package cruzapi;

import java.io.IOException;
import java.util.Scanner;

import command.Bitmap;
import command.Cd;
import command.Command;
import command.Cp;
import command.Exit;
import command.Format;
import command.Ls;
import command.Mkdir;
import command.Pwd;
import command.Rm;
import command.Touch;

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

		Command.register(new Exit("exit"));
		Command.register(new Mkdir("mkdir"));
		Command.register(new Ls("ls"));
		Command.register(new Cd("cd"));
		Command.register(new Cp("cp"));
		Command.register(new Pwd("pwd"));
		Command.register(new Format("format"));
		Command.register(new Rm("rm"));
		Command.register(new Touch("touch"));
		Command.register(new Bitmap("bitmap"));
		
		Scanner scanner = new Scanner(System.in);
		
		while(loop)
		{
			System.out.printf("cruzAPI@Linux:~%s$ ", Pwd.pwd(disk));
			String[] line = scanner.nextLine().split(" ");
			String[] args = new String[line.length - 1];
			
			String name = line[0];

			System.arraycopy(line, 1, args, 0, line.length - 1);
			
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