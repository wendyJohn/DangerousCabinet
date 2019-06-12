package com.sanleng.dangerouscabinet.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 保存数据至preference
 */
public class PreferenceUtils {
    /**
     * 存入文件中的工程名
     */
    public static final String PREFERENCES_NAME = " com.example.juicekaaa.fireserver.preferences";

    /**
     * 保存至preferences中
     *
     * @param key
     * @return
     */
    public static void setString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 从preferences中获取
     *
     * @param key
     */
    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    /**
     * 保存至preferences中
     *
     * @param key
     * @return
     */
    public static void setInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 从preferences中获取
     *
     * @param key
     */
    public static int getInt(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return sp.getInt(key, -1);
    }

    /**
     * 从preferences中获取
     *
     * @param key
     */
    public static String getStringWithDefault(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(key, "0");
    }

    /**
     * 删除preferences中的数据
     *
     * @param key
     * @return
     */
    public static void delString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 保存至preferences中
     *
     * @param key
     * @return
     */
    public static void setLong(Context context, String key, Long value) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 从preferences中获取
     *
     * @param key
     */
    public static Long getLong(Context context, String key, long def) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return sp.getLong(key, def);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    /**
     * 设置当前拍照质量等级
     *
     * @param context
     * @param quality
     */
    public static void setPhotoQuality(Context context, int quality) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constance.PRE_PHOTO_QUILTY, quality);
        editor.commit();
    }

    /**
     * 获取当前拍照质量等级
     *
     * @param context
     * @return
     */
    public static int getPhotoQuality(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return sp.getInt(Constance.PRE_PHOTO_QUILTY,
                Constance.PHOTO_MIDDLE_QUALITY);
    }

    /**
     * 设置当前录音质量等级
     *
     * @param context
     * @param quality
     */
    public static void setSoundsQuality(Context context, int quality) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constance.PRE_SONDS_QUILTY, quality);
        editor.commit();
    }

    /**
     * 获取当前录音质量等级
     *
     * @param context
     * @return
     */
    public static int getSoundsQuality(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return sp.getInt(Constance.PRE_SONDS_QUILTY,
                Constance.SONDS_MIDDLE_QUALITY);
    }

    /**
     * 设置配置包更新状态
     *
     * @param context
     * @param state
     */
    public static void setSwitchState(Context context, boolean state) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constance.SWITCH_UPDATE_CONFIG, state);
        editor.commit();
    }

    /**
     * 获取配置包更新状态
     *
     * @param context
     * @return
     */
    public static boolean getSwitchState(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        // 如果没取到默认是false
        return sp.getBoolean(Constance.SWITCH_UPDATE_CONFIG, false);
    }

    /**
     * 设置任务上传状态
     *
     * @param context
     * @param state
     */
    public static void setSwitchStateF(Context context, boolean state) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constance.SWITCH_COMMIT, state);
        editor.commit();
    }

    /**
     * 获取任务上传开关状态
     *
     * @param context
     * @return
     */
    public static boolean getSwitchStateF(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        // 如果没取到默认是false
        return sp.getBoolean(Constance.SWITCH_COMMIT, false);
    }

    /**
     * 设置自动更新开关状态
     *
     * @param context
     * @param state
     */
    public static void setSwitchStateS(Context context, boolean state) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constance.SWITCH_AUTO_UPDATE, state);
        editor.commit();
    }

    /**
     * 获取自动更新开关状态
     *
     * @param context
     * @return
     */
    public static boolean getSwitchStateS(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        // 如果没取到默认是false
        return sp.getBoolean(Constance.SWITCH_AUTO_UPDATE, false);
    }
}
