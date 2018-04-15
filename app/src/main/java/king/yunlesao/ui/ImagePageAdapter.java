package king.yunlesao.ui;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by King on 2018/3/27.
 */

public class ImagePageAdapter extends PagerAdapter {
    private ArrayList<ImageView> list;
    private int size;
    private final static String TAG="ImagePageAdapter";
    public ImagePageAdapter(ArrayList<ImageView> imageLsit){
        list=imageLsit;
        if(list!=null){
            size=list.size();
        }
    }
    @Override
    public int getCount() {
        return size;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
       // Log.i(TAG,"position:"+position);
        ImageView iv=list.get(position);
        container.addView(iv);
        return iv;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
