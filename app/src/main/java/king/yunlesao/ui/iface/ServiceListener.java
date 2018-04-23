package king.yunlesao.ui.iface;

/**
 * Created by King on 2018/4/20.
 */

public interface ServiceListener {
    void onResultWordList(String result);
    void onResultJsonRes(String result);
    void onError(String error);
}
