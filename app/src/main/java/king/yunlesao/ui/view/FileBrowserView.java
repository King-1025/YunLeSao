package king.yunlesao.ui.view;


import android.content.*;
import android.os.storage.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import king.yunlesao.R;
import king.yunlesao.utils.Tools;

public class FileBrowserView extends ListView implements OnItemClickListener
{
	private Context ctx;
	private FileAdapter mFileAdapter;
	private String innerPath;
	private String outerPath;
	private String currentDir;
	private ArrayList<String> pathList;
	private int count;
	private boolean isInit;
	private final static String ROOT_DIR="root_dir";
	private final static String TAG="FileBrowserView";

	public final static int FLAG_ALL=0xa0;
	public final static int FLAG_ALL_HIDDEN=0xa1;
	public final static int FLAG_ALL_NO_HIDDEN=0xa2;

	public final static int FLAG_ALL_FOLDER=0xb0;
	public final static int FLAG_HIDDEN_FOLDER=0xb1;
	public final static int FLAG_NO_HIDDEN_FOLDER=0xb2;

	public final static int FLAG_ALL_FILE=0xc0;
	public final static int FLAG_HIDDEN_FILE=0xc1;
	public final static int FLAG_NO_HIDDEN_FILE=0xc2;

	public final static int FLAG_MATCH=0x07;
	private int flag;
	private String matchString;
	private View fileItem;
	private String selectedFilePath;
	public FileBrowserView(Context context){
		this(context,null);
	}

	public FileBrowserView(Context context, AttributeSet attrs){
		this(context,attrs,0);
	}

	public FileBrowserView(Context context, AttributeSet attrs, int defStyleAttr){
		super(context,attrs,defStyleAttr);
		init(context);
	}
	
