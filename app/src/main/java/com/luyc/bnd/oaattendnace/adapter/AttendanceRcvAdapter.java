package com.luyc.bnd.oaattendnace.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luyc.bnd.oaattendnace.R;

import java.util.ArrayList;

/**
 * Created by admin on 2017/9/6.
 */

public class AttendanceRcvAdapter extends RecyclerView.Adapter<AttendanceRcvAdapter.MyViewHolder>  {

    private static final String TAG = "MatterRcvAdapter";
    private final Context context;
    private final ArrayList<String> list;

    public AttendanceRcvAdapter(Context context, ArrayList<String> list) {
        this.context =context;
        this.list = list;
    }

    @Override
    public AttendanceRcvAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rcv_attendance, parent, false);
        AttendanceRcvAdapter.MyViewHolder holder = new AttendanceRcvAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AttendanceRcvAdapter.MyViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder: "+1111 );

    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private final ImageView ivAttend;
        private final TextView tvCompanyWorkTime;
        private final TextView tvAddress;
        private final TextView tvLook;
        private final TextView tvAttendTime;
        private final TextView tvUpdataAttend;

        public MyViewHolder(View view)
        {
            super(view);
            ivAttend = ((ImageView) view.findViewById(R.id.iv_attend));
            tvCompanyWorkTime = ((TextView) view.findViewById(R.id.tv_company_work_time));
            tvAttendTime = ((TextView) view.findViewById(R.id.tv_attendance_time));
            tvAddress = ((TextView) view.findViewById(R.id.tv_address));
            tvLook = ((TextView) view.findViewById(R.id.tv_look));
            tvUpdataAttend = ((TextView) view.findViewById(R.id.tv_updata_attend));

        }
    }

}
