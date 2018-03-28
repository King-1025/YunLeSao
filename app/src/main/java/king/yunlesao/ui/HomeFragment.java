package king.yunlesao.ui;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsSeekBar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import king.yunlesao.R;

/**
 * Created by King on 2018/1/3.
 * 主页碎片
 */

public class HomeFragment extends BasedFragment {
   //广告条
   @BindView(R.id.view_pager)ViewPager viewPager;
   @BindView(R.id.description)TextView description;
   @BindView(R.id.show_pointer)LinearLayout pointer;

    private AutoModeFragment autoModeFragment;
    private AdvancedModeFragment advancedModeFragment;
    private AbilityFragment currentFragment;
    private FragmentManager fm;
    private final static String TAG="HomeFragment";

    private ImagePageAdapter imagePageAdapter;
    private int imageId[]={R.mipmap.ic_launcher,R.mipmap.ic_launcher_round};
    private String text[]={"image_0","image_1"};
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null)
        {
            autoModeFragment= (AutoModeFragment)
                    BasedFragment.makeFragment(BasedFragment.AUTO_MODE_FRAGMENT,0,"自动","自动模式");
            advancedModeFragment= (AdvancedModeFragment)
                    BasedFragment.makeFragment(BasedFragment.ADVANCED_MODE_FRAGMENT,0,"高级","高级模式");
            fm=getChildFragmentManager();
        }
      //  imagePageAdapter=new ImagePageAdapter(getImageList(imageId));
      //  viewPager.setAdapter(imagePageAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private ArrayList<ImageView> getImageList(int resid[]){
            if (resid==null)return null;
            ArrayList<ImageView>list=new ArrayList<>();
            Log.i(TAG,"attachActivity:"+attachActivity);
            for(int i=0;i<resid.length;i++){
                ImageView iv=new ImageView(getActivity());
                iv.setBackgroundResource(resid[i]);
                list.add(iv);
            }
            return list;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.home_fragment,container,false);
        show(autoModeFragment);
        imagePageAdapter=new ImagePageAdapter(getImageList(imageId));
        return view;
    }


    private void show(AbilityFragment target){
        currentFragment=(AbilityFragment)show(fm,currentFragment,target,R.id.ability_frame_layout);
    }

}
