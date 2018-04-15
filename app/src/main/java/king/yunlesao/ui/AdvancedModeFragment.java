package king.yunlesao.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import king.yunlesao.R;

/**
 * Created by King on 2018/1/3.
 * 高级模式碎片
 */

public class AdvancedModeFragment extends AbilityFragment {

    private boolean isNormal;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        isNormal=true;
        return inflater.inflate(R.layout.advanced_mode_fragment,container,false);
    }

    public boolean isNormal() {
        return isNormal;
    }
}
