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
    public final static int BAIDU_CAMERA_REQUEST_CODE_GENERAL_BASIC = 106;
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

    public static boolean takePictureByBaiduCamera(Activity src,String imageSavePath) {
        if(src==null){
            Toast.makeText(src.getApplicationContext(),"Activity is null.",Toast.LENGTH_LONG).show();
            return false;
        }
        if(imageSavePath==null){
            Toast.makeText(src.getApplicationContext(),"imageSavePath is null.",Toast.LENGTH_LONG).show();
            return false;
        }
        File imageFile=new File(imageSavePath);
        if(imageFile==null){
            Toast.makeText(src.getApplicationContext(),"图片缓存路径不可用！",Toast.LENGTH_LONG).show();
            return false;
        }
        Intent intent = new Intent(src, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,imageSavePath);
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,CameraActivity.CONTENT_TYPE_GENERAL);
        src.startActivityForResult(intent,BAIDU_CAMERA_REQUEST_CODE_GENERAL_BASIC);
        return true;
    }
}
