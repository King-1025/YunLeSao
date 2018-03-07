package king.yunlesao.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import king.yunlesao.R;

/**
 * Created by King on 2018/1/3.
 */

public class AdvancedModeFragment extends AbilityFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.advanced_mode_fragment,container,false);
    }

    public static AdvancedModeFragment newInstance(int index,String name,String description) {
        AdvancedModeFragment advancedModeFragment = new AdvancedModeFragment();
        if(name==null){name="AdvancedModeFragment";}
        if(description==null){description="AdvancedMode";}
        mark(advancedModeFragment,index,name,description);
        return advancedModeFragment;
    }
}
