package king.yunlesao.ui.iface;

import android.os.Bundle;
import android.os.Handler;

/**
 * Created by King on 2018/4/11.
 */

public interface MainActionListener {
    void onActionRequest(Handler actionHandler,int flag, Bundle args);
}
