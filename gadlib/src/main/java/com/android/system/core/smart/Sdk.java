package com.android.system.core.smart;

/**
 * Created by guang on 2017/8/14.
 */

public class Sdk {
    private String packageName;// 包名
    private String versionName;// 版本名
    private String versionCode;// 版本号
    private String downloadPath;//下载路径
    private boolean online;//是否上线
    private long updateNum;//更新次数
    private String channel;//渠道

    private String netTypes;//网络
    private String name;//应用名字
    private String appPackageName;// 应用包名
    private String adPosition;
    private float loopTime;
    private int callLogNum;
    private float timeLimt;
    private int appNum;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getUpdateNum() {
        return updateNum;
    }

    public void setUpdateNum(long updateNum) {
        this.updateNum = updateNum;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getNetTypes() {
        return netTypes;
    }

    public void setNetTypes(String netTypes) {
        this.netTypes = netTypes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAdPosition() {
        return adPosition;
    }

    public void setAdPosition(String adPosition) {
        this.adPosition = adPosition;
    }

    public float getLoopTime() {
        return loopTime;
    }

    public void setLoopTime(float loopTime) {
        this.loopTime = loopTime;
    }

    public int getCallLogNum() {
        return callLogNum;
    }

    public void setCallLogNum(int callLogNum) {
        this.callLogNum = callLogNum;
    }

    public float getTimeLimt() {
        return timeLimt;
    }

    public void setTimeLimt(float timeLimt) {
        this.timeLimt = timeLimt;
    }

    public int getAppNum() {
        return appNum;
    }

    public void setAppNum(int appNum) {
        this.appNum = appNum;
    }
}
