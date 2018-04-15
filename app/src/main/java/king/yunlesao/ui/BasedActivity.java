package king.yunlesao.ui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;


/**
 * Created by King on 2018/1/3.
 * BasedActivity作为其他Activity的一个父类
 */

public class BasedActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener {


    protected FragmentManager fragmentManager;
    private final static String TAG="BasedActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager=getFragmentManager();
        Log.i(TAG,"onCreate() is called!");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.i(TAG,"onNavigationItemSelected() is called.");
        return false;
    }

    //设置没有标题的工具栏
    protected void setToolBarNoTitle(Toolbar toolbar){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        Log.i(TAG,"setToolBarTitle() is called.");
    }

   protected BasedFragment show(BasedFragment currentFragment,BasedFragment targetFragment,int resid){
        return BasedFragment.display(fragmentManager,currentFragment,targetFragment,resid);
   }

   protected void remove(BasedFragment target){
        BasedFragment.remove(fragmentManager,target);
   }
}
