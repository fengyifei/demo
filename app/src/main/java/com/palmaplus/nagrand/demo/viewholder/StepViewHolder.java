package com.palmaplus.nagrand.demo.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.palmaplus.nagrand.demo.R;
import com.palmaplus.nagrand.demo.utils.MapUtils;
import com.palmaplus.nagrand.navigate.DynamicNavigateAction;
import com.palmaplus.nagrand.navigate.StepInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lchad on 2016/12/21.
 * Github: https://github.com/lchad
 */

public class StepViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.top_line)
    View mTopLine;
    @BindView(R.id.img_action)
    ImageView mImgAction;
    @BindView(R.id.left_container)
    RelativeLayout mLeftContainer;
    @BindView(R.id.count)
    TextView mCount;
    @BindView(R.id.action)
    TextView mAction;
    @BindView(R.id.arrive_line)
    View mArriveLine;

    public StepViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(StepInfo stepInfo) {
        if (stepInfo == null) {
            return;
        }
        int length = (int) stepInfo.mLength;
        if (length == 0) {
            length = 1;
        }

        if (stepInfo.mAction == DynamicNavigateAction.ACTION_ARRIVE) {//如果是到达的类型,不显示图标.
            mImgAction.setVisibility(View.GONE);
            mArriveLine.setVisibility(View.VISIBLE);
        } else {
            int resource = MapUtils.getMapActionNormalResource(stepInfo);
            if (resource == 0) {
                mImgAction.setVisibility(View.GONE);
                mArriveLine.setVisibility(View.VISIBLE);
            } else {
                mImgAction.setVisibility(View.VISIBLE);
                mArriveLine.setVisibility(View.GONE);
                mImgAction.setImageResource(resource);
            }
        }
        mCount.setText("" + length + "");
        mAction.setText(MapUtils.getMapActionString(stepInfo.mAction));
    }
}
