package com.cudocomm.troubleticket.util;

import android.support.v4.app.Fragment;

public interface OnMenuSelected {

    void back();

    void clear(Boolean bool);

    void onMenuSelected(String str, Fragment fragment, Boolean bool);

    void setBreadCrumb(String str);

}
