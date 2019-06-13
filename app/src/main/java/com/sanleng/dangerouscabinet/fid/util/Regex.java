package com.sanleng.dangerouscabinet.fid.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式
 * @author zhuQixiang
 *
 */
public class Regex {
	/**
	 * 验证输入地址数据条件(字符与数字同时出现)
	 * @param str 待验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean isHexCharacter(String str) {
		String regex = "^[0-9A-Fa-f]+$";
		return match(regex, str);
	}
	/**
	 * 验证输入设备号数据条件(只能是数字出现)
	 * @param str 待验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean isDecNumber(String str) {
		String regex ="^[0-9]+$";
		return match(regex,str);
	}
	/**
	 * 验证输入IP地址数据条件(只能是数字和.出现)
	 * @param str 待验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean IsMatch(String str) {
		String regex ="^[0-9.]+$";
		return match(regex,str);
	}
	
	/**
	 * 只能是数字和.出现
	 * @param str 字符
	 * @param decimalLength 小数点长度
	 * @return
	 */
	public static boolean IsMatchDouble(String str, int decimalLength) {
		String regex ="^[1-9][0-9]*(\\.[0-9]{1," + decimalLength + "})?$"; //decimalLength为小数位数
		return match(regex,str);
	}
	
	/**
	 * @param regex 正则表达式字符串
	 * @param str 要匹配的字符串
	 * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
	 */
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	
	/**
	 * 验证IP是否正确
	 * @param strIP
	 * @return
	 */
	public static boolean isValidIP(String strIP) {
		// 先检查有无非数字字符
		if (!Regex.IsMatch(strIP)) {
			return false;
		}
		// 再检查数据范围是否合理
		String[] strNumArray = strIP.split("\\.");
		if (strNumArray.length != 4) {
			return false;
		}
		int n = 0;
		for (int i = 0; i < 4; ++i) {
			n = Integer.parseInt(strNumArray[i]);
			if (n > 255) {
				return false;
			}
		}
		return true;
	}
}
