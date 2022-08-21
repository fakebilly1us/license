package me.beaturing.machine.utils;

import org.hyperic.sigar.Sigar;

import java.io.File;
import java.net.URLDecoder;

/**
 *  处理sugar
 */
public class SigarUtil {

	private static String OSName;

	public static Sigar sigarInit() {
		getOSName();

		try {
			// 将jar包内sigar文件夹内文件写到jar包同级目录
			FileUtil.copyFiles();
			//获取sigar-lib路径
			// jar包外部同级目录
			String jarPath = FileUtil.getJarRoot();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");
			if(jarPath.endsWith(".jar") || jarPath.endsWith(".jar!"))
				jarPath = jarPath.substring(0, jarPath.lastIndexOf("/") + 1);
			File dir = new File(jarPath);
			jarPath = dir.getAbsolutePath();
			String sigarLibPath = jarPath + (jarPath.endsWith("/") ? ".sigar/" : "/.sigar/");
			//String sigarLibPath = "/root/sigar";
			//获取系统path
			String path = System.getProperty("java.library.path");
			if (!path.contains(sigarLibPath)) {
				path += "windows".equals(OSName) ? ";" : ":" + sigarLibPath;
			}
			//重设path
			System.setProperty("java.library.path", path);
			return new Sigar();
		} catch (Exception e) {
			return null;
		}

	}

	private static void getOSName() {
		String os = System.getProperty("os.name").toLowerCase();
		OSName = (os.contains("windows") || os.contains("win")) ? "windows" : "linux";
	}
}
