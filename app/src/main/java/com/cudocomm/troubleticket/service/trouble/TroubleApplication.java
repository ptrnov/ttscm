package com.cudocomm.troubleticket.service.trouble;

import android.content.Context;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class TroubleApplication {
    private TrobleServcice trobleServcice;
    private PenyebabService penyebabService;
    private static Context mContext;
    private static TroubleApplication troubleApplication;
    private Scheduler defaultSubscribeScheduler;



    public synchronized static TroubleApplication get(Context context) {
        mContext = context;
        if (troubleApplication == null) {
            troubleApplication = new TroubleApplication();
        }
        return troubleApplication;
    }

    public TrobleServcice getTrobleServcice() {
        if (trobleServcice == null) {
            return null;
        }
        return trobleServcice;
    }

    public PenyebabService getPenyebabService() {
        if (penyebabService == null) {
            return null;
        }
        return penyebabService;
    }


    public void setPenyebab(PenyebabService trobleServcice) {
        this.penyebabService = trobleServcice;
    }

    public void setTrobleServcice(TrobleServcice trobleServcice) {
        this.trobleServcice = trobleServcice;
    }

    public Scheduler defaultSubscribeScheduler() {
        if (defaultSubscribeScheduler == null) {
            defaultSubscribeScheduler = Schedulers.io();
        }
        return defaultSubscribeScheduler;
    }

    //User to change scheduler from tests
    public void setDefaultSubscribeScheduler(Scheduler scheduler) {
        this.defaultSubscribeScheduler = scheduler;
    }


}
