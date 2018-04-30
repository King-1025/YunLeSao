package king.yunlesao.word;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SimpleSQLiteOpenHelper extends SQLiteOpenHelper
{

	private Context context;
	private final static String DB_NAME="yunlesao.db";
	private final static int VERSION=1;
	
	private final static String TAG="SimpleSQLiteOpenHelper";
	public SimpleSQLiteOpenHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase p1)
	{
		// TODO: Implement this method
		p1.execSQL(WordBarManager.DEFAULT_CREATE_TABLE_SQL);
		Log.i(TAG,"已创建词条表");
		p1.execSQL(HistoryManager.DEFAULT_CREATE_TABLE_SQL);
		Log.i(TAG,"已创建历史表");
	}

	@Override
	public void onUpgrade(SQLiteDatabase p1, int p2, int p3)
	{
		// TODO: Implement this method
	}
	
	public static void execSQL(Context context,String sql,boolean isWritable){
		if(context==null||sql==null)return;
		SQLiteDatabase db;
		if(isWritable){
			db=new SimpleSQLiteOpenHelper(context).getWritableDatabase();
		}else{
			db=new SimpleSQLiteOpenHelper(context).getReadableDatabase();
		}
		db.execSQL(sql);
		db.close();
	}
	
}
