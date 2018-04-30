package king.yunlesao.word;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class WordBarManager
{
	private Context context;
    public final static String TABLE_NAME="tb_wordbar";
	public final static String DEFAULT_CREATE_TABLE_SQL=
	"create table if not exists "+TABLE_NAME+" (id integer primary key autoincrement,"+
	                       "srcText text,"+
						   "dstText text,"+
						   "note text,"+
						   "dateIndex integer)";
	private final static String TAG="WordBarManager";
	private SQLiteDatabase db;
	public WordBarManager(Context context)
	{
		this.context = context;
		db=new SimpleSQLiteOpenHelper(context).getWritableDatabase();
	}
    
	public void release(){
		if(db!=null){
			db.close();
		}
	}
	public void add(WordBar wordBar){
		if(wordBar==null)return;
		String sql="select * from "+TABLE_NAME+" where srcText= ? and dstText= ?";
		Cursor cursor=db.rawQuery(sql,new String[]{wordBar.getSrcText(),wordBar.getDstText()});
		if(cursor!=null&&cursor.getCount()>0){
			Toast.makeText(context,"已添加！",Toast.LENGTH_SHORT).show();
			Log.i(TAG,"已添加! wordbar:"+wordBar.toString());
		}else{
		     String index=getHistoryId(db,wordBar.getCreationDate());
			 if(index==null){
				db.execSQL("insert into "+HistoryManager.TABLE_NAME+
						   "(date,wordNumber)"+
						   "values('"+wordBar.getCreationDate()+"',1)");
				 index=getHistoryId(db,wordBar.getCreationDate());
			 }else{
				 db.execSQL("update "+HistoryManager.TABLE_NAME+
				            " set wordNumber="+(getWordNumber(db,index)+1)+
							" where id="+index);    
			 }
				 int dateIndex=Integer.valueOf(index);
				 sql="insert into "+TABLE_NAME+
					 "(srcText,dstText,note,dateIndex)"+
					 "values('"+wordBar.getSrcText()+"','"+
					           wordBar.getDstText()+"','"+
					           wordBar.getNote()+"',"+
					           dateIndex+")";
				db.execSQL(sql);
			    Toast.makeText(context,"添加成功！",Toast.LENGTH_SHORT).show();
				 
		}
	}
	
	private int getWordNumber(SQLiteDatabase db,String index){
		if(db==null||index==null)return 0;
		String sql="select wordNumber from "+HistoryManager.TABLE_NAME+" where id= ?";
		Cursor cursor=db.rawQuery(sql,new String[]{index});
		if(cursor!=null&&cursor.getCount()>0){
			cursor.moveToFirst();
			return cursor.getInt(0);
		}else{
			return 0;
		}
	}
	private String getHistoryId(SQLiteDatabase db,String date){
		if(db==null||date==null)return null;
		String sql="select id from "+HistoryManager.TABLE_NAME+" where date= ?";
		Cursor cursor=db.rawQuery(sql,new String[]{date});
		if(cursor!=null&&cursor.getCount()>0){
			cursor.moveToFirst();
			return cursor.getInt(0)+"";
		}else{
			return null;
		}
	}
	
	private String getHistoryDate(SQLiteDatabase db,int id){
		if(db==null||id<0)return null;
		String sql="select date from "+HistoryManager.TABLE_NAME+" where id= ?";
		Cursor cursor=db.rawQuery(sql,new String[]{String.valueOf(id)});
		if(cursor!=null&&cursor.getCount()>0){
			cursor.moveToFirst();
			return cursor.getString(0);
		}else{
			return null;
		}
	}
	public ArrayList<WordBar> queryAll(){
		return query("select * from "+TABLE_NAME,null);
	}
	private ArrayList<WordBar>query(String sql,String[]where){
		if(sql==null)return null;
		Cursor cursor=db.rawQuery(sql,where);
		ArrayList<WordBar> list=null;
		if(cursor!=null&&cursor.getCount()>0){
			list=new ArrayList<>();
		    while(cursor.moveToNext()){
			   WordBar wb=new WordBar();
			   wb.setSrcText(cursor.getString(1));
			   wb.setDstText(cursor.getString(2));
			   wb.setNote(cursor.getString(3));
			   wb.setCreationDate(getHistoryDate(db,cursor.getInt(4)));
			   list.add(wb);
		    }
		}
		return list;
	}
	
	public void delete(WordBar warbar){
		if(warbar==null)return;
		delete("srcText",warbar.getSrcText());
		String sql="select id,wordNumber from "+HistoryManager.TABLE_NAME+" where date='"+warbar.getCreationDate()+"'";
		Cursor cursor=db.rawQuery(sql,null);
		if(cursor!=null&&cursor.getCount()>0){
			while(cursor.moveToNext()){
				int number=cursor.getInt(1)-1;
				if(number<=0){
					sql="delete from "+HistoryManager.TABLE_NAME+" where id="+cursor.getInt(0);
				}else{
					sql="update "+HistoryManager.TABLE_NAME+" set wordNumber="+number+" where id="+cursor.getInt(0);
				}
				db.execSQL(sql);
			}
		}
	}
	
	private void delete(String key,String value){
		if(key==null)return;
		String sql="delete from "+TABLE_NAME+
			" where "+key+"='"+value+"'";
		db.execSQL(sql);
	}
	
	public void update(WordBar warbar){
		if(warbar==null)return;
		String sql="update "+TABLE_NAME+
		        " set dstText='"+warbar.getDstText()+"'"+
			    " where srcText='"+warbar.getSrcText()+"'";
		db.execSQL(sql);
	}
}
