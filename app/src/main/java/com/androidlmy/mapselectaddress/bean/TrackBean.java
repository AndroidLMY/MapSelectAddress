package com.androidlmy.mapselectaddress.bean;

import org.litepal.crud.LitePalSupport;

/**
 * @功能:
 * @Creat 2019/12/4 10:07
 * @User Lmy
 * @Compony zaituvideo
 */
public class TrackBean extends LitePalSupport {
    public double jingdu;
    public double weidu;

    public TrackBean(double jingdu, double weidu) {
        this.jingdu = jingdu;
        this.weidu = weidu;
    }

    public double getJingdu() {
        return jingdu;
    }

    public void setJingdu(double jingdu) {
        this.jingdu = jingdu;
    }

    public double getWeidu() {
        return weidu;
    }

    public void setWeidu(double weidu) {
        this.weidu = weidu;
    }
}
