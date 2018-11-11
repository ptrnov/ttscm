package com.cudocomm.troubleticket.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.cudocomm.troubleticket.activity.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.nekocode.badge.BadgeDrawable;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by adsxg on 4/11/2017.
 */

public class CommonsUtil {

    public static String getAbsoluteUrl(String relativeUrl) {
        return Constants.BASE_URL + relativeUrl;
    }

    public static String getAbsoluteUrlImage(String relativeUrl) {
        return Constants.BASE_URL_IMAGE + relativeUrl;
    }

    public static void appPermission(Activity activity) {
        //        permission
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.INTERNET)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_NETWORK_STATE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.MANAGE_DOCUMENTS)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.VIBRATE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.GET_ACCOUNTS)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.SET_ALARM)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WAKE_LOCK)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.MANAGE_DOCUMENTS,
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.VIBRATE, //new
                                Manifest.permission.GET_ACCOUNTS, //new
                                Manifest.permission.SET_ALARM, //new
                                Manifest.permission.WAKE_LOCK, //new
                        }, 1);
            } else {
                //do something
            }
        } else {
            //do something
        }
    }

    public static boolean writeToFile(String data, String Filename, File path) {
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, Filename);
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.flush();
            fOut.close();
            return true;
        } catch (IOException e) {
            Logcat.e("Exception", "File write failed: " + e.toString());
            return false;
        }
    }

    public static String readJSONFromAsset(String path) throws Exception {
        try {
            FileInputStream is = new FileInputStream(new File(path));
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, HTTP.UTF_8);
            return json;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String dateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        return dateFormat.format(date);
    }

    public static String formatDateString(String sDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date = new Date(sDate);
        return dateFormat.format(date);
    }

    public static String getToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String statusToString(int status) {
        String result = "Open";
        switch (status) {
            case 0:
                result = "Closed";
                break;
            case 1:
                result = "Open";
                break;
            case 2:
                result = "Confirm";
                break;
            default:
                break;
        }
        return result;
    }

    public static int statusToInt(String status) {
        if(status.equalsIgnoreCase("open")) {
            return 1;
        } else if(status.equalsIgnoreCase("confirm")) {
            return 2;
        } else {
            return 0;
        }
    }

    public static String severityToString(int status) {
        String severity = "Critical";
        switch (status) {
            case 1:
                severity = "Critical";
                break;
            case 2:
                severity = "Major";
                break;
            case 3:
                severity = "Minor";
                break;
            default:
                break;
        }
        return severity;
    }

    public static int severityToInt(String status) {
        if(status.toLowerCase().equalsIgnoreCase(Constants.CRITICAL))
            return 1;
        else if(status.toLowerCase().equalsIgnoreCase(Constants.MAJOR))
            return 2;
        else if(status.toLowerCase().equalsIgnoreCase(Constants.MINOR))
            return 3;
        else
            return 0;
    }

    public static int assignTypeToInt(String assignType) {
        if(assignType.toLowerCase().equalsIgnoreCase(Constants.GUIDANCE))
            return 1;
        else if(assignType.toLowerCase().equalsIgnoreCase(Constants.ON_SITE_VISIT))
            return 2;
        else
            return 0;
    }

    public static String ticketTypeToString(int status) {
        String type = "Down Time";
        switch (status) {
            case 1:
                type = "Down Time";
                break;
            case 2:
                type = "Kerusakan";
                break;
            case 3:
                type = "Gangguan AV";
                break;
            default:
                break;
        }
        return type;
    }

    public static BadgeDrawable severityBadge(int severity) {
        int color = Color.RED;
        switch (severity) {
            case 1:
                color = Color.RED;
                break;
            case 2:
                color = Color.YELLOW;
                break;
            case 3:
                color = Color.BLUE;
                break;
            default:
                break;
        }
        return new BadgeDrawable.Builder().type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                .badgeColor(color)
                .text1(CommonsUtil.severityToString(severity))
                .build();
    }

    public static String secondToTime(int seconds) {
        int perDays = 3600*24;
        int perHours = 3600;
        int perMinutes = 60;
        int seconds_output, minutes, hours, days;
        days = seconds / perDays;
        hours = (seconds%perDays) / perHours;
        minutes = (seconds%perHours)/perMinutes;
        seconds_output = (seconds% perHours)%perMinutes;

        /*if(days > 0)
            return (days + " days " + hours  + " hours " + minutes + " minutes " + seconds_output +" seconds");
        else
            return (hours  + " hours " + minutes + " minutes " + seconds_output +" seconds");*/
        if(days > 0) {
            return (days + "d " + hours  + "h " + minutes + "m " + seconds_output +"s");
        } else {
            if(hours > 0) {
                return (hours  + "h " + minutes + "m " + seconds_output +"s");
            } else {
                return (minutes + "m " + seconds_output +"s");
            }
        }

    }

    public static String countAging(String createDate) {
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        long retDate;
        try {
            date = oldFormat.parse(createDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        Date d = getDateNow();
        retDate = (d.getTime() - date.getTime()) / 1000;

Logcat.i("DATE CREATE: " + date + " - " + date.getTime());
Logcat.i("DATE NOW: " + d + " - " + d.getTime());
Logcat.i("RET: " + retDate);
Logcat.i("RES: " + secondToTime((int) retDate));


        return secondToTime((int) retDate);
    }

    public static int countAgingInSec(String createDate) {
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        long retDate;
        try {
            date = oldFormat.parse(createDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        Date d = getDateNow();
        retDate = (d.getTime() - date.getTime()) / 1000;

        Logcat.i("DATE CREATE: " + date + " - " + date.getTime());
        Logcat.i("DATE NOW: " + d + " - " + d.getTime());
        Logcat.i("RET: " + retDate);
        Logcat.i("RES: " + secondToTime((int) retDate));


        return (int) retDate;
    }

    public static float countAgingInDay(String createDate) {
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        long retDate;
        long ret;
        try {
            date = oldFormat.parse(createDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        Date d = getDateNow();
        retDate = (d.getTime() - date.getTime()) / 1000;

        ret = retDate / (3600*24);

        Logcat.i("DATE CREATE: " + date + " - " + date.getTime());
        Logcat.i("DATE NOW: " + d + " - " + d.getTime());
        Logcat.i("RET: " + retDate);
        Logcat.i("RES: " + secondToTime((int) retDate));


        return ret;
    }

    public static Date getDateNow() {
        return new Date();
    }

    public static String dateToString(String sDate) {
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        try {
            date = oldFormat.parse(sDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
        return format.format(date);
    }

    public static String datetimeToString(String sDate) {
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        try {
            date = oldFormat.parse(sDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        return format.format(date);
    }

    public static String timeToString(String sDate) {
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        try {
            date = oldFormat.parse(sDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }

    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void refreshFragment(String page, Boolean flag) {
        MainActivity mainActivity = new MainActivity();
        mainActivity.setBreadCrumb(page);
        FragmentTransaction fragmentTransaction = mainActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        Fragment fragment = mainActivity.getSupportFragmentManager().findFragmentByTag(page);
        fragmentTransaction.detach(fragment);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
    }

}
