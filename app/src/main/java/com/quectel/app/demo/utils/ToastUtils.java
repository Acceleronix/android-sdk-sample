package com.quectel.app.demo.utils;

import android.content.Context;
import android.widget.Toast;


public class ToastUtils
    {  
      
        private ToastUtils()
        {
            /* cannot be instantiated */
            throw new UnsupportedOperationException("cannot be instantiated");
        }

        public static boolean isShow = true;

        public static void showShort(Context context, CharSequence message)
        {
            if (isShow)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        public static void showShort(Context context, int message)
        {
            if (isShow)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        public static void showLong(Context context, CharSequence message)
        {
            if (isShow)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }

        public static void showLong(Context context, int message)
        {
            if (isShow)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }

        public static void show(Context context, CharSequence message, int duration)
        {
            if (isShow)
                Toast.makeText(context, message, duration).show();
        }

        public static void show(Context context, int message, int duration)
        {
            if (isShow)
                Toast.makeText(context, message, duration).show();
        }


//        public static void showErrorToast(Context context){
//            if (isShow){
//                Toast.makeText(context, context.getResources().getString(R.string.net_disconnect), Toast.LENGTH_SHORT).show();
//            }
//        }



    }