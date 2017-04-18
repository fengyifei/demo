package com.palmaplus.nagrand.demo.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.palmaplus.nagrand.demo.R;
import com.palmaplus.nagrand.navigate.StepInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lchad on 2016/12/21.
 * Github: https://github.com/lchad
 */

public class StepEndViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.top_line)
    View mTopLine;
    @BindView(R.id.red_dot)
    ImageView mRedDot;
    @BindView(R.id.left_container)
    RelativeLayout mLeftContainer;
    @BindView(R.id.terminal)
    TextView mTerminal;

    public StepEndViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(StepInfo stepInfo) {
        if (stepInfo == null) {
            return;
        }
        mTerminal.setText(stepInfo.mActionName);
    }
}
