package king.yunlesao.ui.iface;

/**
 * Created by King on 2018/4/5.
 */

public interface RecognitionEventNotify {
    void onRecognitionResult(String result,int type);
    void onRecognitionError(String error);
}
