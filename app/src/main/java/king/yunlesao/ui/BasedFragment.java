package king.yunlesao.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

/**
 * Created by King on 2018/1/3.
 */

public class BasedFragment extends Fragment {
    private static final String INDEX="_index_";
    private static final String NAME="_name_";
    private static final String DESCRIPTION="_description_";

    public static void mark(Fragment f,int index,String name,String description){
        Bundle args=new Bundle();
        args.putInt(INDEX, index);
        if(name!=null)
        args.putString(NAME,name);
        if(description!=null)
        args.putString(DESCRIPTION,description);
        f.setArguments(args);
    }
    public static void replaceFragment(FragmentManager fm,int layout_id, Fragment fragment){
        FragmentTransaction tr=fm.beginTransaction();
        tr.replace(layout_id,fragment);
        tr.commit();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void replaceFragment(int layout_id, Fragment fragment ){
        FragmentManager fm=getChildFragmentManager();
        replaceFragment(fm,layout_id,fragment);
    }
}
