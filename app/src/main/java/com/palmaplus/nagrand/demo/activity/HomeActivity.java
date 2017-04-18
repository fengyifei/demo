package com.palmaplus.nagrand.demo.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ExpandableListView;

import com.palmaplus.nagrand.core.Engine;
import com.palmaplus.nagrand.demo.R;
import com.palmaplus.nagrand.demo.adapter.HomeAdapter;
import com.palmaplus.nagrand.demo.base.BaseActivity;
import com.palmaplus.nagrand.demo.constants.Constant;
import com.palmaplus.nagrand.demo.utils.FileUtils;
import com.palmaplus.nagrand.demo.utils.ToastUtils;

import butterknife.BindView;

/**
 * Created by lchad on 2016/11/1.
 * Github: https://github.com/lchad
 * 功能列表主页哈哈哈呵呵呵www
 */
public class HomeActivity extends BaseActivity {

    /**
     * 申请读写文件权限返回标志字段
     */
    private static final int REQUEST_WRITE_STORAGE = 112;

    @BindView(R.id.listView)
    ExpandableListView listView;

    private HomeAdapter adapter;

    private int[] funcArray = {R.array.map_display, R.array.map_event, R.array.cover, R.array
            .search_poi, R.array.locate, R.array.route_plan, R.array.coordinate_transform,
            R.array.style_configuration};

    private String[] funcName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void setView() {
        setTitle(R.string.app_name);
    }

    @Override
    protected void initData() {
        //是否拥有读写文件的权限,Android6.0及以上需开发者格外注意权限问题.
        boolean hasPermission = (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            //申请权限
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
            return;
        } else {
            copyLuaToStorage();
        }

        renderListView();

    }

    private void renderListView() {
        ToastUtils.showLongToast("请确保已经把so动态链接库添加到src\\main\\jniLibs目录下!!!");

        funcName = getResources().getStringArray(R.array.function);
        adapter = new HomeAdapter(this, funcName, funcArray);
        adapter.setChildClickListener(new HomeAdapter.ChildItemClickListener() {
            @Override
            public void onItemClick(int groupPosition, int childPosition) {
                switch (groupPosition) {
                    case 0: //地图显示
                        if (childPosition == 0) {//上海证券大厦
                            SingleBuildingActivity.startActivity(HomeActivity.this,
                                    SingleBuildingActivity.PageType.NORMAL_SINGLE_BUILDING);
                        }
                        if (childPosition == 1) {//正大广场
                            MultiBuildingActivity.startActivity(HomeActivity.this);

                        }
                        if (childPosition == 2) {
                            SingleBuildingActivity.startActivity(HomeActivity.this,
                                    SingleBuildingActivity.PageType.SET_INITIAL_VISIBLE_AREA);
                        }
                        break;
                    case 1: //地图事件
                        if (childPosition == 0) {
                            SingleBuildingActivity.startActivity(HomeActivity.this,
                                    SingleBuildingActivity.PageType.SINGLE_CLICK);
                        }
                        if (childPosition == 1) {
                            SingleBuildingActivity.startActivity(HomeActivity.this,
                                    SingleBuildingActivity.PageType.GESTURE);
                        }
                        break;
                    case 2: //覆盖物
                        if (childPosition == 0) {
                            SingleBuildingActivity.startActivity(HomeActivity.this,
                                    SingleBuildingActivity.PageType.MARKER);
                        }
                        if (childPosition == 1) {
                            SingleBuildingActivity.startActivity(HomeActivity.this,
                                    SingleBuildingActivity.PageType.GEOMETRY);
                        }
                        break;
                    case 3: //搜索POI
                        if (childPosition == 0) {
                            SingleBuildingActivity.startActivity(HomeActivity.this,
                                    SingleBuildingActivity.PageType.SEARCH_POI);
                        }
                        break;
                    case 4: //定位
                        if (childPosition == 0) {
                            SingleBuildingActivity.startActivity(HomeActivity.this,
                                    SingleBuildingActivity.PageType.WIFI_LOCATE);
                        }
                        if (childPosition == 1) {
                            SingleBuildingActivity.startActivity(HomeActivity.this,
                                    SingleBuildingActivity.PageType.BLUE_TOOTH_LOCATE);
                        }
                        break;
                    case 5: //路径规划
                        if (childPosition == 0) {
                            SingleBuildingActivity.startActivity(HomeActivity.this,
                                    SingleBuildingActivity.PageType.NAVIGATION);
                        }
                        if (childPosition == 1) {
                            SingleBuildingActivity.startActivity(HomeActivity.this,
                                    SingleBuildingActivity.PageType.NAVIGATION_POINT);
                        }
                        break;
                    case 6: //坐标转换
                        if (childPosition == 0) {
                            CoordinateTransformActivity.startActivity(HomeActivity.this);
                        }
                        break;
                    case 7: //样式配置
                        if (childPosition == 0) {
                            SingleBuildingActivity.startActivity(HomeActivity.this,
                                    SingleBuildingActivity.PageType.CUSTOM_STYLE);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        listView.setAdapter(adapter);
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ToastUtils.showLongToast(R.string.permission_denied);
                        Log.d("permission: ", getResources().getString(R.string.permission_denied));
                        renderListView();
                    }
                } else {
                    copyLuaToStorage();
                    Engine engine = Engine.getInstance(); //初始化引擎
                    engine.startWithLicense("ef51f2f3b4fa41f993a62e19a8dc3e08", this);//设置验证license，可以通过开发者平台去查找自己的license
                    initData();
                }
            }
            default:
                break;
        }
    }

    /**
     * 把Asset目录下的lua配置文件复制到sd卡内
     */
    public void copyLuaToStorage() {
        if (FileUtils.copyLuaFinished()) {
            return;
        }
        if (FileUtils.checkoutSDCard()) {
            Log.d("lua:", "开始复制!");
            FileUtils.copyDirToSDCardFromAsserts(this, Constant.LUR_NAME, "font");
            FileUtils.copyDirToSDCardFromAsserts(this, Constant.LUR_NAME, Constant.LUR_NAME);
            Log.d("lua:", "复制完成!");
        } else {
            ToastUtils.showToast(R.string.do_not_find_sdcard);
        }
    }

}
