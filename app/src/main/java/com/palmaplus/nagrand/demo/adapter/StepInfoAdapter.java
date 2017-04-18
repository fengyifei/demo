package com.palmaplus.nagrand.demo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.palmaplus.nagrand.demo.R;
import com.palmaplus.nagrand.demo.viewholder.StepEndViewHolder;
import com.palmaplus.nagrand.demo.viewholder.StepStartViewHolder;
import com.palmaplus.nagrand.demo.viewholder.StepViewHolder;
import com.palmaplus.nagrand.navigate.StepInfo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by lchad on 2016/12/21.
 * Github: https://github.com/lchad
 */

public class StepInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private List<StepInfo> mStepInfos = new ArrayList<>();

    private Context mContext;

    private OnRecyclerViewItemClickListener mOnItemClickListener;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (int) v.getTag();
            mOnItemClickListener.onItemClick(v, pos);
        }
    };

    public StepInfoAdapter(List<StepInfo> stepInfos, Context context) {
        mStepInfos = stepInfos;
        mContext = context;
    }

    @SuppressLint("InflateParams")
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case StepType.START:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_step_start, null);
                view.setOnClickListener(mOnClickListener);
                return new StepStartViewHolder(view);
            case StepType.STEP:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_step_step, null);
                view.setOnClickListener(mOnClickListener);
                return new StepViewHolder(view);
            case StepType.END:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_step_end, null);
                view.setOnClickListener(mOnClickListener);
                return new StepEndViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case StepType.START:
                StepStartViewHolder start = (StepStartViewHolder) holder;
                holder.itemView.setTag(position);
                start.bind(mStepInfos.get(position));
                break;
            case StepType.STEP:
                StepViewHolder step = (StepViewHolder) holder;
                holder.itemView.setTag(position);
                step.bind(mStepInfos.get(position));
                break;
            case StepType.END:
                StepEndViewHolder end = (StepEndViewHolder) holder;
                holder.itemView.setTag(position);
                end.bind(mStepInfos.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mStepInfos == null) {
            return 0;
        }
        return mStepInfos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mStepInfos.size() == 0) {
            return 0;
        }
        if (position == 0) {
            return StepType.START;
        }
        if (position == mStepInfos.size() -1) {
            return StepType.END;
        }
        return StepType.STEP;
    }


    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }


    /**
     * 分段信息item类型
     */
    @IntDef({StepType.START, StepType.STEP, StepType.END})
    @Retention(RetentionPolicy.SOURCE)
    @interface StepType {
        /**
         * 开始
         */
        int START = 0;
        /**
         * 分段
         */
        int STEP = 1;
        /**
         * 结束
         */
        int END = 2;

    }
}
