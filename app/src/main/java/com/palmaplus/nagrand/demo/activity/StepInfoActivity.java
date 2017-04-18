package com.palmaplus.nagrand.demo.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.palmaplus.nagrand.demo.R;
import com.palmaplus.nagrand.demo.adapter.StepInfoAdapter;
import com.palmaplus.nagrand.demo.base.BaseActivity;
import com.palmaplus.nagrand.navigate.StepInfo;

import java.util.ArrayList;

import butterknife.BindView;
import in.workarounds.bundler.Bundler;
import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.bundler.annotations.Required;

@RequireBundler
public class StepInfoActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private StepInfoAdapter mStepInfoAdapter;

    @Arg
    @Required
    ArrayList<StepInfo> mStepInfos;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_step_info;
    }

    @Override
    protected void setView() {
        Bundler.inject(this);
        setTitle("路径详情");
        mStepInfoAdapter = new StepInfoAdapter(mStepInfos, StepInfoActivity.this);
        LinearLayoutManager manager = new LinearLayoutManager(StepInfoActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mStepInfoAdapter);
    }

    @Override
    protected void initData() {

    }

}
