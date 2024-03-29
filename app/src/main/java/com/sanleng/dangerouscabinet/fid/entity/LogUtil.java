package com.sanleng.dangerouscabinet.fid.entity;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LogUtil {
	// The name for
	private static final String GLOBAL_TAG = "POS_LOG.";
	public static final int DEBUG = Log.DEBUG;
	public static final int INFO = Log.INFO;
	public static final int ERROR = Log.ERROR;
	public static final int ASSERT = Log.ASSERT;
	public static final int PRIORITY_LOWEST = 0;
	public static final int PRIORITY_HIGHEST = ASSERT + 1;
	// TODO 设置此处的Log级别，可屏蔽输出
	public static final int LOGCAT_FILTER_PRIORITY_ADB = PRIORITY_LOWEST;
	public static final int LOGCAT_FILTER_PRIORITY_SAVE_FILE = PRIORITY_HIGHEST;



	private static final char[] PRIORITY_NAMES = "LLVDIWEA".toCharArray();

	private static final int TRACE_PRIORITY = INFO;
	private static final int TRACE_STACK_POSITION = 4;
	private static final String TRACE_TAG = "TRACE";
	private static final String ERROR_TAG = "FAILED";

	private static final String LOG_FOLDER = "bluetooth_log";
	private static FileOutputStream sLogWriter;
	private static final Object FILE_LOCK = new Object();

	private static final SimpleDateFormat FORMATER = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
	private static final int METHOD_NAME_LENGTH = getTraceLog("", "", "")
			.length();
	private static final int FORMAT_FILE_LOG_LENGTH = formatFileLog(DEBUG, "",
			"").length();

	private LogUtil() {
	}

	public static final void close() {
		closeFileLog();
	}

	private static final void closeFileLog() {
		synchronized (FILE_LOCK) {
			if (sLogWriter == null) {
				return;
			}
			try {
				sLogWriter.close();
				Log.i(GLOBAL_TAG, "log file is closed.");
			} catch (IOException e) {
				Log.e(GLOBAL_TAG, "log file close error.", e);
			} finally {
				sLogWriter = null;
			}
		}
	}

	public static final int println(int priority, String tag, String msg) {
		if (tag == null) {
			tag = "";
		}
		if (msg == null) {
			msg = "";
		}

		int result = 0;
		if (priority >= LOGCAT_FILTER_PRIORITY_ADB) {
			result = Log.println(priority, GLOBAL_TAG + tag, msg);
		}

		if (priority >= LOGCAT_FILTER_PRIORITY_SAVE_FILE) {
			saveToFile(priority, tag, msg);
		}
		return result;
	}

	private static final void saveToFile(int priority, String tag, String msg) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return;
		}
		try {
			if (sLogWriter == null) {
				createLogFile();
			}

			String log = formatFileLog(priority, tag, msg);
			byte[] array = log.getBytes();
			sLogWriter.write(array);
			sLogWriter.flush();
		} catch (Throwable t) {
			Log.w(GLOBAL_TAG, "LogUtils.saveToFile error.", t);
			closeFileLog();
		}
	}

	private static final void createLogFile() {
		synchronized (FILE_LOCK) {
			try {
				File sdcard = Environment.getExternalStorageDirectory();
				File fileLog = new File(sdcard, LOG_FOLDER);
				fileLog.mkdirs();
				File file = new File(fileLog, getFileName());
				Log.d(GLOBAL_TAG + "CREAT_LOG_FILE", "log file path: "
						+ getFileName());
				sLogWriter = new FileOutputStream(file, true);
			} catch (IOException e) {
				throw new RuntimeException("create log file error.", e);
			}
		}
	}

	private static String getFileName() {
		StringBuilder fileName = new StringBuilder();
		SimpleDateFormat mFormater = new SimpleDateFormat(
				"yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
		Date date = Calendar.getInstance().getTime();
		fileName.append(mFormater.format(date));
		fileName.append(".txt");
		return fileName.toString();
	}

	private static final String formatFileLog(int priority, String tag,
                                              String msg) {
		int length = tag.length() + msg.length() + FORMAT_FILE_LOG_LENGTH;
		StringBuilder builder = new StringBuilder(length);

		builder.append("[");
		builder.append(getTime());
		builder.append("][");
		builder.append(PRIORITY_NAMES[priority]);
		builder.append("][");
		builder.append(tag);
		builder.append("]: ");
		builder.append(msg);
		builder.append("\n");

		return builder.toString();
	}

	private static final String getTime() {
		Calendar calendar = Calendar.getInstance();
		return FORMATER.format(calendar.getTime());
	}


	public static final int d(String tag, String msg) {
		return println(DEBUG, tag, msg);
	}

	public static String byte2hexString(byte[] b,int lenght) {
		String str = "";
		if(b.length<lenght)
			lenght = b.length;

		for (int n = 0; n < lenght; n++) {
			str += String.format("%02X ", b[n]);
		}

		return str;
	}

	/**
	 * 十六进制字符串转换成bytes
	 * @param byte[] b byte数组
	 * @return String 每个Byte值之间空格分隔
	 */
	public static byte[] HexString2Bytes(String src) {
		int lenth = src.length() / 2;
		byte[] ret = new byte[lenth];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < lenth; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	public static final int i(String tag, String msg) {
		return println(INFO, tag, msg);
	}



	public static final int e(String tag, String msg) {
		return println(ERROR, tag, msg);
	}

	public static final int e(String tag, String msg, Throwable tr) {
		return e(tag, msg + '\n' + Log.getStackTraceString(tr));
	}

	public static final int e(Throwable e) {
		return e(ERROR_TAG, Log.getStackTraceString(e));
	}

	private static final String getTraceLog() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		if (trace == null || (trace.length < TRACE_STACK_POSITION + 1)) {
			return "";
		}

		StackTraceElement ele = trace[TRACE_STACK_POSITION];
		String className = ele.getClassName();
		int dot = className.lastIndexOf('.');
		String simpleClassName = className.substring(dot + 1);
		String methodName = ele.getMethodName();

		Thread thread = Thread.currentThread();
		String threadName = thread.getName() + "(" + thread.getId() + ")";

		String traceLog = getTraceLog(threadName, simpleClassName, methodName);
		return traceLog;
	}

	private static final String getTraceLog(String threadName,
                                            String simpleClassName, String methodName) {
		StringBuilder sb = new StringBuilder(threadName.length()
				+ simpleClassName.length() + methodName.length()
				+ METHOD_NAME_LENGTH);
		sb.append("[");
		sb.append(threadName);
		sb.append("][");
		sb.append(simpleClassName);
		sb.append(".");
		sb.append(methodName);
		sb.append("]");

		return sb.toString();
	}

	// TODO for this style: I/BLUETOOTH_PHONE.TRACE( 1996):
	// [main(1)][MainActivity.onCreate]MainActivity, get more information
	public static final int trace(String msg) {
		return println(TRACE_PRIORITY, TRACE_TAG, getTraceLog() + msg);
	}




}
