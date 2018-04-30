package king.yunlesao.word;

public class History
{
	private int id;
	private String date;
	private int wordNumber;

	public History(int id,String date, int wordNumber)
	{
		this.id=id;
		this.date = date;
		this.wordNumber = wordNumber;
	}

	public History(){

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getDate()
	{
		return date;
	}

	public void setWordNumber(int wordNumber)
	{
		this.wordNumber = wordNumber;
	}

	public int getWordNumber()
	{
		return wordNumber;
	}

	@Override
	public String toString()
	{
		// TODO: Implement this method
		return "日期:"+date+" 词条数:"+wordNumber;
	}
	
}
