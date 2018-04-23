package king.yunlesao.capture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.baidu.ocr.ui.camera.CameraActivity;

import java.io.File;

import king.yunlesao.ui.MainActivity;

/**
 * Created by King on 2018/4/5.
 */

public class ImageCaptureManager {
    private Context context;
    private String imagePath;
    private final static String TAG="ImageCaptureManager";
    private final static String IMAGE_NAME="pic.jpg";

    public final static String IMAGE_SAVE_PATH="image_save_path";

    public static final int BAIDU_CAMERA=0xc0;

    public static final int REQUEST_CODE_GENERAL = 105;
    public static final int REQUEST_CODE_GENERAL_BASIC = 106;
    public static final int REQUEST_CODE_ACCURATE_BASIC = 107;
    public static final int REQUEST_CODE_ACCURATE = 108;
    public static final int REQUEST_CODE_GENERAL_ENHANCED = 109;
    public static final int REQUEST_CODE_GENERAL_WEBIMAGE = 110;
    public static final int REQUEST_CODE_BANKCARD = 111;
    public static final int REQUEST_CODE_VEHICLE_LICENSE = 120;
    public static final int REQUEST_CODE_DRIVING_LICENSE = 121;
    public static final int REQUEST_CODE_LICENSE_PLATE = 122;
    public static final int REQUEST_CODE_BUSINESS_LICENSE = 123;
    public static final int REQUEST_CODE_RECEIPT = 124;

    public final static int FLAG_TAKE_PICTURE_FINISH=0xA0;
    public final static int FLAG_TAKE_PICTURE_ERROR=0xA1;
    public ImageCaptureManager(Context ctx){
        context=ctx;
        imagePath=context.getFilesDir()+"/"+IMAGE_NAME;
        Log.i(TAG,"imagePath:"+imagePath);
    }

    public String getImagePath() {
        return imagePath;
    }

    public static boolean takePictureByBaiduCamera(Activity src,String imageSavePath){
        return takePictureByBaiduCamera(src,imageSavePath,REQUEST_CODE_GENERAL);
    }

    public static boolean takePictureByBaiduCamera(Activity src,String imageSavePath,int flag) {
        if(src==null){
            Toast.makeText(src.getApplicationContext(),"Activity is null.",Toast.LENGTH_LONG).show();
            return false;
        }
        if(imageSavePath==null){
            Toast.makeText(src.getApplicationContext(),"imageSavePath is null.",Toast.LENGTH_LONG).show();
            return false;
        }
        if(new File(imageSavePath)==null){
            Toast.makeText(src.getApplicationContext(),"图片缓存路径不可用！",Toast.LENGTH_LONG).show();
            return false;
        }
        switch (flag){
            case REQUEST_CODE_GENERAL:
            case REQUEST_CODE_GENERAL_BASIC:
            case REQUEST_CODE_ACCURATE:
            case REQUEST_CODE_ACCURATE_BASIC:
            case REQUEST_CODE_GENERAL_ENHANCED:
            case REQUEST_CODE_GENERAL_WEBIMAGE:
            case REQUEST_CODE_BANKCARD:
            case REQUEST_CODE_VEHICLE_LICENSE:
            case REQUEST_CODE_DRIVING_LICENSE:
            case REQUEST_CODE_LICENSE_PLATE:
            case REQUEST_CODE_BUSINESS_LICENSE:
            case REQUEST_CODE_RECEIPT:
                Intent intent = new Intent(src, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,imageSavePath);
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,CameraActivity.CONTENT_TYPE_GENERAL);
                src.startActivityForResult(intent,flag);
                break;
        }
        return true;
    }

    public static boolean checkImagePath(String path){
        boolean isValid=false;
        if(path!=null){
            File imageFile=new File(path);
            if(imageFile.exists()){
                isValid=true;
            }
        }
        return isValid;
    }

}
