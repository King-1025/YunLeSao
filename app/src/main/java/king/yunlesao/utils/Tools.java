package king.yunlesao.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

import king.yunlesao.capture.ImageCaptureManager;

/**
 * Created by King on 2018/4/23.
 */

public class Tools {

    public static void  copyToClipboard(Context context,String text){
        ClipboardManager clipboardManager= (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(text);
    }

    public static File [] classification(File[] files){
        if(files==null){
            return null;
        }
        else{
            File[] temp=files;
            return quickSort(temp,0,temp.length-1);
        }
    }

    private static File[] quickSort(File[] files, int left, int right) {
        int partitionIndex;
        if (left < right) {
            partitionIndex = partition(files, left, right);
            quickSort(files, left, partitionIndex-1);
            quickSort(files, partitionIndex+1, right);
        }
        return files;
    }

    private static int partition(File[] files, int left, int right) {     // 分区操作
        int pivot = left,                      // 设定基准值（pivot）
                index = pivot + 1;
        for (int i = index; i <= right; i++) {
            int j=files[i].getName().compareToIgnoreCase(files[pivot].getName());
            if (files[i].isDirectory()&&j>0) {
                swap(files, i, index);
                index++;
            }
        }
        swap(files, pivot, index - 1);
        return index-1;
    }

    private static void swap(File[] files, int i, int j){
        File temp=files[i];
        files[i]=files[j];
        files[j]=temp;
    }

    public static FilenameFilter getFilenameFilter(final String regex){
        if(regex==null)return null;
        return new FilenameFilter(){
            @Override
            public boolean accept(File p1, String p2)
            {
                return Pattern.matches(regex, p2);
            }
        };
    }

    public static String getTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

//    public static int getHistoryMaxRecord(Context context){
//        SharedPreferences sp=context.getSharedPreferences("setting",Context.MODE_PRIVATE);
//        return 0;
//    }
//
//    public static void firstToSetting(Context context){
//        SharedPreferences sp=context.getSharedPreferences("",Context.MODE_PRIVATE);
//        sp.get
//    }
//    public static Set<String> getTranslateType(Context context){
//        return context.getSharedPreferences("setting",Context.MODE_PRIVATE).getStringSet("translateType", );
//    }
//    public static int getScanfType(Context context){
//       return context.getSharedPreferences("setting",Context.MODE_PRIVATE).getInt("scanfType", ImageCaptureManager.REQUEST_CODE_GENERAL_BASIC);
//    }

    public static boolean checkValue(Context context,String label,String value,boolean isNull){
        if(value==null||(value!=null&&value.length()<=0)){
            if(!isNull)
            Toast.makeText(context,label+"不能为空！",Toast.LENGTH_SHORT).show();
            return isNull;
        }else{
            if(value.contains("'")){
                Toast.makeText(context,label+"包含无效字符！"+value,Toast.LENGTH_SHORT).show();
                return false;
            }else {
                return true;
            }
        }
    }

    public static String getAppInfo(Context context){
        try {
            return "版本:"+context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
