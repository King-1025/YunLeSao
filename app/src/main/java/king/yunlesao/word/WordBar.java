package king.yunlesao.word;

public class WordBar
{
	private String srcText;
	private String dstText;
	private String note;
	private String creationDate;
    
	public WordBar(){
	}
	public WordBar(String srcText, String dstText, String note, String creationDate)
	{
		this.srcText = srcText;
		this.dstText = dstText;
		this.note = note;
		this.creationDate = creationDate;
	}
	

	public void setSrcText(String srcText)
	{
		this.srcText = srcText;
	}

	public String getSrcText()
	{
		return srcText;
	}

	public void setDstText(String dstText)
	{
		this.dstText = dstText;
	}

	public String getDstText()
	{
		return dstText;
	}

	public void setNote(String details)
	{
		this.note = details;
	}

	public String getNote()
	{
		return note;
	}

	public void setCreationDate(String creationDate)
	{
		this.creationDate = creationDate;
	}

	public String getCreationDate()
	{
		return creationDate;
	}

	@Override
	public String toString()
	{
		// TODO: Implement this method
		return "原文:"+srcText+" 译文:"+dstText+" 笔记:"+note+" 创建日期:"+creationDate;
	}
	
}
