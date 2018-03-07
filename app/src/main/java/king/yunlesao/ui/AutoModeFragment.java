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

public class AutoModeFragment extends AbilityFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.auto_mode_fragment,container,false);
    }

    public static AutoModeFragment newInstance(int index,String name,String description) {
        AutoModeFragment AutoModeFragment = new AutoModeFragment();
        if(name==null){name="AutoModeFragment";}
        if(description==null){description="AutoMode";}
        mark(AutoModeFragment,index,name,description);
        return AutoModeFragment;
    }
}
