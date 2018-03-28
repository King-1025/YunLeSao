package king.yunlesao.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import king.yunlesao.R;

/**
 * Created by King on 2018/1/3.
 * 历史记录碎片
 */

public class HistoryFragment extends BasedFragment {

//    public static HistoryFragment newInstance() {
//        return new HistoryFragment();
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history_fragment,container,false);
    }

}
