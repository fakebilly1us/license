package me.beaturing.machine.utils;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtil {

	private static String DIR_NAME = ".sigar";

	public static void copyFiles() throws IOException {
		//获取jar包目录
		String path = getJarRoot();
		//设置.sigar目录
		String dirPath = setJarPath(path, DIR_NAME);
		//创建.sigar目录
		mkdir(dirPath);
		//读取
		JarFile jarFile = new JarFile(new File(path));
		Enumeration<JarEntry> entries = jarFile.entries();
		InputStream in = null;
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			//jar包内文件路径
			String innerPath = jarEntry.getName();
			//过滤出目标文件，依赖jar包也会获取到
			if (!jarEntry.isDirectory() && (innerPath.startsWith("sigar/l") || innerPath.startsWith("sigar/s"))) {
				String filename = innerPath.substring(innerPath.indexOf("/"));
				String jarPath = "jar:file:" + path + "!/" + innerPath;
				//获取源文件输入流
				in = getJarInput(jarPath);
				Files.copy(in ,new File(dirPath + filename).toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		}
		in.close();
	}

	/**
	 * 获取输入流
	 * @param jarPath
	 * @return
	 * @throws IOException
	 */
	private static InputStream getJarInput(String jarPath) throws IOException {
		URL url = new URL(jarPath);
		JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
		InputStream in = jarConnection.getInputStream();
		return in;
	}

	/**
	 * 获取jar包路径
	 * @return
	 */
	public static String getJarRoot() {
		return FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	}

	/**
	 * 设置jar包同级目录 文件/文件夹
	 * @param path
	 * @return
	 */
	private static String setJarPath(String path, String dirName) {
		if(path.endsWith(".jar") || path.endsWith(".jar!"))
			path = path.substring(0, path.lastIndexOf("/") + 1);
		String dirPath = path + (path.endsWith("/") ? "" : "/");
		return dirPath + dirName;
	}

	/**
	 * 创建目标文件夹
	 * @param path
	 */
	private static void mkdir(String path) {
		File dir = new File(path);
		if(!dir.exists())
			dir.mkdir();
	}

	/**
	 * 删除目标文件夹
	 */
	public static void deleteFiles() {
		File dir = new File(setJarPath(getJarRoot(), DIR_NAME));
		try {
			if (!dir.exists())
				return;
			FileUtils.deleteDirectory(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * jar包同级写结果
	 * @param fileName
	 * @param content
	 */
	public static void writeMachineNumber(String fileName, String content) {
		File file = new File(setJarPath(getJarRoot(), fileName));
		Writer writer = null;
		try {
			if (!file.exists()) {
				Files.createFile(file.toPath());
			}
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(content);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
