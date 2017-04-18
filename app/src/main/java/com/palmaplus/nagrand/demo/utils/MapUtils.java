package com.palmaplus.nagrand.demo.utils;

import com.palmaplus.nagrand.demo.R;
import com.palmaplus.nagrand.demo.base.NagrandApplication;
import com.palmaplus.nagrand.navigate.DynamicNavigateAction;
import com.palmaplus.nagrand.navigate.StepInfo;

/**
 * Created by lchad on 2016/12/15.
 * Github: https://github.com/lchad
 */

public class MapUtils {
    // TODO: 2016/12/15 补全每种动作对应的图标.

    /**
     * 根据action type 来获取大图标.(72px)
     *
     * @param stepinfo 分段信息.
     * @return 图片.
     */
    public static int getMapActionHugeResource(StepInfo stepinfo) {
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_ARRIVE) {
            return 0;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_BACK_LEFT) {
            return R.drawable.icon_turn_left_back_72;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_BACK_RIGHT) {
            return R.drawable.icon_turn_right_back_72;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_CONNECT_FACILITY) {
            if (stepinfo.mEndConnectInfo == null) {
                return R.drawable.icon_turn_left_72;
            }
            long cateId = stepinfo.mEndConnectInfo.categoryId;//类型
            if (cateId == 0) {
                return R.drawable.icon_turn_left_72;
            }
            if (cateId == 24091000 || cateId == 24092000) {//电梯
                return R.drawable.icon_details_elevater;
            }
            if (cateId == 22076000) {//坡道
                return R.drawable.icon_details_ramp;
            }
            if (cateId == 22006000 || cateId == 22054000 || cateId == 23041000 || cateId == 23043000) {//门
                return R.drawable.icon_details_gate;
            }
            if (cateId == 24093000 || cateId == 24094000 || cateId == 24095000 || cateId == 24096000) {//扶梯
                return R.drawable.icon_escalator;
            }
            if (cateId == 24097000 || cateId == 24098000) {//楼梯
                return R.drawable.icon_stairs;
            }
            return R.drawable.icon_turn_left_72;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_FRONT_LEFT) {
            return R.drawable.icon_turn_left_front_72;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_FRONT_RIGHT) {
            return R.drawable.icon_turn_right_front_72;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_RESET) {
            return R.drawable.icon_turn_left_72;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_STRAIGHT) {
            return R.drawable.icon_turn_front_72;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_TURN_BACK) {
            return R.drawable.icon_turn_back_72;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_TURN_LEFT) {
            return R.drawable.icon_turn_left_72;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_TURN_RIGHT) {
            return R.drawable.icon_turn_right_72;
        }

        return 0;
    }

    /**
     * 根据action type 来获取小图标.(40px)
     *
     * @param stepinfo 分段信息.
     * @return 图片.
     */
    public static int getMapActionNormalResource(StepInfo stepinfo) {
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_ARRIVE) {
            return R.drawable.icon_turn_left_40;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_BACK_LEFT) {
            return R.drawable.icon_turn_left_back_40;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_BACK_RIGHT) {
            return R.drawable.icon_turn_right_back_40;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_CONNECT_FACILITY) {//联通设施
            if (stepinfo.mEndConnectInfo == null) {
                return 0;
            }
            long cateId = stepinfo.mEndConnectInfo.categoryId;//类型
            if (cateId == 0) {
                return 0;
            }
            if (cateId == 24091000 || cateId == 24092000) {//电梯
                return R.drawable.icon_details_elevater;
            }
            if (cateId == 22076000) {//坡道
                return R.drawable.icon_details_ramp;
            }
            if (cateId == 22006000 || cateId == 22054000 || cateId == 23041000 || cateId == 23043000) {//门
                return R.drawable.icon_details_gate;
            }
            if (cateId == 24093000 || cateId == 24094000 || cateId == 24095000 || cateId == 24096000) {//扶梯
                return R.drawable.icon_escalator;
            }
            if (cateId == 24097000 || cateId == 24098000) {//楼梯
                return R.drawable.icon_stairs;
            }
            return 0;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_FRONT_LEFT) {
            return R.drawable.icon_turn_left_front_40;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_FRONT_RIGHT) {
            return R.drawable.icon_turn_right_front_40;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_RESET) {
            return R.drawable.icon_turn_left_40;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_STRAIGHT) {
            return R.drawable.icon_turn_front_40;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_TURN_BACK) {
            return R.drawable.icon_turn_back_40;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_TURN_LEFT) {
            return R.drawable.icon_turn_left_40;
        }
        if (stepinfo.mAction == DynamicNavigateAction.ACTION_TURN_RIGHT) {
            return R.drawable.icon_turn_right_40;
        }

        return 0;
    }

    /**
     * 根据action type 来获取对应的文字描述信息.
     *
     * @param type 动作类型,如左转,直行等.
     * @return 描述信息.
     */
    public static String getMapActionString(int type) {
        if (type == DynamicNavigateAction.ACTION_ARRIVE) {
            return NagrandApplication.instance.getString(R.string.arrive_in);
        }
        if (type == DynamicNavigateAction.ACTION_BACK_LEFT) {
            return NagrandApplication.instance.getString(R.string.turn_left);
        }
        if (type == DynamicNavigateAction.ACTION_BACK_RIGHT) {
            return NagrandApplication.instance.getString(R.string.turn_right);
        }
        if (type == DynamicNavigateAction.ACTION_CONNECT_FACILITY) {
            return NagrandApplication.instance.getString(R.string.connect_facility);
        }
        if (type == DynamicNavigateAction.ACTION_FRONT_LEFT) {
            return NagrandApplication.instance.getString(R.string.turn_left);
        }
        if (type == DynamicNavigateAction.ACTION_FRONT_RIGHT) {
            return NagrandApplication.instance.getString(R.string.turn_right);
        }
        if (type == DynamicNavigateAction.ACTION_RESET) {
            return NagrandApplication.instance.getString(R.string.turn_left);
        }
        if (type == DynamicNavigateAction.ACTION_STRAIGHT) {
            return NagrandApplication.instance.getString(R.string.walk_streight);
        }
        if (type == DynamicNavigateAction.ACTION_TURN_BACK) {
            return NagrandApplication.instance.getString(R.string.u_turn);
        }
        if (type == DynamicNavigateAction.ACTION_TURN_LEFT) {
            return NagrandApplication.instance.getString(R.string.turn_left);
        }
        if (type == DynamicNavigateAction.ACTION_TURN_RIGHT) {
            return NagrandApplication.instance.getString(R.string.turn_right);
        }

        return NagrandApplication.instance.getString(R.string.turn_left);
    }
}