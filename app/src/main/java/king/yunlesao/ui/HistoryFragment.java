package king.yunlesao.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import king.yunlesao.R;
import king.yunlesao.ui.iface.OnResetHistoryListener;
import king.yunlesao.word.History;
import king.yunlesao.word.HistoryManager;
import king.yunlesao.word.WordBar;

/**
 * Created by King on 2018/1/3.
 * 历史记录碎片
 */

public class HistoryFragment extends BasedFragment implements AdapterView.OnItemClickListener,OnResetHistoryListener{

    private LinearLayout control;
    private ListView listView;
    private Button back;
    private HistoryManager historyManager;
    private ArrayList<History> histories;
    private ArrayList<WordBar> wordBars;
    public final static int FLAG_HISTORY_LIST=0x01;
    public final static int FLAG_DETAILS_LIST=0x02;
    private BaseAdapter currentAdapter;
    private TextView lable;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
        historyManager=new HistoryManager(mActivity);
        HistoryManager.setOnResetHistoryListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.history_fragment,container,false);
        listView= (ListView) view.findViewById(R.id.show_list);
        control=(LinearLayout)view.findViewById(R.id.history_control);
        lable=(TextView)view.findViewById(R.id.lable_without_history);
        back=(Button)view.findViewById(R.id.history_back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(FLAG_HISTORY_LIST);
            }
        });
        show(FLAG_HISTORY_LIST);
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(historyManager!=null){
            historyManager.release();
        }
    }

    public void show(int flag){
        switch (flag){
            case FLAG_HISTORY_LIST:
                isOnlyShowChildFragment(false);
                control.setVisibility(View.INVISIBLE);
                listView.setAdapter(getHistoryAdapter());
                listView.setOnItemClickListener(this);
                break;
            case FLAG_DETAILS_LIST:
                isOnlyShowChildFragment(true);
                control.setVisibility(View.VISIBLE);
                listView.setAdapter(getDetailsAdapter());
                listView.setOnItemClickListener(null);
                listView.setVisibility(View.VISIBLE);
                lable.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            show(FLAG_HISTORY_LIST);
        }
        //Toast.makeText(mActivity,"hidden:"+hidden,Toast.LENGTH_SHORT).show();
    }

    private BaseAdapter getHistoryAdapter(){
        histories=historyManager.queryAll();
        if(histories==null||(histories!=null&&histories.size()<=0)){
            listView.setVisibility(View.INVISIBLE);
            lable.setVisibility(View.VISIBLE);
            //Toast.makeText(mActivity,"history is "+histories,Toast.LENGTH_SHORT).show();
        }else{
            listView.setVisibility(View.VISIBLE);
            lable.setVisibility(View.INVISIBLE);
            //Toast.makeText(mActivity,"hidden is ok !",Toast.LENGTH_SHORT).show();
        }
        updateAdaper(FLAG_HISTORY_LIST);
        return currentAdapter;
    }

    private BaseAdapter getDetailsAdapter(){
        updateAdaper(FLAG_DETAILS_LIST);
        return currentAdapter;
    }

    private void updateAdaper(int flag){
            switch (flag){
                case FLAG_HISTORY_LIST:
                    currentAdapter=new HistoryAdapter();
                    break;
                case FLAG_DETAILS_LIST:
                    currentAdapter=new DetailsAdapter();
                    break;
            }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        History history=histories.get(position);
        if(history!=null){
            wordBars=historyManager.queryWordBar(history.getId(),history.getDate());
        }else{
            Toast.makeText(mActivity,"history is "+history,Toast.LENGTH_SHORT).show();
        }
        show(FLAG_DETAILS_LIST);
    }

    @Override
    public void reset() {
        show(FLAG_HISTORY_LIST);
    }

    class HistoryAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if(histories!=null){
                return histories.size();
            }else{
                return 0;
            }
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
                item= LayoutInflater.from(mActivity).inflate(R.layout.history_item,null);
            }else{
                item=convertView;
            }
            TextView time=(TextView) item.findViewById(R.id.history_time);
            TextView number=(TextView) item.findViewById(R.id.history_wordbar_number);
            time.setText(histories.get(position).getDate());
            number.setText(histories.get(position).getWordNumber()+"条");
            return item;
        }
    }

    class DetailsAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if(wordBars!=null){
                return wordBars.size();
            }else{
                return 0;
            }
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
                item= LayoutInflater.from(mActivity).inflate(R.layout.details_item,null);
            }else{
                item=convertView;
            }
            TextView createDate= (TextView) item.findViewById(R.id.detatils_createDate);
            TextView srcText= (TextView) item.findViewById(R.id.details_wordbar_src_text);
            TextView dstText=(TextView) item.findViewById(R.id.details_wordbar_dst_text);
            TextView noteText= (TextView) item.findViewById(R.id.details_wordbar_note_text);
            WordBar wb=wordBars.get(position);
            if(wb!=null){
                createDate.setText(wb.getCreationDate());
                srcText.setText(wb.getSrcText());
                dstText.setText(wb.getDstText());
                noteText.setText(wb.getNote());
            }
            return item;
        }
    }
}
