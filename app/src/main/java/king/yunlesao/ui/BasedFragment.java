package king.yunlesao.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import butterknife.Unbinder;


/**
 * Created by King on 2018/1/3.
 * BasedFragment作为其他Fragment的父类
 */

public class BasedFragment extends Fragment {

    private int index;
    private String name;
    private String description;
    protected Activity mActivity;
    protected Unbinder unbinder;
    private final static String STAT_SAVE_IS_HIDDEN="STAT_SAVE_IS_HIDDEN";
    private final static  String TAG="BasedFragment";

    public final static int HOME_FRAGMENT=0x00;
    public final static int HISTORY_FRAGMENT=0x01;
    public final static int ADVANCED_MODE_FRAGMENT=0x02;
    public final static int AUTO_MODE_FRAGMENT=0x03;

    public static BasedFragment makeFragment(int type){
        return makeFragment (type,0,null,null);
    }

    public static BasedFragment makeFragment (int type,int ndex,String name,String description) {
        BasedFragment bf=null;
        switch(type){
            case HOME_FRAGMENT:
                bf=new HomeFragment();
                break;
            case HISTORY_FRAGMENT:
                bf=new HistoryFragment();
                break;
            case ADVANCED_MODE_FRAGMENT:
                bf=new AdvancedModeFragment();
                break;
            case AUTO_MODE_FRAGMENT:
                bf=new AutoModeFragment();
                break;
            default:Log.e(TAG,"not found fragment of type:"+type);
        }
        if(bf!=null) {
            bf.index = ndex;
            bf.name = name;
            bf.description = description;
        }
        return bf;
    }
    public static BasedFragment display(FragmentManager fr,BasedFragment currentFragment,BasedFragment targetFragment,int resid){
        if(fr==null||targetFragment==null)return currentFragment;
        FragmentTransaction ftr=fr.beginTransaction();
        if(currentFragment!=null) {
            if(currentFragment.equals(targetFragment))return currentFragment;
            ftr.hide(currentFragment);
            if(targetFragment.isAdded()){
                if(targetFragment.isHidden())ftr.show(targetFragment);
            }else{
                ftr.add(resid,targetFragment,targetFragment.getName());
            }
        }else{
            ftr.add(resid,targetFragment,targetFragment.getName());
        }
        ftr.commit();
        return targetFragment;
    }

    public static void remove(FragmentManager fr,BasedFragment target){
        if(fr==null||target==null)return;
        if(!target.isAdded())return;
        FragmentTransaction ftr=fr.beginTransaction();
        ftr.remove(target);
        ftr.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity= (Activity) context;
        Log.i(TAG," onAttach() is called.");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            boolean isHidden=savedInstanceState.getBoolean(STAT_SAVE_IS_HIDDEN);
            FragmentTransaction ft=getFragmentManager().beginTransaction();
            if(isHidden){
                ft.hide(this);
            }else{
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STAT_SAVE_IS_HIDDEN,isHidden());
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
