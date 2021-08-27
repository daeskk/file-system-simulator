package command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cruzapi.*;

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
            String dir = args[0];

            Disk disk = Main.getDisk();

            Inode current = disk.getCurrentInode();

            if(dir.equals("."))
            {
                return;
            }
            else if(dir.equals(".."))
            {
                try {
                    Block block = new Block(current.pointer()[0]);
                    block.readFully();

                    for(int i = 0; i < 30; i++)
                    {
                        System.out.println(block.getData()[i]);
                    }

                    current = new Inode(block.getEntry(1).getIndex());
                    current.readFully();

                    disk.setCurrentInode(current);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                try {

                    for(int i = 0; i < current.pointer().length; i++)
                    {
                        Block block = new Block(current.pointer()[i]);

                        if(block.index() == 0) continue;

                        block.readFully();

                        for(DirEntry entry : block.getEntries())
                        {
                            if(dir.equalsIgnoreCase(entry.getName()))
                            {
                                current = new Inode(entry.getIndex());
                                current.readFully();
                                disk.setCurrentInode(current);
                                System.out.println("Changed to \"" +  entry.getName() + "\" dir.");
                                return;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.printf("Directory \"%s\" not found.%n", dir);
            }
        }
        else
        {
            System.out.println("Wrong syntax! Try: cd <dir> | cd ..");
        }
    }
}