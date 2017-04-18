package com.palmaplus.nagrand.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.palmap.widget.Compass;
import com.palmap.widget.Scale;
import com.palmaplus.nagrand.core.Types;
import com.palmaplus.nagrand.core.Value;
import com.palmaplus.nagrand.data.BasicElement;
import com.palmaplus.nagrand.data.DataSource;
import com.palmaplus.nagrand.data.DataUtil;
import com.palmaplus.nagrand.data.Feature;
import com.palmaplus.nagrand.data.FeatureCollection;
import com.palmaplus.nagrand.data.FloorModel;
import com.palmaplus.nagrand.data.LocationList;
import com.palmaplus.nagrand.data.LocationModel;
import com.palmaplus.nagrand.data.LocationPagingList;
import com.palmaplus.nagrand.data.MapElement;
import com.palmaplus.nagrand.data.MapModel;
import com.palmaplus.nagrand.data.PlanarGraph;
import com.palmaplus.nagrand.demo.R;
import com.palmaplus.nagrand.demo.base.BaseActivity;
import com.palmaplus.nagrand.demo.constants.Constant;
import com.palmaplus.nagrand.demo.utils.DisplayUtils;
import com.palmaplus.nagrand.demo.utils.ToastUtils;
import com.palmaplus.nagrand.demo.widget.Mark;
import com.palmaplus.nagrand.geos.Coordinate;
import com.palmaplus.nagrand.geos.GeometryFactory;
import com.palmaplus.nagrand.geos.LineString;
import com.palmaplus.nagrand.geos.Point;
import com.palmaplus.nagrand.geos.Polygon;
import com.palmaplus.nagrand.navigate.NavigateManager;
import com.palmaplus.nagrand.position.Location;
import com.palmaplus.nagrand.position.PositioningManager;
import com.palmaplus.nagrand.position.ble.BeaconPositioningManager;
import com.palmaplus.nagrand.position.util.PositioningUtil;
import com.palmaplus.nagrand.position.wifi.SinglePositioningManager;
import com.palmaplus.nagrand.view.MapView;
import com.palmaplus.nagrand.view.adapter.DataListAdapter;
import com.palmaplus.nagrand.view.gestures.OnSingleTapListener;
import com.palmaplus.nagrand.view.gestures.OnZoomListener;
import com.palmaplus.nagrand.view.layer.FeatureLayer;
import com.palmaplus.nagrand.view.overlay.OverlayCell;
import com.vividsolutions.jts.geom.LinearRing;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import in.workarounds.bundler.Bundler;


/**
 * Created by lchad on 2016/11/2.
 * Github: https://github.com/lchad
 */