	private void init(Context context){
		ctx=context;
		isInit=true;
		flag=FLAG_ALL_NO_HIDDEN;
		updateView(ROOT_DIR);
	}

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		String path;
		if(isRoot()){
			path=pathList.get(p3);
		}else{
			if(p3==0){
				//获取上层目录路径
				path=new File(currentDir).getParent();
				//String name=new File(currentDir).getName();
				//path=currentDir.substring(0,currentDir.length()-name.length()-1);
				//Toast.makeText(ctx,path,Toast.LENGTH_SHORT).show();
			}else{
				path=pathList.get(p3-1);
			}
		}
		path=checkPath(path);
		if(path==null){
			Toast.makeText(ctx,"onItemClick()->path is "+path,Toast.LENGTH_SHORT).show();
		}else{
			File file=new File(path);
			if(file.isFile()){
				if(selectedFilePath!=null&&path.equals(selectedFilePath)){
					selectedFilePath=null;
					if(fileItem!=null){
						fileItem.setBackgroundResource(R.color.colorItemBackgroundNormal);
					}
				}else{
					selectedFilePath=path;
					if(fileItem!=null){
						fileItem.setBackgroundResource(R.color.colorItemBackgroundNormal);
					}
					fileItem=p2;
					fileItem.setBackgroundResource(R.color.colorItemBackgroundSelect);
				}
			}else{
				selectedFilePath=null;
				if(fileItem!=null){
					fileItem.setBackgroundResource(R.color.colorItemBackgroundNormal);
				}
				updateView(path);
			}
		}
		//Toast.makeText(ctx,path,Toast.LENGTH_SHORT).show();
	}

	private boolean isRoot(){
		return (ROOT_DIR.equals(currentDir)||currentDir.equals("/"));
	}

	private String checkPath(String path){
		if(path!=null){
			if((!currentDir.equals(ROOT_DIR))&&(path.equals(innerPath)||path.equals(outerPath))){
				return ROOT_DIR;
			}else{
				return path;
			}
		}else{
			return null;
		}
	}
	//更新视图
	private void updateView(String path){
	   if(path==null){
		   Log.i(TAG,"path:"+path);
		   Toast.makeText(ctx,"path is "+path,Toast.LENGTH_SHORT).show();
		   return;
	   }
	   if(path.equals(ROOT_DIR)){	
			pathList=new ArrayList<String>();
			if(isInit){
			   innerPath=getStoragePath(ctx,false);
			   outerPath=getStoragePath(ctx,true);
			}
			if(innerPath!=null){
				pathList.add(innerPath);
			}
			if(outerPath!=null){
				pathList.add(outerPath);
			}
	   }else{
			ArrayList<String>tempPathList=getPathList(path,flag,matchString);
			if(tempPathList!=null){
				pathList=tempPathList;
			}else{
				return;
			}
	   }
		currentDir=path;
		count=pathList.size();
		if(isInit){
			isInit=false;
			mFileAdapter=new FileAdapter();
			setAdapter(mFileAdapter);
			setOnItemClickListener(this);
		}else{
			mFileAdapter.notifyDataSetChanged();
		}
	  // Toast.makeText(ctx,"updateView() count:"+count+" currentDir:"+currentDir,Toast.LENGTH_SHORT).show();
	}

	//反射获取存储路径
	public static String getStoragePath(Context mContext, boolean is_removale) { 

		StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
		Class<?> storageVolumeClazz = null;
		try {
			storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
			Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
			Method getPath = storageVolumeClazz.getMethod("getPath");
			Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
			Object result = getVolumeList.invoke(mStorageManager);
			final int length = Array.getLength(result);
			for (int i = 0; i < length; i++) {
				Object storageVolumeElement = Array.get(result, i);
				String path = (String) getPath.invoke(storageVolumeElement);
				boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
				if (is_removale == removable) {
					return path;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getCurrentDir(){
		if(ROOT_DIR.equals(currentDir)){
			return null;
		}else{
		    return currentDir;
		}
	}
	
	public String getInnerPath(){
		return innerPath;
	}
	
	public String getOuterPath(){
		return outerPath;
	}

	private ArrayList<String> getPathList(String path,int flag,String regex){
		if(path==null)return null;
		File fd=new File(path);
		if(fd==null){
			Log.i(TAG,"path:"+path+" fd:"+fd);
			Toast.makeText(ctx,"path:"+path+" fd:"+fd,Toast.LENGTH_SHORT).show();
			return null;
		}

		if(fd.isFile()){
			Log.i(TAG,"it is a file. path:"+path);
			Toast.makeText(ctx,"it is a file. path:"+path,Toast.LENGTH_SHORT).show();
			return null;
		}

		File []childFiles;

		if(flag==FLAG_MATCH){
			childFiles=fd.listFiles(Tools.getFilenameFilter(regex));
		}else {
			childFiles=fd.listFiles();
		}

		if(childFiles==null){
			Log.i(TAG,"childFiles:"+childFiles);
			Toast.makeText(ctx,"childFiles:"+childFiles,Toast.LENGTH_SHORT).show();
			return null;
		}

		Tools.classification(childFiles);//分类处理

		int length=childFiles.length;
		ArrayList<String> tempPathList=new ArrayList<>();
		for(int i=0;i<length;i++){
			switch(flag){
				case FLAG_ALL_HIDDEN:
					if(childFiles[i].isHidden())continue;
					break;
				case FLAG_ALL_NO_HIDDEN:
					if(!childFiles[i].isHidden())continue;
					break;
				case FLAG_ALL_FOLDER:
					if(childFiles[i].isFile())continue;
					break;
				case FLAG_HIDDEN_FOLDER:
					if((childFiles[i].isHidden())||childFiles[i].isFile())continue;
					break;
				case FLAG_NO_HIDDEN_FOLDER:
					if((!childFiles[i].isHidden())||childFiles[i].isFile())continue;
					break;
				case FLAG_ALL_FILE:
					if(childFiles[i].isDirectory())continue;
					break;
				case FLAG_HIDDEN_FILE:
					if((childFiles[i].isHidden())||childFiles[i].isDirectory())continue;
					break;
				case FLAG_NO_HIDDEN_FILE:
					if((!childFiles[i].isHidden())||childFiles[i].isDirectory())continue;
					break;
				case FLAG_ALL:
					default: break;
			}
			tempPathList.add(childFiles[i].getAbsolutePath());
		}
		return tempPathList;
	}

	private void pop(){
		Toast.makeText(ctx,"count:"+count+" currentDir:"+currentDir,Toast.LENGTH_SHORT).show();
	}

	public void showType(int flag,String matchString){
		this.flag=flag;
		this.matchString=matchString;
		updateView(currentDir);
	}

	//内部适配器类
	class FileAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			if(isRoot()){
				return count;
			}else{
				return count+1;
			}
		}

		@Override
		public Object getItem(int p1)
		{
			return null;
		}

		@Override
		public long getItemId(int p1)
		{
			return 0;
		}

		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			View item;
			if(p2==null){
				item=LayoutInflater.from(getContext()).inflate(R.layout.file_item,null);
			}else{
				item=p2;
			}
			TextView title= (TextView) item.findViewById(R.id.file_tilte);
			TextView subtitle= (TextView) item.findViewById(R.id.file_subtitle);
			ImageView icon= (ImageView) item.findViewById(R.id.file_icon);
			int index;
			if(isRoot()){
				index=p1;
			}else{
				if(p1==0){
					title.setText("../");
					subtitle.setText("上层目录");
					icon.setBackgroundResource(R.drawable.file_dir);
					return item;
				}else{
					index=p1-1;
				}
			}
			String path=pathList.get(index);
			if(selectedFilePath!=null&&path.equals(selectedFilePath)){
				item.setBackgroundResource(R.color.colorItemBackgroundSelect);
			}else{
				item.setBackgroundResource(R.color.colorItemBackgroundNormal);
			}
			if(path==null){
				Toast.makeText(ctx,"path is null",Toast.LENGTH_SHORT).show();
			}else{
				File f=new File(path);
				if(f==null){
					Toast.makeText(ctx,"f is null",Toast.LENGTH_SHORT).show();
				}else{
					title.setText(f.getName());
					subtitle.setText(f.getPath());
					if(f.isDirectory()){
						icon.setBackgroundResource(R.drawable.file_dir);
					}else{
						icon.setBackgroundResource(R.drawable.file);
					}
				}
			}
			return item;
		}

	}

	public String getSelectedFilePath() {
		return selectedFilePath;
	}
}

