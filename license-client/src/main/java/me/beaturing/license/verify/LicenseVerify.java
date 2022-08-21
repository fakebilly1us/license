package me.beaturing.license.verify;

import de.schlichtherle.license.*;
import me.beaturing.license.LicenseManagerHolder;
import me.beaturing.machine.utils.EncryptUtil;
import me.beaturing.machine.utils.MachineInfoUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.prefs.Preferences;

public class LicenseVerify {

	private static String PUBLIC_KEY_ALIAS;
	private static String PUBLIC_STORE_PASSWORD;
	private static String SUBJECT;
	// license证书，放在包外
	private static String LICENSE_PATH;
	private static String PUBLIC_STORE_PATH;
	// 验证文件，放在包内
	private static String CONFPATH;

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 读取配置文件赋值
	 * @param in
	 */
	public void loadProperties(InputStream in){
		Properties properties = new Properties();
		try {
			properties.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PUBLIC_KEY_ALIAS = properties.getProperty("PUBLIC_KEY_ALIAS");
		PUBLIC_STORE_PASSWORD = properties.getProperty("PUBLIC_STORE_PASSWORD");
		SUBJECT = properties.getProperty("SUBJECT");
		LICENSE_PATH = properties.getProperty("LICENSE_PATH");
		PUBLIC_STORE_PATH = properties.getProperty("PUBLIC_STORE_PATH");
		CONFPATH = properties.getProperty("CONFPATH");
	}

	/**
	 * 初始化
	 * @return
	 */
	private static LicenseParam initLicenseParams() {
		Preferences preferences = Preferences.userNodeForPackage(LicenseVerify.class);
		CipherParam cipherParam = new DefaultCipherParam(PUBLIC_STORE_PASSWORD);
		KeyStoreParam publicStoreParam = new DefaultKeyStoreParam(LicenseVerify.class, PUBLIC_STORE_PATH,
				PUBLIC_KEY_ALIAS, PUBLIC_STORE_PASSWORD, null);
		LicenseParam licenseParam = new DefaultLicenseParam(SUBJECT, preferences, publicStoreParam, cipherParam);
		return licenseParam;
	}

	/**
	 * 证书校验
	 * @return
	 */
	public boolean verify() {
		LicenseManager licenseManager = LicenseManagerHolder.getLicenseManager(initLicenseParams());
		// 安装license
		try {
			licenseManager.install(new File(LICENSE_PATH));
			System.out.println("License安装成功");
		} catch (Exception e) {
			System.out.println("License安装失败");
			return false;
		}
		// 验证license
		try {
			// 暂不重写源码
			LicenseContent licenseContent = licenseManager.verify();

			// 拓展校验
			// 获取证书machineCode
			String extra = licenseContent.getExtra().toString();
			String machineCode = extra.substring(0, 32);
			// 获取本机machineCode
			String machineNumber = EncryptUtil.encodeMD5(MachineInfoUtil.getMachineInfos());
			if (!machineCode.equals(machineNumber)) {
				System.out.println("License验证失败");
				return false;
			}

			// 获取本地时间
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			Date now = calendar.getTime();
			// 获取网络时钟
			try {
				HttpURLConnection connection = (HttpURLConnection) new URL("http://www.baidu.com").openConnection();

				connection.setConnectTimeout(1000);
				connection.setReadTimeout(1000);
				connection.connect();

				now = new Date(connection.getDate());
			} catch (Exception e) {
				// 内网环境
			}
			// 获取证书时间
			Date notAfter = licenseContent.getNotAfter();
			Date notBefore = licenseContent.getNotBefore();
			int before = (int)((now.getTime() - notBefore.getTime())/(1000*60*60*24));
			int after = (int)((now.getTime() - notAfter.getTime())/(1000*60*60*24));
			if (after > 0 || before < 0) {
				System.out.println("License验证失败");
				return false;
			}
			// 验证文件时间
			File validtimeConf = new File(CONFPATH);
			if (!validtimeConf.exists()) {
				System.out.println("License验证失败");
				return false;
			}
			int validTime = Integer.valueOf(extra.substring(32));
			// TODO 文件时间内容加密处理，确立加密方案

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("License验证失败");
			return false;
		}

	}


}
