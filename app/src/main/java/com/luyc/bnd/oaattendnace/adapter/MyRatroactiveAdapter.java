package com.luyc.bnd.oaattendnace.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luyc.bnd.oaattendnace.R;
import com.luyc.bnd.oaattendnace.view.MyTextCircleView;

import java.util.ArrayList;

/**
 * Created by admin on 2017/9/15.
 */

public class MyRatroactiveAdapter extends RecyclerView.Adapter<MyRatroactiveAdapter.MyViewHolder> {

    private final Context context;
    private final ArrayList<String> mList;

    public MyRatroactiveAdapter(Context context, ArrayList<String> mList) {
        this.context = context;
        this.mList = mList;

    }

    private RecycleViewAdapter.onRecycleViewItemClick listener;

    public interface onRecycleViewItemClick {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnRecyclerViewItemClick(RecycleViewAdapter.onRecycleViewItemClick listener) {
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_retroactive, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.myTextCV.setPaintStyle(position, "Ma");

        holder.tvAttendance.setText("缺勤" + mList.get(position).toString());

        if (listener != null) {
            holder.ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(view, position);
                }
            });
            holder.ll_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClick(view, position);
                    return true;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private MyTextCircleView myTextCV;
        private final TextView tvAttendance;
        private final TextView tvTime;
        private final LinearLayout ll_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            ll_item = ((LinearLayout) itemView.findViewById(R.id.ll_item));
            myTextCV = ((MyTextCircleView) itemView.findViewById(R.id.mytcv));
            tvAttendance = ((TextView) itemView.findViewById(R.id.tv_attendance));
            tvTime = ((TextView) itemView.findViewById(R.id.tv_time));
        }
    }

}
