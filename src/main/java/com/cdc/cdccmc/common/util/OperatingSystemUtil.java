package com.cdc.cdccmc.common.util;

import com.cdc.cdccmc.common.enums.OperatingSystemPlatform;

/**
 * 操作系统判别
 * @author zhuwen
 * 2018-07-05
 */
public class OperatingSystemUtil {
private static String OS = System.getProperty("os.name").toLowerCase();
	
	private static OperatingSystemUtil _instance = new OperatingSystemUtil();
	
	private OperatingSystemPlatform platform;
	
	private OperatingSystemUtil(){}
	
	public static boolean isLinux(){
		return OS.indexOf("linux")>=0;
	}
	
	public static boolean isMacOS(){
		return OS.indexOf("mac")>=0&&OS.indexOf("os")>0&&OS.indexOf("x")<0;
	}
	
	public static boolean isMacOSX(){
		return OS.indexOf("mac")>=0&&OS.indexOf("os")>0&&OS.indexOf("x")>0;
	}
	
	public static boolean isWindows(){
		return OS.indexOf("windows")>=0;
	}
	
	public static boolean isOS2(){
		return OS.indexOf("os/2")>=0;
	}
	
	public static boolean isSolaris(){
		return OS.indexOf("solaris")>=0;
	}
	
	public static boolean isSunOS(){
		return OS.indexOf("sunos")>=0;
	}
	
	public static boolean isMPEiX(){
		return OS.indexOf("mpe/ix")>=0;
	}
	
	public static boolean isHPUX(){
		return OS.indexOf("hp-ux")>=0;
	}
	
	public static boolean isAix(){
		return OS.indexOf("aix")>=0;
	}
	
	public static boolean isOS390(){
		return OS.indexOf("os/390")>=0;
	}
	
	public static boolean isFreeBSD(){
		return OS.indexOf("freebsd")>=0;
	}
	
	public static boolean isIrix(){
		return OS.indexOf("irix")>=0;
	}
	
	public static boolean isDigitalUnix(){
		return OS.indexOf("digital")>=0&&OS.indexOf("unix")>0;
	}
	
	public static boolean isNetWare(){
		return OS.indexOf("netware")>=0;
	}
	
	public static boolean isOSF1(){
		return OS.indexOf("osf1")>=0;
	}
	
	public static boolean isOpenVMS(){
		return OS.indexOf("openvms")>=0;
	}
	
	/**
	 * 获取操作系统名字
	 * @return 操作系统名
	 */
	public static OperatingSystemPlatform getOperatingSystemName(){
		if(isAix()){
			_instance.platform = OperatingSystemPlatform.AIX;
		}else if (isDigitalUnix()) {
			_instance.platform = OperatingSystemPlatform.Digital_Unix;
		}else if (isFreeBSD()) {
			_instance.platform = OperatingSystemPlatform.FreeBSD;
		}else if (isHPUX()) {
			_instance.platform = OperatingSystemPlatform.HP_UX;
		}else if (isIrix()) {
			_instance.platform = OperatingSystemPlatform.Irix;
		}else if (isLinux()) {
			_instance.platform = OperatingSystemPlatform.Linux;
		}else if (isMacOS()) {
			_instance.platform = OperatingSystemPlatform.Mac_OS;
		}else if (isMacOSX()) {
			_instance.platform = OperatingSystemPlatform.Mac_OS_X;
		}else if (isMPEiX()) {
			_instance.platform = OperatingSystemPlatform.MPEiX;
		}else if (isNetWare()) {
			_instance.platform = OperatingSystemPlatform.NetWare_411;
		}else if (isOpenVMS()) {
			_instance.platform = OperatingSystemPlatform.OpenVMS;
		}else if (isOS2()) {
			_instance.platform = OperatingSystemPlatform.OS2;
		}else if (isOS390()) {
			_instance.platform = OperatingSystemPlatform.OS390;
		}else if (isOSF1()) {
			_instance.platform = OperatingSystemPlatform.OSF1;
		}else if (isSolaris()) {
			_instance.platform = OperatingSystemPlatform.Solaris;
		}else if (isSunOS()) {
			_instance.platform = OperatingSystemPlatform.SunOS;
		}else if (isWindows()) {
			_instance.platform = OperatingSystemPlatform.Windows;
		}else{
			_instance.platform = OperatingSystemPlatform.Others;
		}
		return _instance.platform;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(OperatingSystemUtil.getOperatingSystemName());
		
		System.out.println( System.getProperty("os.name") );
	    System.out.println( System.getProperty("os.version") );
	    System.out.println( System.getProperty("os.arch") );
	}

}
