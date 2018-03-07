package king.yunlesao.ui;

import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import king.yunlesao.R;

/**
 * Created by King on 2018/1/3.
 */

public class HomeFragment extends BasedFragment implements BasedActivity.Test{
    //广告条
    @BindView(R.id.view_pager)ViewPager viewPager;
    @BindView(R.id.description)TextView description;
    @BindView(R.id.show_pointer)LinearLayout pointer;
    //功能面板
    @BindView(R.id.ability_mode_panel)ConstraintLayout constraintLayout;
    private AutoModeFragment autoModeFragment;
    private AdvancedModeFragment advancedModeFragment;
    private FragmentManager fm;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null){
        autoModeFragment=AutoModeFragment.newInstance(0,"自动","自动模式");
        advancedModeFragment=AdvancedModeFragment.newInstance(0,"高级","高级模式");
        fm=getChildFragmentManager();}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.home_fragment,container,false);
        replaceFragment(fm,R.id.ability_frame_layout,autoModeFragment);
        return view;
    }

    @Override
    public void change(int flag) {
        switch(flag){
            case 0X1:
                replaceFragment(fm,R.id.ability_frame_layout,autoModeFragment);
                break;
            case 0x2:
                replaceFragment(fm,R.id.ability_frame_layout,advancedModeFragment);
                break;
        }
    }

    public static HomeFragment newInstance(int index) {
        HomeFragment homeFragment = new HomeFragment();
        mark(homeFragment,index,null,null);
        return homeFragment;
    }
}
