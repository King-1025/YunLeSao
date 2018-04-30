package king.yunlesao.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import king.yunlesao.R;
import king.yunlesao.ui.iface.OnWordBarUpdate;
import king.yunlesao.utils.Tools;
import king.yunlesao.word.WordBar;
import king.yunlesao.word.WordBarManager;

/**
 * Created by King on 2018/4/28.
 */

public class ListWordBarView extends ListView implements AdapterView.OnItemClickListener ,View.OnClickListener{
    private Context context;
    private ArrayList<WordBar> wordBars;
    private WordBarManager wordBarManager;
    private boolean isInit;
    private WordBarAdapter wordBarAdapter;
    private TextView title;
    private EditText etDst;
    private EditText etNote;
    private TextView tvCreateDate;
    private WordBar currentWordBar;
    private AlertDialog dialog;
    private int currentWordBarCount;
    private OnWordBarUpdate onWordBarUpdate;

    public ListWordBarView(Context context){
        this(context,null);
    }

    public ListWordBarView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public ListWordBarView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.context=context;
        isInit=true;
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View dialogView=LayoutInflater.from(getContext()).inflate(R.layout.update_wordbar,null);
        dialogView.findViewById(R.id.motify).setOnClickListener(this);
        dialogView.findViewById(R.id.delete).setOnClickListener(this);
        dialogView.findViewById(R.id.cancel).setOnClickListener(this);
        title=(TextView)dialogView.findViewById(R.id.update_wordbar_title);
        etDst= (EditText) dialogView.findViewById(R.id.update_wordbar_dst_text);
        etNote= (EditText) dialogView.findViewById(R.id.update_wordbar_note_text);
        tvCreateDate= (TextView) dialogView.findViewById(R.id.createDate);
        builder.setView(dialogView);
        dialog=builder.create();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentWordBar=wordBars.get(position);
        title.setText(currentWordBar.getSrcText());
        tvCreateDate.setText("创建日期："+currentWordBar.getCreationDate());
        etDst.setText(currentWordBar.getDstText());
        etNote.setText(currentWordBar.getNote());
        dialog.show();
    }

    public void release(){
        if(dialog!=null){
            dialog.dismiss();
            dialog=null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.motify:
                String dst=etDst.getText().toString();
                if(Tools.checkValue(context,"译文",dst,false))return;
                String note=etNote.getText().toString();
                if(Tools.checkValue(context,"笔记",dst,true))return;
                currentWordBar.setDstText(dst);
                currentWordBar.setNote(note);
                wordBarManager.update(currentWordBar);
                update();
                Toast.makeText(context,"修改成功！",Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                wordBarManager.delete(currentWordBar);
                update();
                Toast.makeText(context,"删除成功！",Toast.LENGTH_SHORT).show();
                if(onWordBarUpdate!=null){
                    if(wordBars==null){
                        onWordBarUpdate.onUpdate(0,null);
                    }else{
                        onWordBarUpdate.onUpdate(wordBars.size(),wordBars);
                    }
                }
                break;
            case R.id.cancel:
                break;
        }
        dialog.hide();
    }

    class WordBarAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if(wordBars!=null){
                currentWordBarCount=wordBars.size();
            }else{
                currentWordBarCount=0;
            }
            return currentWordBarCount;
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
                item= LayoutInflater.from(getContext()).inflate(R.layout.word_bar_item,null);
            }else{
                item=convertView;
            }
            TextView title= (TextView) item.findViewById(R.id.wordbar_item_title);
            if(wordBars!=null){
                if(wordBars.get(position)!=null){
                    WordBar wb=wordBars.get(position);
                    title.setText("原文："+wb.getSrcText()+" 译文："+wb.getDstText());
                }else {
                    Toast.makeText(context,"position:"+position,Toast.LENGTH_SHORT).show();
                    title.setText("2");
                }
            }else {
                Toast.makeText(context,"wordBars:"+wordBars,Toast.LENGTH_SHORT).show();
                title.setText("1");
            }
            return item;
        }
    }

    public void update(){
        if(wordBarManager!=null){
            wordBars=wordBarManager.queryAll();
            if(isInit){
                isInit=false;
                wordBarAdapter=new WordBarAdapter();
                setAdapter(wordBarAdapter);
                setOnItemClickListener(this);
            }else{
                wordBarAdapter.notifyDataSetChanged();
            }
        }

        if(onWordBarUpdate!=null){
            if(wordBars==null){
                onWordBarUpdate.onUpdate(0,null);
            }else{
                onWordBarUpdate.onUpdate(wordBars.size(),wordBars);
            }
        }
    }

    public WordBarManager getWordBarManager() {
        return wordBarManager;
    }

    public void setWordBarManager(WordBarManager wordBarManager) {
        this.wordBarManager = wordBarManager;
    }

    public int getCurrentWordBarCount() {
        return currentWordBarCount;
    }

    public void setOnWordBarUpdate(OnWordBarUpdate onWordBarUpdate) {
        this.onWordBarUpdate = onWordBarUpdate;
    }
}
