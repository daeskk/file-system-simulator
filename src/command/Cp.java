package command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cruzapi.Block;
import cruzapi.DirEntry;
import cruzapi.Disk;
import cruzapi.Inode;
import cruzapi.Main;
import cruzapi.Inode.Type;

public class Cp extends Command
{
	public Cp(String name)
	{
		super(name);
	}
	
	@Override
	public void execute(String[] args)
	{
		try
		{
			if(args.length == 2)
			{
				Disk disk = Main.getDisk();
				
				Object obj0;
				Object obj1;
				
				Inode arg0 = args[0].startsWith("/") ? new Inode(1, true) : disk.getCurrentInode();
				Inode arg1 = args[1].startsWith("/") ? new Inode(1, true) : disk.getCurrentInode();
				
				String[] path0 = Arrays.stream(args[0].split("/")).filter(x -> !x.isEmpty()).toArray(String[]::new);
				String[] path1 = Arrays.stream(args[1].split("/")).filter(x -> !x.isEmpty()).toArray(String[]::new);
				
				obj0 = path0[0].equalsIgnoreCase("dsc") ? disk.getEntryByPath(new Block(arg0.pointer()[0], true).getEntry(0), path0, 1) : new File(args[0]);
				obj1 = path1[0].equalsIgnoreCase("dsc") ? disk.getEntryByPath(new Block(arg1.pointer()[0], true).getEntry(0), Arrays.copyOf(path1, path1.length - 1), 1) : new File(args[1]);
				
				if(obj0 == null || obj1 == null)
				{
					System.out.println("File not found.");
					return;
				}
				
				byte[] data;
				if(obj0.getClass() == File.class)
				{
					try(FileInputStream fin = new FileInputStream((File) obj0))
					{
						data = fin.readAllBytes();
					}
					catch(IOException ex)
					{
						System.out.println(ex.getMessage());
						return;
					}
				}
				else
				{
					Inode inode = new Inode(((DirEntry) obj0).getIndex(), true);
					
					if(inode.getType() != Type.FILE)
					{
						System.out.println("args[0] isn't a file.");
						return;
					}
					
					data = new byte[inode.getSize()];
					
					int j = 0;
					
					try
					{
						for(int i : inode.pointer())
						{
							Block b = new Block(i);
							
							if(b.index() == 0)
							{
								break;
							}
							
							b.readFully();
							
							for(byte by : b.getData())
							{
								data[j++] = by;
							}
						}
					}
					catch(IndexOutOfBoundsException ex)
					{
						System.out.println(ex.getMessage());
					}
				}
				
				if(obj1.getClass() == File.class)
				{
					try(FileOutputStream file = new FileOutputStream((File) obj1))
					{
						file.write(data);
					}
					catch(IOException ex)
					{
						throw ex;
					}
				}
				else
				{
					if(data.length > 4096 * 12)
					{
						System.out.println("File too big.");
						return;
					}
					
					Inode inode = new Inode(((DirEntry) obj1).getIndex(), true);
					
					Inode newInode = disk.getEmptyInode();
					
					if(newInode == null)
					{
						System.out.println("No empty inode available.");
						return;
					}
					
					newInode.setSize(data.length);
					newInode.setType(Type.FILE);
					
					DirEntry newEntry = new DirEntry(newInode.index(), path1[path1.length - 1]);
					
					Integer[] pointer = Arrays.stream(inode.pointer()).boxed().toArray(Integer[]::new);
					
					Arrays.sort(pointer, Collections.reverseOrder());
					
					for(int i = 0; i < pointer.length; i++)
					{
						Block b = new Block(inode.pointer()[i]);
						
						if(b.index() == 0)
						{
							b = disk.getEmptyBlock();
							
							if(b == null)
							{
								System.out.println("No such block available.");
								return;
							}
						}
						
						b.readFully();
						
						if(b.addEntry(newEntry))
						{
							List<Block> newBlocks = new ArrayList<>();
							
							Block b1 = null;
							
							for(int j = 0; j < data.length; j++)
							{
								if(j % 4096 == 0)
								{
									b1 = disk.getEmptyBlock();
									
									if(b1 == null)
									{
										for(Block b2 : newBlocks)
										{
											b2.setInUse(false);
										}
										
										System.out.println("No empty block available.");
										return;
									}
									
									b1.setInUse(true);
									
									newInode.addPointer(b1.index());
									newBlocks.add(b1);
								}
								
								b1.data[j % 4096] = data[j];
							}
							
							b.setInUse(true);
							b.rw();
							
							newInode.rw();
							newInode.setInUse(true);

							for(Block b2 : newBlocks)
							{
								b2.rw();
								b2.setInUse(true);
							}
							return;
						}
					}
					
					System.out.println("Inode is full.");
				}
//				Inode inode1 = new Inode(entry1.getIndex(), true);
//				
//				for(int i = 0; i < inode1.pointer().length; i++)
//				{
//					Block b = new Block(inode1.pointer()[i]);
//					
//					if(b.index() == 0)
//					{
//						b = disk.getEmptyBlock();
//					}
//					else
//					{
//						b.readFully();
//					}
//					
//					if(b.addEntry(entry0))
//					{
//						inode1.pointer()[i] = b.index();
//						inode1.rw();
//						b.rw();
//						b.setInUse(true);
//						System.out.println(String.format("File \"%s\" copied to directory \"%s\".", args[0], args[1]));
//						return;
//					}
//				}
//				
//				System.out.println("Inode is full.");
			}
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
}