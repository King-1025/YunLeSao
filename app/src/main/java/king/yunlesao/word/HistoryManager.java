package king.yunlesao.word;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import king.yunlesao.ui.iface.OnResetHistoryListener;
import king.yunlesao.utils.SettingManager;

public class HistoryManager
{
	private Context context;
    public final static String TABLE_NAME="tb_history";
	public final static String DEFAULT_CREATE_TABLE_SQL=
	         "create table if not exists "+TABLE_NAME+" (id integer primary key autoincrement,"+
	                                "date text,"+
	                                "wordNumber integer)";
	private SQLiteDatabase db;
	private static OnResetHistoryListener onResetHistoryListener;

	public HistoryManager(Context context)
	{
		this.context = context;
		db=new SimpleSQLiteOpenHelper(context).getWritableDatabase();
	}

	public ArrayList<History> queryAll(){
		String sql="select * from "+TABLE_NAME;
		Cursor cursor=db.rawQuery(sql,null);
		ArrayList<History> list=null;
		if(cursor!=null&&cursor.getCount()>0){
			list=new ArrayList<>();
			while(cursor.moveToNext()){
				History history=new History();
				history.setId(cursor.getInt(0));
				history.setDate(cursor.getString(1));
				history.setWordNumber(cursor.getInt(2));
				list.add(history);
			}
		}
		return list;
	}

	public ArrayList<WordBar> queryWordBar(int dateIndex,String date){
		String sql="select * from "+WordBarManager.TABLE_NAME+" where dateIndex="+dateIndex;
		Cursor cursor=db.rawQuery(sql,null);
		ArrayList<WordBar> list=null;
		if(cursor!=null&&cursor.getCount()>0){
			list=new ArrayList<>();
			while(cursor.moveToNext()){
				WordBar wb=new WordBar();
				wb.setSrcText(cursor.getString(1));
				wb.setDstText(cursor.getString(2));
				wb.setNote(cursor.getString(3));
				wb.setCreationDate(date);
				list.add(wb);
			}
		}
		return list;
	}

	public static void checkToReset(Context context){
		String sql="delete from "+TABLE_NAME+" where id > "+
				SettingManager.getHistoryMaxRecord(context.getApplicationContext())
				+" or wordNumber <= 0";
        SimpleSQLiteOpenHelper.execSQL(context,sql,true);
	}

	public static void reset(Context context){
		String sql="delete from "+TABLE_NAME+" where id > -1";
		SimpleSQLiteOpenHelper.execSQL(context,sql,true);
		if(onResetHistoryListener!=null){
			onResetHistoryListener.reset();
		}
	}

	public static void setOnResetHistoryListener(OnResetHistoryListener onResetHistoryListener) {
		HistoryManager.onResetHistoryListener = onResetHistoryListener;
	}

	public void release(){
		if(db!=null){
			db.close();
		}
	}

}
