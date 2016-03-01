package pedidos;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.italo_view.R;

public class ExandableAdapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater;
    private ArrayList<Pedidos_parent> mParent;
 
    public ExandableAdapter(Context context, ArrayList<Pedidos_parent> parent){
        mParent = parent;
        inflater = LayoutInflater.from(context);
    }
 
    @Override
    public int getGroupCount() {
        return mParent.size();
    }
 
    @Override
    public int getChildrenCount(int i) {
        return mParent.get(i).getArrayChildren().size();
    }
 
    @Override
    public Object getGroup(int i) {
        return mParent.get(i).getTitle();
    }
 
    @Override
    public Object getChild(int i, int i1) {
        return mParent.get(i).getArrayChildren().get(i1);
    }
 
    @Override
    public long getGroupId(int i) {
        return i;
    }
 
    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }
 
    @Override
    public boolean hasStableIds() {
        return true;
    }
 
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        if (view == null) {
            view = inflater.inflate(R.layout.item_expandable_group, viewGroup,false);
        }
        final TextView textView = (TextView) view.findViewById(R.id.group_title);
        textView.setText(getGroup(i).toString());
        view.setTag(holder);
        return view;
    }
 
    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
    	ViewHolder holder = new ViewHolder();
        holder.childPosition = i1;
        holder.groupPosition = i;
        if (view == null) {
            view = inflater.inflate(R.layout.item_expandable_item, viewGroup,false);
        }
        final TextView textView = (TextView) view.findViewById(R.id.child_text);
        textView.setText(mParent.get(i).getArrayChildren().get(i1).getDescripcion());
        view.setTag(holder);
        return view;
    }
 
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
 
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    public void onClick(View view) {
        ViewHolder holder = (ViewHolder)view.getTag();
        if (view.getId() == holder.button.getId()){}
    }

    protected class ViewHolder {
        protected int childPosition;
        protected int groupPosition;
        protected Button button;
    }
}
