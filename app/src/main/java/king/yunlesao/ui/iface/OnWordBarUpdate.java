package king.yunlesao.ui.iface;

import java.util.ArrayList;

import king.yunlesao.word.WordBar;

/**
 * Created by King on 2018/4/29.
 */

public interface OnWordBarUpdate {
    void onUpdate(int count, ArrayList<WordBar> wordBars);
}
