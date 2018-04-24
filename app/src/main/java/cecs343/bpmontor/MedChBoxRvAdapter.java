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
    public List<String> medsSelected;

    private OnItemCheckListener onItemClick;

    interface OnItemCheckListener {
        void onItemCheck(String med);
        void onItemUncheck(String med);
    }

    public MedChBoxRvAdapter(List<String> meds, OnItemCheckListener onItemCheckListener){
        this.medsSelected = meds;
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
        int layoutIdforListItem = R.layout.medcheckbox_list_item;
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
            // TODO ^ <> return a string with the value so i can query database
            //Intent i = new Intent("custom-message");
            //https://stackoverflow.com/questions/33434626/get-list-of-checked-checkboxes-from-recyclerview-android
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
