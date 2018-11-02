package com.growalong.android.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**生成图片唯一id
 * @author admin
 *
 */
public class UniqueUtils {

	
	/**
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getImageViewUnique(){
		String name = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String r = String.valueOf((int)((Math.random()*9+1)*100000));
		String identity = name+r;
		return identity;
	}
	
}
