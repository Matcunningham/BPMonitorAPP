package cecs343.bpmontor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Mat on 4/22/2018.
 */

public class MedChBoxRvAdapter extends RecyclerView.Adapter<MedChBoxRvAdapter.ItemViewHolder> {

    private String[] dataSet;
    private int layoutId;

    private OnItemCheckListener onItemClick;

    interface OnItemCheckListener {
        void onItemCheck(String med);
        void onItemUncheck(String med);
    }

    public MedChBoxRvAdapter(int layoutId, OnItemCheckListener onItemCheckListener){
        this.layoutId = layoutId;
        this.onItemClick = onItemCheckListener;

    }

    @Override
    public int getItemCount(){
        if(dataSet == null){
            return 0;
        }
        return dataSet.length; //size();
    }

    @Override
    public MedChBoxRvAdapter.ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        Context context = viewGroup.getContext();
        int layoutIdforListItem = layoutId;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdforListItem, viewGroup, false);
        return new MedChBoxRvAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        String dataElem = dataSet[position]; //.get(position);
        holder.medCheckItemView.setText(dataElem);

        //holder.check.setChecked(dataSet.get(position));
        holder.check.setTag(dataSet[position]); //.get(position));

        holder.check.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            CheckBox cb = (CheckBox) v;
            String med = (String) cb.getTag();

            if(((CheckBox) v).isChecked())
            {
                onItemClick.onItemCheck(med);

            }
            else
            {
                onItemClick.onItemUncheck(med);
            }
        }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView medCheckItemView;
        public CheckBox check;

        public ItemViewHolder(View itemView){
            super(itemView);

            medCheckItemView = (TextView) itemView.findViewById(R.id.medcheck_listitem);
            check = (CheckBox) itemView.findViewById(R.id.medcheckbox);
        }

    }

    public void setBpData(String[] data)
    {
        dataSet = data;
        notifyDataSetChanged();
    }
}
