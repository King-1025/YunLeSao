package king.yunlesao.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import king.yunlesao.R;

/**
 * Created by King on 2018/4/30.
 */

public class UsingHelpActivity extends BasedActivity {
    private ListView listView;
    private final static int steps[]={R.string.step_1,R.string.step_2,R.string.step_3};
    private final static int pictures[]={R.drawable.image_1,R.drawable.image_2,R.drawable.image_3};
    private int count;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_using_help);
        listView= (ListView) findViewById(R.id.using_help_list);
        if(steps.length>pictures.length){
            count=pictures.length;
        }else {
            count=steps.length;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        listView.setAdapter(new SimpleAdapter());
    }

    class SimpleAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item;
            if(convertView==null){
                item=getLayoutInflater().inflate(R.layout.simple_item,null);
            }else{
                item=convertView;
            }
            TextView step= (TextView) item.findViewById(R.id.simple_step);
            ImageView picture= (ImageView) item.findViewById(R.id.simple_picture);
            step.setText(steps[position]);
            picture.setBackgroundResource(pictures[position]);
            return item;
        }
    }
}
