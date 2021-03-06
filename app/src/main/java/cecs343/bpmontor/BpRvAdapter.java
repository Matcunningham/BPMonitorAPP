package cecs343.bpmontor;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Mat on 4/19/2018.
 */

// Adapter class for displaying list items in the recycler view
public class BpRvAdapter extends RecyclerView.Adapter<BpRvAdapter.ItemViewHolder> {
    private String[] dataSet;
    private int listItem;

    public BpRvAdapter(int layoutID){
        listItem = layoutID;
    }

    @Override
    public int getItemCount(){
        if(dataSet == null){
            return 0;
        }
        return dataSet.length;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        Context context = viewGroup.getContext();
        int layoutIdforListItem = listItem;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdforListItem, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        // Alternating row colors
        if(position % 2 == 0)
        {
            holder.bpItemView.setBackgroundColor(Color.parseColor("#8b8f94"));
        }
        else
        {
            holder.bpItemView.setBackgroundColor(Color.parseColor("#5e6266"));
        }

        String dataElem = dataSet[position];
        holder.bpItemView.setText(dataElem);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView bpItemView;

        public ItemViewHolder(View itemView){
            super(itemView);

            bpItemView = (TextView) itemView.findViewById(R.id.bp_item);
        }

    }

    public void setBpData(String[] data)
    {
        dataSet = data;
        notifyDataSetChanged();
    }
}
