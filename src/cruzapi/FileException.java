package cruzapi;

public class FileException extends Exception
{
	private static final long serialVersionUID = -3047172610365724729L;
	
	private final int code;
	
	public FileException(String msg, int code)
	{
		super(msg);
		this.code = code;
	}
	
	public int getErrorCode()
	{
		return code;
	}
}