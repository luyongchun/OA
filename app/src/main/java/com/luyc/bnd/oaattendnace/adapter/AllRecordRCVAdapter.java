package com.luyc.bnd.oaattendnace.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luyc.bnd.oaattendnace.R;

import java.util.ArrayList;

/**
 * Created by admin on 2017/9/1.
 */

public class AllRecordRCVAdapter extends RecyclerView.Adapter<AllRecordRCVAdapter.MyViewHolder>{


    private final Context context;
    private ArrayList<String> userNames =new ArrayList<>();


    private RecycleViewAdapter.onRecycleViewItemClick listener;
    public interface onRecycleViewItemClick{
        void onItemClick(View view, int position);
        void onItemLongClick(View view,int position);
    }
    public void setOnRecyclerViewItemClick(RecycleViewAdapter.onRecycleViewItemClick listener){
        this.listener = listener;
    }

    public AllRecordRCVAdapter(Context context, ArrayList<String> userNames) {
        this.context = context;
        this.userNames =userNames;
    }

    @Override
    public AllRecordRCVAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_allrecord, parent, false);
        AllRecordRCVAdapter.MyViewHolder holder = new AllRecordRCVAdapter.MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(AllRecordRCVAdapter.MyViewHolder holder, final int position) {

        holder.userName.setText(userNames.get(position).toString());

        if (listener !=null){
            holder.layout_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(view,position);
                }
            });
            holder.layout_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClick(view,position);
                    return true;
                }
            });


        }


    }

    @Override
    public int getItemCount() {
        if (userNames !=null){
            return userNames.size();
        }else {
            return 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView userName;

        private final TextView endTime;
        private final TextView startTime;
        private final ImageView userIamge;
        private final LinearLayout layout_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            userName = ((TextView) itemView.findViewById(R.id.tv_userName));

            endTime = ((TextView) itemView.findViewById(R.id.tv_endTime));
            startTime = ((TextView) itemView.findViewById(R.id.tv_startTime));
            layout_item = ((LinearLayout) itemView.findViewById(R.id.ll_item));
            userIamge = ((ImageView) itemView.findViewById(R.id.iv_userIamge));


        }
    }

}