public class SingleBuildingActivity extends BaseActivity implements CompoundButton
        .OnCheckedChangeListener, View.OnClickListener {

    public static final String PAGE_TYPE = "page_type";

    public static void startActivity(Context context, int pageType) {
        Intent intent = new Intent(context, SingleBuildingActivity.class);
        intent.putExtra(PAGE_TYPE, pageType);
        context.startActivity(intent);
    }

    @BindView(R.id.screen_coo)
    TextView mCurrentCoo;
    @BindView(R.id.al_la_coo)
    TextView mCenterCoordinate;
    @BindView(R.id.plan_coo)
    TextView mZoomLevel;
    @BindView(R.id.top_text_container)
    LinearLayout mTopTextContainer;
    @BindView(R.id.map_view)
    MapView mMapView;
    @BindView(R.id.search_container)
    LinearLayout mSearchContainer;
    @BindView(R.id.search)
    EditText mSearch;
    @BindView(R.id.submit)
    Button mSubmit;
    @BindView(R.id.container)
    LinearLayout mContainer;
    /**
     * 楼层切换列表
     */
    @BindView(R.id.spinner)
    Spinner mSpinner;
    /**
     * 地图覆盖物父容器
     */
    @BindView(R.id.map_overlay_container)
    RelativeLayout mMapOverlayContainer;
    @BindView(R.id.zoom)
    SwitchButton mZoom;
    @BindView(R.id.move)
    SwitchButton mMove;
    @BindView(R.id.skew)
    SwitchButton mSkew;
    @BindView(R.id.rotate)
    SwitchButton mRotate;
    @BindView(R.id.click)
    SwitchButton mClick;
    @BindView(R.id.switch_container)
    LinearLayout mSwitchContainer;
    /**
     * 指北针
     */
    @BindView(R.id.compass)
    Compass mCompass;
    /**
     * wifi定位按钮
     */
    @BindView(R.id.wifi)
    ImageView mWifi;
    /**
     * 蓝牙定位按钮
     */
    @BindView(R.id.bluetooth)
    ImageView mBlueTooth;
    @BindView(R.id.locate_container)
    LinearLayout mLocationContainer;
    /**
     * 比例尺
     */
    @BindView(R.id.architect)
    Scale mArchitect;
    @BindView(R.id.config_origin)
    RadioButton mConfigOrigin;
    @BindView(R.id.config_new)
    RadioButton mConfigNew;
    @BindView(R.id.map_function_radio_group)
    RadioGroup mMapFuncRadioGroup;
    @BindView(R.id.info_text)
    TextView mInfoText;
    @BindView(R.id.geo_container)
    LinearLayout mGeoContainer;
    @BindView(R.id.linestring)
    Button mLineString;
    @BindView(R.id.rect)
    Button mPolygon;

    /**
     * 页面类型,对应每一种功能
     */
    private int mPageType;

    /**
     * 地图中心点
     */
    protected Types.Point mMapCenter;
    protected Types.Point mNorthPoint;

    /**
     * 途经点
     */
    long[] transientId = new long[]{186904};
    /**
     * 定位图层
     */
    FeatureLayer mPositioningLayer;
    /**
     * wifi定位接口
     */
    PositioningManager mWifiPositioningManager;
    /**
     * 蓝牙定位接口
     */
    PositioningManager mBlePositioningManager;
    /**
     * 存储地图标记的列表
     */
    private List<OverlayCell> mMarkList;
    private int mNum = 0;
    /**
     * 导航接口
     */
    private NavigateManager mNavigateManager;
    /**
     * 导航图层
     */
    private FeatureLayer mNavFeatureLayer;
    /**
     * 导航需要的坐标点
     */
    private double startX = 13525325.814450;
    private double startY = 3663568.547362;
    private long startId = 185817L;
    private double toX = 13525157.350047;
    private double toY = 3663465.373461;
    private long toId = 186094L;
    /**
     * 当前楼层id.
     */
    private long mCurrentFloorId;
    /**
     * 决定什么时候显示“没有导航数据”提示语
     */
    private boolean mIsNavigating;

    private Feature mPOICenterFeature = null;
    /**
     * 手机屏幕宽度.
     */
    private int mScreenWidth;
    /**
     * 手机屏幕高度.
     */
    private int mScreenHeight;
    /**
     * 手机状态栏高度
     */
    private int mStatusBarHeight;

    /**
     * 折线图层
     */
    private FeatureLayer linestringLayer;
    /**
     * 多边形图层
     */
    private FeatureLayer polygonLayer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_single_building;
    }

    @Override
    protected void setView() {
        showProgressDialog();
        initDimens();
        mMapView.start();

        if (mPageType == PageType.NORMAL_SINGLE_BUILDING) {
            setTitle(R.string.single_building);
            mTopTextContainer.setVisibility(View.GONE);
        }

        if (mPageType == PageType.SET_INITIAL_VISIBLE_AREA) {
            setTitle(R.string.set_initial_visible_area);

            mCurrentCoo.setText(getString(R.string.current_display_area) + "\n" + "(" + startX + "," + startY + ")" + "\n" + "(" + toX + "," + toY + ")");

            mTopTextContainer.setVisibility(View.VISIBLE);
        }

        if (mPageType == PageType.SINGLE_CLICK) {
            setTitle(R.string.single_click);
            mInfoText.setVisibility(View.VISIBLE);

            mMapView.setOnSingleTapListener(new OnSingleTapListener() {
                @Override
                public void onSingleTap(MapView mapView, float x, float y) {
                    Feature feature = mapView.selectFeature(x, y);
                    if (mPOICenterFeature != null) {
                        mMapView.resetOriginStyle("Area", LocationModel.id.get(mPOICenterFeature));
                    }
                    if (feature == null) {
                        mPOICenterFeature = null;
                        ToastUtils.showToast(getString(R.string.position_clicked_beyond_the_map));
                        return;
                    } else {
                        mPOICenterFeature = feature;
                        mMapView.setRenderableColor("Area", LocationModel.id.get(mPOICenterFeature), 0xffff5722);
                    }
                    mapView.moveToPoint(mPOICenterFeature.getCentroid(), true, 300);
                    showClickedPOI(x, y);
                }
            });
        }

        if (mPageType == PageType.GESTURE) {
            setTitle(R.string.gesture);
            mInfoText.setVisibility(View.VISIBLE);
            mSwitchContainer.setVisibility(View.VISIBLE);
            mMapOptions.setSigleTapEnabled(false);
            mMapOptions.setRotateEnabled(true);
            mMapOptions.setSkewEnabled(true);
            mMapOptions.setZoomEnabled(true);
            mMapOptions.setMoveEnabled(true);

            mZoom.setOnCheckedChangeListener(this);
            mMove.setOnCheckedChangeListener(this);
            mSkew.setOnCheckedChangeListener(this);
            mRotate.setOnCheckedChangeListener(this);
            mClick.setOnCheckedChangeListener(this);
        }

        if (mPageType == PageType.MARKER) {
            setTitle(R.string.marker);
            mMarkList = new ArrayList<>();
            //地图单击事件
            mMapView.setOnSingleTapListener(new OnSingleTapListener() {
                @Override
                public void onSingleTap(MapView mapView, float x, float y) {
                    Types.Point point = mMapView.converToWorldCoordinate(x, y);

                    Mark mark = new Mark(mMapView.getContext());
                    mark.setMark(++mNum, x, y);
                    mark.init(new double[]{point.x, point.y});
                    mark.setFloorId(mCurrentFloorId);
                    mapView.addOverlay(mark);
                    mMarkList.add(mark);
                }
            });
        }


        if (mPageType == PageType.GEOMETRY) {
            setTitle(R.string.draw_geometry);

            linestringLayer = new FeatureLayer("linestring");
            polygonLayer = new FeatureLayer("polygon");

            mGeoContainer.setVisibility(View.VISIBLE);
            mLineString.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Coordinate[] coordinatesLineOne = new Coordinate[]{new Coordinate(50, 100), new Coordinate(100, 200)};
                    Coordinate[] coordinatesLineTwo = new Coordinate[]{new Coordinate(100, 200), new Coordinate(100, 50)};

                    /**
                     * 线
                     */
                    LineString lineStringOne = GeometryFactory.createLineString(coordinatesLineOne);
                    LineString lineStringTwo = GeometryFactory.createLineString(coordinatesLineTwo);

                    MapElement lineElementOne = new MapElement();
                    MapElement lineElementTwo = new MapElement();
                    lineElementOne.addElement("id", new BasicElement(1L));
                    lineElementTwo.addElement("id", new BasicElement(2L));
                    Feature lineFeatureOne = new Feature(lineStringOne, lineElementOne);
                    Feature lineFeatureTwo = new Feature(lineStringTwo, lineElementTwo);
                    mMapView.addLayer(linestringLayer);
                    mMapView.addLayer(polygonLayer);
                    linestringLayer.addFeature(lineFeatureOne);
                    linestringLayer.addFeature(lineFeatureTwo);
                }
            });

            mPolygon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * 多边形-世界坐标(墨卡托坐标)
                     */

                    Coordinate[] coordinatesB = new Coordinate[]{
                            new Coordinate(13525198.477705315, 3663550.724238159),
                            new Coordinate(13525261.222753838, 3663552.2181727905),
                            new Coordinate(13525265.704534844, 3663494.950880768),
                            new Coordinate(13525180.052576378, 3663493.622942688),
                            new Coordinate(13525198.477705315, 3663550.724238159)};

                    Polygon polygon = GeometryFactory.createPolygon(
                            GeometryFactory.createLinearRing(coordinatesB),
                            new LinearRing[]{});

                    MapElement polygonElement = new MapElement();
                    polygonElement.addElement("id", new BasicElement(4L));
                    Feature polygonFeature = new Feature(polygon, polygonElement);
                    mMapView.setLayerOffset(polygonLayer);
                    polygonLayer.addFeature(polygonFeature);
                }
            });

        }

        if (mPageType == PageType.SEARCH_POI) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            mSpinner.setVisibility(View.GONE);
            mSearchContainer.setVisibility(View.VISIBLE);
            mContainer.setVisibility(View.VISIBLE);
            mSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = mSearch.getText().toString();
                    if (TextUtils.isEmpty(text)) {
                        return;
                    }
                    mDataSource.search(text, 1, 10, null, null, new DataSource.OnRequestDataEventListener<LocationPagingList>() {
                        @Override
                        public void onRequestDataEvent(DataSource.ResourceState resourceState, LocationPagingList locationPagingList) {
                            handleRequestData(resourceState, locationPagingList);
                        }
                    });
                }
            });
            mSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() == 0) {
                        mContainer.removeAllViews();
                    }
                }
            });
        }

        if (mPageType == PageType.WIFI_LOCATE) {
            setTitle(R.string.wifi_locate);
            mWifi.setVisibility(View.VISIBLE);
            mWifi.setOnClickListener(this);
        }

        if (mPageType == PageType.BLUE_TOOTH_LOCATE) {
            setTitle(R.string.bluetooth_locate);
            mBlueTooth.setOnClickListener(this);
            mBlueTooth.setVisibility(View.VISIBLE);
        }

        if (mPageType == PageType.NAVIGATION_POINT) {
            setTitle(R.string.navigation_point);
        }

        if (mPageType == PageType.NAVIGATION) {
            setTitle(R.string.navigation);
        }

        if (mPageType == PageType.CUSTOM_STYLE) {
            setTitle(R.string.custom_style);
            mMapFuncRadioGroup.setVisibility(View.VISIBLE);

            mConfigOrigin.setOnCheckedChangeListener(this);
            mConfigNew.setOnCheckedChangeListener(this);
        }

        mArchitect.setMapView(mMapView);
        mCompass.setMapView(mMapView);
        mMapView.setMapOptions(mMapOptions);
        mMapView.setOverlayContainer(mMapOverlayContainer);

        //切换楼层事件
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final LocationModel item = (LocationModel) parent.getAdapter().getItem(position);
                //根据floorId，加载地图数据
                showProgressDialog(R.string.map_loading);
                mCurrentFloorId = LocationModel.id.get(item);
                mDataSource.requestPlanarGraph(LocationModel.id.get(item),
                        new DataSource.OnRequestDataEventListener<PlanarGraph>() {
                            @Override
                            public void onRequestDataEvent(DataSource.ResourceState state, PlanarGraph planarGraph) {
                                hideProgressDialog();
                                if (state != DataSource.ResourceState.ok) {
                                    ToastUtils.showToast(mHandler, R.string.map_load_fail);
                                    return;
                                }

                                mMapView.drawPlanarGraph(planarGraph);
                                afterDrawPlanarGraph();
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mMapView.setOnZoomListener(new OnZoomListener() {
            @Override
            public void preZoom(MapView mapView, float x, float y) {

            }

            @Override
            public void onZoom(MapView mapView, boolean b) {

            }

            @Override
            public void postZoom(MapView mapView, float x, float y) {
                mZoomLevel.setText(getString(R.string.zoom_level));
                mZoomLevel.append("" + (int)mMapView.getZoomLevel());
                mCompass.invalidate();
                mArchitect.invalidate();

                Types.Point pointLeftTop = mMapView.converToWorldCoordinate(0, 0);
                Types.Point pointRightBottom = mMapView.converToWorldCoordinate(mScreenWidth, mScreenHeight);
                Types.Point pointCenter = mMapView.converToWorldCoordinate(mScreenWidth / 2, mScreenHeight / 2);

                mCurrentCoo.setText(getString(R.string.current_display_area) + "\n" + "(" + pointLeftTop.x + "," + pointLeftTop.y + ")" + "\n" + "(" + pointRightBottom.x +
                        "," + pointRightBottom.y +
                        ")");
                mCenterCoordinate.setText(getString(R.string.current_display_area) + "\n" + "(" + pointCenter.x + "," + pointCenter.y + ")");

            }
        });
    }

    private void initDimens() {
        mMarkList = new ArrayList<>();

        mPageType = getIntent().getIntExtra(PAGE_TYPE, -1);
        mScreenWidth = DisplayUtils
                .getScreenWidthPixel(SingleBuildingActivity.this);
        mScreenHeight = DisplayUtils
                .getScreenHeightPixel(SingleBuildingActivity.this);
        mStatusBarHeight = DisplayUtils.getStatusBarHeight(SingleBuildingActivity.this);
    }

    public void afterDrawPlanarGraph() {
        mMapCenter = mMapView.converToWorldCoordinate(mScreenWidth / 2, (mScreenHeight - mStatusBarHeight) / 2);
        mNorthPoint = mMapView.converToWorldCoordinate(mScreenWidth / 2, (mScreenHeight - mStatusBarHeight) / 2 - 1000);
        mCenterCoordinate.setText(getString(R.string.center_point) + "\n" + "(" + mMapCenter.x + ", " + mMapCenter.y + ")");
        mZoomLevel.setText(getString(R.string.zoom_level));
        mZoomLevel.append("" + (int)mMapView.getZoomLevel());

        if (mPageType == PageType.NAVIGATION) {
            showProgressDialog(getString(R.string.fetch_navigation_info));
            mNavigateManager.navigation(startX, startY, mCurrentFloorId, toX, toY, mCurrentFloorId); //请求导航线

        }

        if (mPageType == PageType.NAVIGATION_POINT) {
            showProgressDialog(getString(R.string.fetch_navigation_info));
            final Coordinate begin = new Coordinate(startX, startY);
            final Coordinate end = new Coordinate(toX, toY);
            final Coordinate[] transientPoint = new Coordinate[]{new Coordinate(mMapCenter.x, mMapCenter.y)};
            mNavigateManager.navigation(begin, mCurrentFloorId, end, mCurrentFloorId, transientPoint, transientId); //请求导航线

        }

        if (mPageType == PageType.BLUE_TOOTH_LOCATE || mPageType == PageType.WIFI_LOCATE) {
            Mark mark = new Mark(mMapView.getContext());
            mark.setTitle(R.string.current_position);
            mark.setMark(++mNum, startX, startY);
            mark.init(new double[]{startX, startY});
            mark.setFloorId(mCurrentFloorId);
            mMapView.addOverlay(mark);
            mMarkList.add(mark);
        }

        if (mPageType == PageType.SET_INITIAL_VISIBLE_AREA) {
            mMapView.moveToRect(13525261.222753838, 3663552.2181727905, 13525180.052576378, 3663493.622942688);
        }
        if (mPageType == PageType.WIFI_LOCATE) {
            mPositioningLayer = new FeatureLayer("positioning"); //新建一个放置定位点的图层
            mMapView.addLayer(mPositioningLayer);  // 把这个图层添加至MapView中
            mMapView.setLayerOffset(mPositioningLayer); // 让这个图层获取到当前地图的坐标偏移

            // 在PositionLayer上添加一个特征点，用于显示定位点
            Point point = GeometryFactory.createPoint(new Coordinate(0, 0));
            MapElement mapElement = new MapElement();
            mapElement.addElement("id", new BasicElement(1L)); // 1L为特征点ID，下面需要
            Feature feature = new Feature(point, mapElement);
            mPositioningLayer.addFeature(feature);
        }
    }

    @Override
    protected void initData() {
        mDataSource = new DataSource(Constant.SERVER_URL);
        if (mPageType == PageType.NAVIGATION_POINT || mPageType == PageType.NAVIGATION) {
            mNavigateManager = new NavigateManager(Constant.SERVER_URL);

            if (mPageType == PageType.NAVIGATION_POINT || mPageType == PageType.NAVIGATION) {
                Mark markStart = new Mark(mMapView.getContext());
                markStart.setMark(++mNum, startX, startY);
                markStart.setTitle(R.string.start);
                markStart.init(new double[]{startX, startY});
                markStart.setFloorId(mCurrentFloorId);
                mMapView.addOverlay(markStart);

                Mark markEnd = new Mark(mMapView.getContext());
                markStart.setFloorId(mCurrentFloorId);
                markEnd.setMark(++mNum, toX, toY);
                markEnd.setTitle(R.string.end);
                markEnd.init(new double[]{toX, toY});
                mMapView.addOverlay(markEnd);
                mIsNavigating = true;
            }
            /**
             * 设置更换楼层监听器,每次切换楼层需要重新添加导航层
             */
            mMapView.setOnChangePlanarGraph(new MapView.OnChangePlanarGraph() {
                @Override
                public void onChangePlanarGraph(PlanarGraph oldPlanarGraph, PlanarGraph newPlanarGraph, long oldPlanarGraphId, long newPlanarGraphId) {
                    Log.d("Navigate", "oldPlanarGraphId = " + oldPlanarGraphId + "; " +
                            "newPlanarGraphId = " + newPlanarGraphId);
                    mCurrentFloorId = newPlanarGraphId;
                    mNavFeatureLayer = new FeatureLayer("navigate");
                    mMapView.addLayer(mNavFeatureLayer);
                    mMapView.setLayerOffset(mNavFeatureLayer);
                    mCompass.invalidate();
                    mArchitect.invalidate();
                    if (mNavigateManager != null) {
                        mNavigateManager.switchPlanarGraph(newPlanarGraphId);
                    }
                }
            });

            mNavigateManager.setOnNavigateComplete(new NavigateManager.OnNavigateComplete() {
                @Override
                public void onNavigateComplete(NavigateManager.NavigateState navigateState, FeatureCollection featureCollection) {
                    hideProgressDialog();
                    if (mNavFeatureLayer != null && navigateState == NavigateManager.NavigateState.ok) { //成功返回导航线，先清除旧的导航线，再添加新的导航线
                        Log.d("Navigate", "onNavigateComplete->ResourceState.ok");
                        startX = 0;
                        mNavFeatureLayer.clearFeatures();
                        mNavFeatureLayer.addFeatures(featureCollection);
                    } else {
                        if (mIsNavigating) {
                            ToastUtils.showLongToast(R.string.no_navigation_data);
                        }
                    }
                    mIsNavigating = false;
                    Log.d("Navigate", "导航线总长" + String.valueOf(mNavigateManager.getTotalLineLength()));
                    if (featureCollection != null) {
                        Log.d("Navigate", "导航线总长" + featureCollection.getSize());
                    }
                    Log.d("Navigate", "导航线本层长度" + String.valueOf(mNavigateManager.getFloorLineLength(mCurrentFloorId)));
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Bundler.stepInfoActivity(new ArrayList<>(Arrays.asList(mNavigateManager.getAllStepInfo()))).start(SingleBuildingActivity.this);
                        }
                    }, 2000);
                }
            });
        }

        if (mPageType == PageType.WIFI_LOCATE) {
            WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();

            Log.d("TAG", "mac = " + info.getMacAddress());
            // 返回WiFi定位管理对象
            mWifiPositioningManager = new SinglePositioningManager(info.getMacAddress(), "http://location" + ".palmap.cn/ws/comet/");
            // 定位监听的事件，如果得到了新的位置数据，就会调用这个方法
            mWifiPositioningManager.setOnLocationChangeListener(new PositioningManager.OnLocationChangeListener<Location>() {
                @Override
                public void onLocationChange(PositioningManager.LocationStatus status, final Location oldLocation,
                                             final Location newLocation) {  // 分别代表着上一个位置点和新位置点
                    Log.d("TAG", "onLocationChange");
                    switch (status) {
                        case MOVE:
                            Coordinate coordinate = newLocation.getPoint().getCoordinate();
                            Log.d("onLocationChange", "x = " + coordinate.getX() + ", y = " +
                                    coordinate.getY());
                            PositioningUtil.positionLocation(1L, mPositioningLayer, newLocation); //
                            // 当第二次返回点位点时，我们就可以让这个定位点开始移动了
                            break;
                    }


                }
            });
        }

        if (mPageType == PageType.BLUE_TOOTH_LOCATE) {
            // 蓝牙定位管理对象
            mBlePositioningManager = new BeaconPositioningManager(this, Constant.APP_KEY);
            // 定位监听的事件，如果得到了新的位置数据，就会调用这个方法
            mBlePositioningManager.setOnLocationChangeListener(
                    new PositioningManager.OnLocationChangeListener<Location>() {
                        @Override
                        public void onLocationChange(PositioningManager.LocationStatus status, final Location oldLocation,
                                                     final Location newLocation) {  // 分别代表着上一个位置点和新位置点
                            Log.d("TAG", "onLocationChange");
                            switch (status) {
                                case MOVE:
                                    Coordinate coordinate = newLocation.getPoint().getCoordinate();
                                    Log.d("onLocationChange", "x = " + coordinate.getX() + ", y =" +
                                            " " + coordinate.getY());
                                    PositioningUtil.positionLocation(1L, mPositioningLayer, newLocation); //
                                    // 当第二次返回点位点时，我们就可以让这个定位点开始移动了
                                    break;
                                default:
                                    break;
                            }

                        }
                    });
        }

        mDataSource.requestMap(Constant.SINGLE_BUILDING_ID, new DataSource.OnRequestDataEventListener<MapModel>() {
            @Override
            public void onRequestDataEvent(DataSource.ResourceState resourceState, MapModel mapModel) {
                if (resourceState != DataSource.ResourceState.ok) {
                    ToastUtils.showToast(R.string.map_load_fail);
                    return;
                }
                mDataSource.requestPOI(MapModel.POI.get(mapModel), new DataSource.OnRequestDataEventListener<LocationModel>() {
                    @Override
                    public void onRequestDataEvent(DataSource.ResourceState resourceState, final LocationModel locationModel) {
                        if (resourceState != DataSource.ResourceState.ok) {
                            ToastUtils.showToast(R.string.map_load_fail);
                            return;
                        }

                        switch (LocationModel.type.get(locationModel)) {
                            case LocationModel.PLANARGRAPH://平面图
                            case LocationModel.FLOOR://楼层
                                mSpinner.setVisibility(View.GONE);
                                mCurrentFloorId = LocationModel.id.get
                                        (locationModel);
                                mDataSource.requestPlanarGraph(LocationModel.id.get
                                        (locationModel), new DataSource.OnRequestDataEventListener<PlanarGraph>() {
                                    @Override
                                    public void onRequestDataEvent(DataSource.ResourceState resourceState, PlanarGraph planarGraph) {
                                        mMapView.drawPlanarGraph(planarGraph);

                                        afterDrawPlanarGraph();

                                        mCenterCoordinate.setText(getString(R.string.center_point) + "(" + mMapCenter.x + ", " + mMapCenter.y + ")");
                                        mZoomLevel.setText(getString(R.string.zoom_level));
                                        mZoomLevel.append("" + (int)mMapView.getZoomLevel());
                                    }
                                });
                                break;
                            case LocationModel.BUILDING://建筑物
                                mDataSource.requestPOIChildren(LocationModel.id.get
                                        (locationModel), new DataSource.OnRequestDataEventListener<LocationList>() {
                                    @Override
                                    public void onRequestDataEvent(DataSource.ResourceState resourceState, LocationList locationList) {
                                        if (resourceState != DataSource.ResourceState.ok) {
                                            ToastUtils.showToast(R.string.map_load_fail);
                                            return;
                                        }
                                        //楼层切换控件adapter
                                        DataListAdapter<LocationModel> floorAdapter = new DataListAdapter<>(
                                                SingleBuildingActivity.this,
                                                android.R.layout.simple_spinner_item, locationList,
                                                Constant.FLOOR_SHOW_FIELD);

                                        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        mSpinner.setAdapter(floorAdapter);

                                        //设置默认楼层
                                        for (int i = 0; i < floorAdapter.getCount(); i++) {
                                            LocationModel model = floorAdapter.getItem(i);
                                            if (model != null) {
                                                if (FloorModel.default_.get(model)) {
                                                    mSpinner.setSelection(i);
                                                    break;
                                                }
                                            }
                                        }

                                    }
                                });
                                break;
                            default:
                                break;
                        }

                    }
                });

            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.click:
                mMapOptions.setSigleTapEnabled(isChecked);
                mMapView.setMapOptions(mMapOptions);
                OnSingleTapListener singleTapListener = new OnSingleTapListener() {
                    @Override
                    public void onSingleTap(MapView mapView, float x, float y) {
                        Feature feature = mapView.selectFeature(x, y);
                        if (mPOICenterFeature != null) {
                            mMapView.resetOriginStyle("Area", LocationModel.id.get(mPOICenterFeature));
                        }
                        if (feature == null) {
                            mPOICenterFeature = null;
                            ToastUtils.showToast(mHandler, getString(R.string.position_clicked_beyond_the_map));
                            return;
                        } else {
                            mPOICenterFeature = feature;
                            mMapView.setRenderableColor("Area", LocationModel.id.get
                                    (mPOICenterFeature), 0xffff5722);
                        }
                        mapView.moveToPoint(mPOICenterFeature.getCentroid(), true, 300);
                        showClickedPOI(x, y);
                    }
                };
                mMapView.setOnSingleTapListener(isChecked ? singleTapListener : null);
                break;
            case R.id.rotate:
                mMapOptions.setRotateEnabled(isChecked);
                mMapView.setMapOptions(mMapOptions);
                break;
            case R.id.skew:
                mMapOptions.setSkewEnabled(isChecked);
                mMapView.setMapOptions(mMapOptions);
                break;
            case R.id.move:
                mMapOptions.setMoveEnabled(isChecked);
                mMapView.setMapOptions(mMapOptions);
                break;
            case R.id.zoom:
                mMapOptions.setZoomEnabled(isChecked);
                mMapView.setMapOptions(mMapOptions);
                break;
            case R.id.config_origin:
                if (isChecked) {//重置
                    mMapView.resetOriginStyle("Area", "category", new Value(23062000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(23999000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(11201000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(11208000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(13001000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(13003000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(13011000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(13032000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(13035000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(13036000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(13061000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(13062000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(13071000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(13074000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(11454000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(11452000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(11452001L));
                    mMapView.resetOriginStyle("Area", "category", new Value(24000000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(23038000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(23006000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(24091000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(24097000L));
                    mMapView.resetOriginStyle("Area", "category", new Value(24093000L));
                }
                break;
            case R.id.config_new:
                if (isChecked) {
                    mMapView.updateRenderableStyle("Area", "category", new Value(23062000L), 23062000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(23999000L), 23999000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(11201000L), 11201000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(11208000L), 11208000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(13001000L), 13001000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(13003000L), 13003000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(13011000L), 13011000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(13032000L), 13032000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(13035000L), 13035000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(13036000L), 13036000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(13061000L), 13061000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(13062000L), 13062000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(13071000L), 13071000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(13074000L), 13074000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(11454000L), 11454000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(11452000L), 11452000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(11452001L), 11452001L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(24000000L), 24000000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(23038000L), 23038000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(23006000L), 23006000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(24091000L), 24091000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(24097000L), 24097000L);
                    mMapView.updateRenderableStyle("Area", "category", new Value(24093000L), 24093000L);
                }
                break;
        }
    }

    /**
     * 单击一点,展示附近的POI信息
     */
    private void showClickedPOI(float x, float y) {
        Feature selectFeature = mMapView.selectFeature(x, y);
        //通过LocationModel来拿到Feature的name
        String name = LocationModel.name.get(selectFeature);
        mInfoText.setText("(" + selectFeature.getCentroid().getX() + "," +
                selectFeature.getCentroid().getY() + ")" + "\n" + (name == null ? "" : name));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bluetooth:
                if (mMapView.checkInitializing()) {
                    return;
                }
                mBlePositioningManager.start(); // 开始定位
                break;
            case R.id.wifi:
                if (mMapView.checkInitializing()) {
                    return;
                }
                mWifiPositioningManager.start(); // 开始定位
                break;
            case R.id.cancel_button:
                if (mMapView != null) {
                    mMapView.removeAllOverlay();
                }
                if (mMarkList != null) {
                    mMarkList.clear();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理搜索结果
     */
    private void handleRequestData(DataSource.ResourceState resourceState, LocationPagingList locationPagingList) {
        if (resourceState == DataSource.ResourceState.ok) {
            mContainer.removeAllViews();
            int size = locationPagingList.getSize();
            int length = Math.min(size, 5);
            for (int i = 0; i < length; i++) {
                final LocationModel poi = locationPagingList.getPOI(i);
                Log.d("tag", "KFC1");
                String[] keys = DataUtil.highLight(poi);
                Log.d("tag", "KFC2");
                String s = LocationModel.display.get(poi);
                if (s == null) return;
                Log.d("tag", "KFC3 = " + s);

                int[] offset = DataUtil.getOffset(s, keys);

                Log.d("tag", "KFC4");
                final Spannable spannable = Spannable.Factory.getInstance().newSpannable(s);
                for (int j = 0; j < offset.length; j += 2) {
                    spannable.setSpan(new BackgroundColorSpan(0xFFFFFF00), offset[j], offset[j + 1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = new TextView(SingleBuildingActivity.this);
                        tv.setText(spannable);
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Feature feature = mMapView.selectFeature(LocationModel.id.get(poi));
                                mMapView.moveToPoint(feature.getCentroid(), true, 300);
                                Types.Point point = mMapView.converToWorldCoordinate(feature
                                        .getCentroid().x, feature.getCentroid().y);

                                Mark mark = new Mark(mMapView.getContext());
                                mark.setMark(++mNum, feature.getCentroid().x, feature.getCentroid().y);
                                mark.setTitle("");
                                mark.init(new double[]{point.x, point.y});
                                mark.setFloorId(mCurrentFloorId);
                                mMapView.addOverlay(mark);
                                mMarkList.add(mark);

                                mContainer.removeAllViews();
                            }
                        });
                        mContainer.addView(tv);
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.drop();
        }
        if (mNavigateManager != null) {
            mNavigateManager.drop(); //销毁导航管理对象
        }
        if (mBlePositioningManager != null) {
            mBlePositioningManager.drop(); //销毁蓝牙定位管理对象
        }
        if (mWifiPositioningManager != null) {
            mWifiPositioningManager.drop(); //销毁wifi定位管理对象
        }
    }

    /**
     * 页面类型,对应HomeActivity列表中的每一种功能.
     */
    @IntDef({PageType.NORMAL_SINGLE_BUILDING, PageType.SET_INITIAL_VISIBLE_AREA, PageType
            .SINGLE_CLICK, PageType.GESTURE, PageType.MARKER, PageType.GEOMETRY, PageType
            .SEARCH_POI, PageType.WIFI_LOCATE, PageType
            .BLUE_TOOTH_LOCATE, PageType.NAVIGATION_POINT, PageType.CUSTOM_STYLE})
    @Retention(RetentionPolicy.SOURCE)
    @interface PageType {
        /**
         * 单建筑物
         */
        int NORMAL_SINGLE_BUILDING = 0;
        /**
         * 设置初始显示区域
         */
        int SET_INITIAL_VISIBLE_AREA = 1;
        /**
         * 单击事件
         */
        int SINGLE_CLICK = 2;
        /**
         * 手势事件
         */
        int GESTURE = 3;
        /**
         * 标记
         */
        int MARKER = 4;
        /**
         * 绘制几何图形
         */
        int GEOMETRY = 5;
        /**
         * 搜索POI
         */
        int SEARCH_POI = 6;
        /**
         * 定位(WIFI)
         */
        int WIFI_LOCATE = 8;
        /**
         * 定位(蓝牙)
         */
        int BLUE_TOOTH_LOCATE = 13;
        /**
         * 途经点导航
         */
        int NAVIGATION_POINT = 9;
        /**
         * 个性化样式
         */
        int CUSTOM_STYLE = 11;
        /**
         * 路径规划
         */
        int NAVIGATION = 12;
    }

}
