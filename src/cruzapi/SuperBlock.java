package cruzapi;

public class SuperBlock
{
	public static final int SUPER_BLOCK_SIZE = 12;
	private int magicNumber = 0xEF53;
	private int blocks = 25545;
	private int blockSize = 1 << 12;
	
	public SuperBlock()
	{
		
	}
	
	public SuperBlock(int magicNumber, int blocks, int blockSize)
	{
		this.magicNumber = magicNumber;
		this.blocks = blocks;
		this.blockSize = blockSize;
	}
	
	public int getMagicNumber()
	{
		return magicNumber;
	}
	
	public int getBlocks()
	{
		return blocks;
	}
	
	public int getBlockSize()
	{
		return blockSize;
	}
	
	public int getInodes()
	{
		return blocks / 12;
	}
	
	public int getSuperBlockSize()
	{
		return SUPER_BLOCK_SIZE;
	}
	
	public int getBitMapSize()
	{
		return blocks / 8;
	}
	
	public int getInodesSize()
	{
		return Inode.INODE_SIZE * getInodes();
	}
}