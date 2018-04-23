package king.yunlesao.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by King on 2018/4/23.
 */

public class Tools {

    public static void  copyToClipboard(Context context,String text){
        ClipboardManager clipboardManager= (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(text);
    }
}
