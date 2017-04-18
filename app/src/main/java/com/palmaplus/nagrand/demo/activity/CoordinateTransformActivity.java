package com.palmaplus.nagrand.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.palmap.widget.Compass;
import com.palmaplus.nagrand.core.Types;
import com.palmaplus.nagrand.data.DataSource;
import com.palmaplus.nagrand.data.DataUtil;
import com.palmaplus.nagrand.data.PlanarGraph;
import com.palmaplus.nagrand.demo.R;
import com.palmaplus.nagrand.demo.base.BaseActivity;
import com.palmaplus.nagrand.demo.utils.ToastUtils;
import com.palmaplus.nagrand.view.MapView;
import com.palmaplus.nagrand.view.gestures.OnSingleTapListener;
import com.palmaplus.nagrand.view.gestures.OnZoomListener;

import butterknife.BindView;

/**
 * Created by lchad on 2016/11/1.
 * Github: https://github.com/lchad
 * 演示坐标转换
 */
public class CoordinateTransformActivity extends BaseActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, CoordinateTransformActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.map_view)
    MapView mMapView;
    @BindView(R.id.spinner)
    Spinner mSpinner;
    @BindView(R.id.map_overlay_container)
    RelativeLayout mMapOverlayContainer;
    @BindView(R.id.screen_coo)
    TextView mScreenCoo;
    @BindView(R.id.al_la_coo)
    TextView mAlLaCoo;
    @BindView(R.id.plan_coo)
    TextView mPlanCoo;
    @BindView(R.id.compass)
    Compass mCompass;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_coordinate_transform;
    }

    @Override
    protected void setView() {
        setTitle(R.string.coordinate_transform);
        showProgressDialog(R.string.map_loading);
        mMapView.start();
        mSpinner.setVisibility(View.GONE);
        mMapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(MapView mapView, float x, float y) {
                Types.Point worldPoint = mMapView.converToWorldCoordinate(x, y);
                float[] alLaCoo = DataUtil.webMercator2lonLat(worldPoint.x, worldPoint.y);
                mPlanCoo.setText(getString(R.string.plan_coo) + "\n" + "(" + worldPoint.x + "," +
                        worldPoint.y + ")");
                mScreenCoo.setText(getString(R.string.screen_coo) + "\n" + "(" + x + "," + y + ")");
                mAlLaCoo.setText(getString(R.string.al_la_coo) + "\n" + "(" + alLaCoo[0] + "," +
                        alLaCoo[1] + ")");
            }
        });
        mMapView.setOnZoomListener(new OnZoomListener() {
            @Override
            public void preZoom(MapView mapView, float v, float v1) {

            }

            @Override
            public void onZoom(MapView mapView, boolean b) {

            }

            @Override
            public void postZoom(MapView mapView, float v, float v1) {
                //刷新罗盘
                mCompass.invalidate();
            }
        });
    }

    @Override
    protected void initData() {
        mMapView.setMapOptions(mMapOptions);
        mMapView.setOverlayContainer(mMapOverlayContainer);

        /**
         * 根据floorId，加载地图数据
         */
        mDataSource.requestPlanarGraph(186677, new DataSource
                .OnRequestDataEventListener<PlanarGraph>() {
            @Override
            public void onRequestDataEvent(DataSource.ResourceState state, PlanarGraph planarGraph) {
                hideProgressDialog();
                if (state != DataSource.ResourceState.ok) {
                    ToastUtils.showToast(mHandler, R.string.map_load_fail);
                    return;
                }

                mMapView.drawPlanarGraph(planarGraph);

                mCompass.setMapView(mMapView);
            }
        });
    }


}
