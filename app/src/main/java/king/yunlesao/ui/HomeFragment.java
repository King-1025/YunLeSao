package king.yunlesao.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import king.yunlesao.R;
import king.yunlesao.ui.iface.ModeSwitcher;
import king.yunlesao.ui.iface.ViewChanger;

/**
 * Created by King on 2018/1/3.
 * 主页碎片
 */

public class HomeFragment extends BasedFragment implements
        ViewPager.OnPageChangeListener,ViewChanger,ModeSwitcher{
   //广告条
   @BindView(R.id.view_pager)ViewPager viewPager;
   @BindView(R.id.description)TextView description;
   @BindView(R.id.show_pointer)LinearLayout pointer;
   @BindView(R.id.top_viewpager_layout)RelativeLayout top_viewpager;
    private AutoModeFragment autoModeFragment;
    private AdvancedModeFragment advancedModeFragment;
    private AbilityFragment currentFragment;
    private FragmentManager fm;
    private final static String TAG="HomeFragment";

    private ImagePageAdapter imagePageAdapter;
    private int imageId[]={R.drawable.image_1,R.drawable.image_2,R.drawable.image_3};
    private String text[]={"image_1","image_2","image_3"};
    private int currentPosition;
    private int oldChildId;
    private int size;
    private Handler mh;
    private boolean isAutoShow;
    private final static int AUTO_SHOW=0x00;
    private final static int changeTime=2000;//视图自滑动间隔时间
    private final static int waitTime=5000;//等待视图重新自滑动的时间

    public final static int FLAG_NOEMAL=0x00;
    public final static int FLAG_ONLY_SHOW_HOME_FRAGMENT=0x01;

    public final static int MODE_CHANGE_AUTO=0x10;
    public final static int MODE_CHANGE_ADVANCED=0x11;

    private int currentMode;
    private boolean isNormal;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG,"onAttach() is called.");
    }

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
        }
        Log.i(TAG,"onCreate() is called.");
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.home_fragment,container,false);
        unbinder=ButterKnife.bind(this,view);
        mActivity=getActivity();
        fm=getChildFragmentManager();
        initViewPager();
        initAbilityPanel();
        //注意：必须初始化为false，强制第一次更新，防止添加Fragment导致界面重绘，状态丢失。
        isNormal=false;
        Log.i(TAG,"onCreateView() is called.");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        startAutoShow(changeTime);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoShow();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder!=null)unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeAllFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initViewPager(){
        //初始化size，选取最小长度。
        if(imageId.length<text.length){
            size=imageId.length;
        }else {
            size=text.length;
        }
        //ViewPager适配器
        imagePageAdapter=new ImagePageAdapter(
                getImageList(mActivity,imageId,size));//初始化视图列表
        viewPager.setAdapter(imagePageAdapter);//设置适配器
        viewPager.addOnPageChangeListener(this);

        //初始化指示器
        createPoints(mActivity,pointer,R.drawable.point_selector,size);

        //初始化最开始的视图位置
        if(size>1){
            currentPosition=1;
            oldChildId=currentPosition-1;
            viewPager.setCurrentItem(currentPosition,false);
        }

        //设置图片描述
        description.setText(text[0]);
        //设置指示位置
        pointer.getChildAt(0).setEnabled(false);

        mh=new Handler(mActivity.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case AUTO_SHOW:
                        if(isAutoShow){
                            scrollViewPager(false);//向右自动滑动
                        }else {
                            mh.removeMessages(AUTO_SHOW);
                        }
                        break;
                }
            }
        };
    }

    private void initAbilityPanel(){
        AbilityFragment.setViewChanger(this);

        show(advancedModeFragment);
        currentMode=MODE_CHANGE_ADVANCED;

        show(autoModeFragment);
        currentMode=MODE_CHANGE_AUTO;

    }

    private void show(AbilityFragment target){
        currentFragment=(AbilityFragment)display(fm,currentFragment,target,R.id.ability_frame_layout);
    }
    private void removeAllFragment(){
        remove(fm,autoModeFragment);
        remove(fm,advancedModeFragment);
    }
    private ArrayList<ImageView> getImageList(Activity activity,int resid[],int length){
        if (activity==null||resid==null||length<=0)return null;
        if(length>resid.length){
            Log.e(TAG,"error! size:"+length+" > "+"resid.length:"+resid.length);
            return null;
        }
        ArrayList<ImageView>list=new ArrayList<>();
        ImageView iv;
        if(length==1){
            iv=new ImageView(activity);
            iv.setBackgroundResource(resid[0]);
            list.add(iv);
        }else{
            //起始位置对应最后一个视图
            iv=new ImageView(activity);
            iv.setBackgroundResource(resid[length-1]);
            list.add(iv);
            //中间位置依次对应
            for(int i=1;i<=length;i++){
                iv=new ImageView(activity);
                iv.setBackgroundResource(resid[i-1]);
                list.add(iv);
            }
            //末尾位置对应第一个视图
            iv=new ImageView(activity);
            iv.setBackgroundResource(resid[0]);
            list.add(iv);
        }
        //eg: size=3
        //index: 0 1 2 3 4
        //view:  3 1 2 3 1
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPoints(Activity activity, ViewGroup container, int resid, int number){
        if(activity==null||container==null||number<=0)return;
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(20,20);
        for(int i=0;i<number;i++){
            View iv=new View(activity);
            iv.setBackgroundResource(resid);
            lp.rightMargin=10;
            iv.setLayoutParams(lp);
            container.addView(iv,i);
        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      //Log.i(TAG,"onPageScrolled()->position:"+position);
    }

    @Override
    public void onPageSelected(int position) {
       // Log.i(TAG,"onPageSelected()->position:"+position);
        currentPosition=position;
        //如果，正在自动滑动,停下来等待.
        if(isAutoShow){
            stopAutoShow();
            startAutoShow(waitTime);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(state!=ViewPager.SCROLL_STATE_IDLE){return;}
        changeViewPagerState();
    }

    private void changeViewPagerState(){
        if(currentPosition<=0){ //最左边
            viewPager.setCurrentItem(size,true);
            description.setText(text[size-1]);
            pointer.getChildAt(size-1).setEnabled(false);
            pointer.getChildAt(oldChildId).setEnabled(true);
            oldChildId=size-1;//更新Id
        }else if(currentPosition>=size+1){ //最右边
            viewPager.setCurrentItem(1,true);
            description.setText(text[0]);
            pointer.getChildAt(0).setEnabled(false);
            pointer.getChildAt(oldChildId).setEnabled(true);
            oldChildId=0;
        }else{ //正常范围
            if(oldChildId==currentPosition-1) {
                if(!isAutoShow)
                Log.i(TAG,"视图没有发生改变! currentPostition:"+currentPosition+" odlChidId:"+oldChildId);
                return;
            }//滑动了，但没有改变视图
            description.setText(text[currentPosition-1]);
            pointer.getChildAt(currentPosition-1).setEnabled(false);//选中
            pointer.getChildAt(oldChildId).setEnabled(true);//正常
            oldChildId=currentPosition-1;
        }
        if(!isAutoShow)
        Log.i(TAG,"currentPostition:"+currentPosition+" odlChidId:"+oldChildId);
    }

    private void startAutoShow(long delay){
        isAutoShow=true;
        mh.sendEmptyMessageDelayed(AUTO_SHOW,delay);
    }
    private void stopAutoShow(){
        isAutoShow=false;
        mh.removeMessages(AUTO_SHOW);
    }
    private void scrollViewPager(boolean isToleft){
        if(isToleft){
            currentPosition -= 1;//向左
        }else{
            currentPosition += 1;//向右
        }
        if(currentPosition<0){
            currentPosition=size+1;
        }else if(currentPosition>size+1){
            currentPosition=0;
        }
        if(isAutoShow){
            viewPager.setCurrentItem(currentPosition,true);
            changeViewPagerState();
            mh.sendEmptyMessageDelayed(AUTO_SHOW,changeTime);
        }
    }

    @Override
    public void changeViewVisibility(int flag,Object args) {
            switch (flag){
                case FLAG_NOEMAL:
                   // if(isNormal)return;
                    isOnlyShowChildFragment(false);
                    top_viewpager.setVisibility(View.VISIBLE);
                    startAutoShow(1000);
                    isNormal=true;
                    break;
                case FLAG_ONLY_SHOW_HOME_FRAGMENT:
                   // if(!isNormal)return;
                    stopAutoShow();
                    isOnlyShowChildFragment(true);
                    top_viewpager.setVisibility(View.INVISIBLE);
                    isNormal=false;
                    break;
            }
    }

    @Override
    public void requestModeChange(int flag) {
        if(currentMode==flag)return;
        boolean needNormalState=false;
        switch (flag){
            case MODE_CHANGE_AUTO:
                show(autoModeFragment);
                currentMode=MODE_CHANGE_AUTO;
                needNormalState=autoModeFragment.isNormal();
                AbilityFragment.setManagerForFragment(autoModeFragment);
                break;
            case MODE_CHANGE_ADVANCED:
                show(advancedModeFragment);
                currentMode=MODE_CHANGE_ADVANCED;
                needNormalState=advancedModeFragment.isNormal();
                AbilityFragment.setManagerForFragment(advancedModeFragment);
                break;
        }
        if(needNormalState){
            changeViewVisibility(FLAG_NOEMAL,null);
        }else{
            changeViewVisibility(FLAG_ONLY_SHOW_HOME_FRAGMENT,null);
        }
    }
}
