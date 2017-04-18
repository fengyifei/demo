package com.palmaplus.nagrand.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.palmap.widget.Compass;
import com.palmap.widget.Scale;
import com.palmaplus.nagrand.data.DataSource;
import com.palmaplus.nagrand.data.LocationList;
import com.palmaplus.nagrand.data.LocationModel;
import com.palmaplus.nagrand.data.MapModel;
import com.palmaplus.nagrand.data.PlanarGraph;
import com.palmaplus.nagrand.demo.R;
import com.palmaplus.nagrand.demo.base.BaseActivity;
import com.palmaplus.nagrand.demo.constants.Constant;
import com.palmaplus.nagrand.demo.utils.ToastUtils;
import com.palmaplus.nagrand.view.MapView;
import com.palmaplus.nagrand.view.adapter.DataListAdapter;
import com.palmaplus.nagrand.view.gestures.OnZoomListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
/**
 * Created by lchad on 2016/11/1.
 * Github: https://github.com/lchad
 * 演示多建筑物
 */
public class MultiBuildingActivity extends BaseActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MultiBuildingActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.map_view)
    MapView mMapView;
    @BindView(R.id.spinner)
    Spinner mSpinner;
    @BindView(R.id.map_overlay_container)
    RelativeLayout mMapOverlayContainer;
    @BindView(R.id.building_one)
    Button mBuildingOne;
    @BindView(R.id.building_two)
    Button mBuildingTwo;
    @BindView(R.id.container)
    LinearLayout mContainer;
    @BindView(R.id.compass)
    Compass mCompass;
    @BindView(R.id.architect)
    Scale mScale;
    @BindView(R.id.all)
    Button mAll;

    private long allId = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_multi_building;
    }

    @Override
    protected void setView() {
        setTitle(R.string.multi_building);
        mMapView.start();
        showProgressDialog();
        mMapView.setOnZoomListener(new OnZoomListener() {
            @Override
            public void preZoom(MapView mapView, float v, float v1) {

            }

            @Override
            public void onZoom(MapView mapView, boolean b) {

            }

            @Override
            public void postZoom(MapView mapView, float v, float v1) {
                mCompass.invalidate();
                mScale.invalidate();
            }
        });

    }

    @Override
    protected void initData() {
        mDataSource = new DataSource(Constant.APP_KEY);
        mAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allId != -1)
                    mDataSource.requestPlanarGraph(allId, new DataSource.OnRequestDataEventListener<PlanarGraph>() {
                        @Override
                        public void onRequestDataEvent(DataSource.ResourceState resourceState, PlanarGraph planarGraph) {
                            mMapView.drawPlanarGraph(planarGraph);


                        }
                    });
            }
        });
        mMapView.setMapOptions(mMapOptions);
        mMapView.setOverlayContainer(mMapOverlayContainer);
        mDataSource.requestMap(Constant.MULTI_BUILDING_ID, new DataSource.OnRequestDataEventListener<MapModel>() {
            @Override
            public void onRequestDataEvent(DataSource.ResourceState resourceState, final MapModel mapModel) {
                hideProgressDialog();
                if (resourceState != DataSource.ResourceState.ok) {
                    return;
                }

                mDataSource.requestPOI(MapModel.POI.get(mapModel), new DataSource.OnRequestDataEventListener<LocationModel>() {
                    @Override
                    public void onRequestDataEvent(DataSource.ResourceState resourceState, LocationModel locationModel) {
                        allId = LocationModel.id.get(locationModel);
                        mDataSource.requestPlanarGraph(LocationModel.id.get(locationModel), new DataSource.OnRequestDataEventListener<PlanarGraph>() {
                            @Override
                            public void onRequestDataEvent(DataSource.ResourceState resourceState, PlanarGraph planarGraph) {
                                mMapView.drawPlanarGraph(planarGraph);

                            }
                        });
                    }
                });

                mDataSource.requestPOIChildren(MapModel.POI.get(mapModel), new DataSource.OnRequestDataEventListener<LocationList>() {
                    @Override
                    public void onRequestDataEvent(DataSource.ResourceState resourceState, LocationList locationList) {
                        if (resourceState != DataSource.ResourceState.ok) {
                            ToastUtils.showToast(mHandler, R.string.map_load_fail);
                            return;
                        }
                        final List<LocationModel> lists = new ArrayList<>();
                        for (int i = 0; i < locationList.getSize(); i++) {
                            lists.add(locationList.getPOI(i));
                        }
                        final long buildingOneId = LocationModel.id.get(lists.get(0));
                        final long buildingTwoId = LocationModel.id.get(lists.get(1));
                        mBuildingOne.setText(LocationModel.name.get(lists.get(0)).trim());
                        mBuildingOne.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestBuilding(buildingOneId);
                            }
                        });
                        mBuildingTwo.setText(LocationModel.name.get(lists.get(1)).trim());
                        mBuildingTwo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestBuilding(buildingTwoId);
                            }
                        });
                    }

                });
            }
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LocationModel item = (LocationModel) parent.getAdapter().getItem(position);
                /**
                 * 根据floorId，加载地图数据
                 */
                showProgressDialog(R.string.map_loading);
                mDataSource.requestPlanarGraph(LocationModel.id.get(item), new DataSource.OnRequestDataEventListener<PlanarGraph>() {
                    @Override
                    public void onRequestDataEvent(DataSource.ResourceState state, PlanarGraph planarGraph) {
                        hideProgressDialog();
                        if (state != DataSource.ResourceState.ok) {
                            ToastUtils.showToast(mHandler, R.string.map_load_fail);
                            return;
                        }

                        mMapView.drawPlanarGraph(planarGraph);

                        mCompass.setMapView(mMapView);
                        mScale.setMapView(mMapView);
                    }
                });
            }
        });
    }

    /**
     * 请求building数据
     */
    private void requestBuilding(long id) {
        mDataSource.requestPOIChildren(id, new DataSource.OnRequestDataEventListener<LocationList>() {
            @Override
            public void onRequestDataEvent(DataSource.ResourceState resourceState, LocationList locationList) {
                if (resourceState != DataSource.ResourceState.ok) {
                    ToastUtils.showToast(R.string.map_load_fail);
                    return;
                }
                DataListAdapter<LocationModel> floorAdapter = new DataListAdapter<>(
                        MultiBuildingActivity.this,
                        android.R.layout.simple_spinner_item, locationList,
                        Constant.FLOOR_SHOW_FIELD);

                floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(floorAdapter);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.drop();
        }
    }
}
