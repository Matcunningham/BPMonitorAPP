package cecs343.bpmontor;

/**
 * Created by Mat on 4/26/2018.
 */

import android.content.Context;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RadioRecyclerAdapter extends RecyclerView.Adapter<RadioRecyclerAdapter.ItemViewHolder> {

private String[] dataSet;
private List<Integer> idData;
private List<String> nameData;
private int lastSelectedPos = -1;
private RadioButton lastChecked = null;

private OnItemCheckListener onItemClick;

interface OnItemCheckListener {
    void onItemCheck(int patient, String name);
}

    public RadioRecyclerAdapter( OnItemCheckListener onItemCheckListener){
        this.onItemClick = onItemCheckListener;

    }

    @Override
    public int getItemCount(){
        if(dataSet == null){
            return 0;
        }
        return dataSet.length;
    }

    @Override
    public RadioRecyclerAdapter.ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        Context context = viewGroup.getContext();
        int layoutIdforListItem = R.layout.sp_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdforListItem, viewGroup, false);
        return new RadioRecyclerAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        // Alternating row colors
        if(position % 2 == 0)
        {
            holder.patItemView.setBackgroundColor(Color.parseColor("#8b8f94"));
        }
        else
        {
            holder.patItemView.setBackgroundColor(Color.parseColor("#5e6266"));
        }
        String dataElem = dataSet[position];
        final String nameElem = nameData.get(position);

        String textToSet = nameElem + "\n\t\t" + dataElem;

        if(position == 0)
        {
            holder.patItemView.setBackgroundColor(Color.parseColor("#428bca"));
            textToSet = textToSet + "\t\t\t(ME)";
        }

        holder.patItemView.setText(textToSet);



        final int pid = idData.get(position);


        holder.rButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RadioButton rb = (RadioButton) v;
                int clickedPos = position;

                if(((RadioButton) rb).isChecked())
                {
                    if(lastChecked != null)
                    {
                        lastChecked.setChecked(false);
                    }
                    onItemClick.onItemCheck(pid, nameElem);
                    lastChecked = rb;
                    lastSelectedPos = clickedPos;
                }

            }
        });
    }

class ItemViewHolder extends RecyclerView.ViewHolder {
    public TextView patItemView;
    public RadioButton rButton;

    public ItemViewHolder(View itemView){
        super(itemView);

        patItemView = (TextView) itemView.findViewById(R.id.sp_listitem);
        rButton = (RadioButton) itemView.findViewById(R.id.patient_radio);
    }

}

    public void setPatData(String[] data, List pids, List names)
    {
        dataSet = data;
        idData = pids;
        nameData = names;
        notifyDataSetChanged();
    }
}
