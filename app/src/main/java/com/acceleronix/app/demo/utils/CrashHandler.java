package com.acceleronix.app.demo.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class CrashHandler implements UncaughtExceptionHandler
{
	private static final String TAG = "CrashHandler";
	private UncaughtExceptionHandler mDefaultHandler;//
	private static CrashHandler INSTANCE = new CrashHandler();// 
	private Context mContext;//
	public Map<String, String> info = new HashMap<String, String>();//
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//
	private File dir;
	private String imei;

	private CrashHandler()
	{

	}

	public static CrashHandler getInstance()
	{
		return INSTANCE;
	}


	public void init(Context context)
	{
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);

	}


	public void uncaughtException(Thread thread, Throwable ex)
	{
		if (!handleException(ex) && mDefaultHandler != null)
		{
			mDefaultHandler.uncaughtException(thread, ex);
		}
		else
		{
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
	public boolean handleException(Throwable ex)
	{
		if (ex == null)
			return false;
		ex.printStackTrace();
		saveCrashInfo2File(ex);
		return true;
	}
	private String exception2String(Throwable ex) {
	       StringWriter sw = new StringWriter();
	       PrintWriter pw = new PrintWriter(sw);
	       ex.printStackTrace(pw);
		return sw.toString();
	}

	private String saveCrashInfo2File(Throwable ex)
	{
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : info.entrySet())
		{
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\r\n");
		}
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		ex.printStackTrace(pw);
		Throwable cause = ex.getCause();
		while (cause != null)
		{
			cause.printStackTrace(pw);
			cause = cause.getCause();
		}
		pw.close();
		String result = writer.toString();
		sb.append(result);
		long timetamp = System.currentTimeMillis();
		String time = format.format(new Date());
		String fileName = "crash-" + time + "-" + timetamp + ".txt";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			try
			{
				dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
						+ File.separator + "test_sdk");

				if (!dir.exists())
					dir.mkdir();
				FileOutputStream fos = new FileOutputStream(new File(dir, fileName));
				fos.write(sb.toString().getBytes());
				fos.close();
				
				return fileName;
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	private String crashContent(Throwable ex)
	{
		StringBuffer sb = new StringBuffer();

		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		ex.printStackTrace(pw);
		Throwable cause = ex.getCause();
		while (cause != null)
		{
			cause.printStackTrace(pw);
			cause = cause.getCause();
		}
		pw.close();
		String result = writer.toString();
		sb.append(result);
		return sb.toString();
	}



}