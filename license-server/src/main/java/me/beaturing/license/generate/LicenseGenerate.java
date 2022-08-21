package me.beaturing.license.generate;

import de.schlichtherle.license.*;
import me.beaturing.license.LicenseManagerHolder;

import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.prefs.Preferences;

public class LicenseGenerate {

	private static String PRIVATE_KEY_ALIAS;
	private static String KEY_PASSWORD;
	private static String PRIVATE_STORE_PASSWORD;
	private static String SUBJECT;
	private static String LICENSE_OUTPUT_PATH;
	private static String PRIVATE_STORE_PATH;

	private static Date ISSUED_TIME;
	private static Date NOT_BEFORE_TIME;
	private static Date NOT_AFTER_TIME;
	private static String CONSUMER_TYPE;
	private static int CONSUMER_AMOUNT;
	private static String INFO;
	private static String VALID_TIME; // 证书有效时间，若为0，即无效
	private static String MACHINECODE;

	private static X500Principal DEFAULTHOLDERANDISSUER; // 使用keytool工具生成密钥库的信息

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 加载外部配置文件
	 *
	 * @param in
	 */
	public void loadProperties(InputStream in) {

		Properties properties = new Properties();
		try {
			properties.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 读取配置文件中的信息，赋值变量
		PRIVATE_KEY_ALIAS = properties.getProperty("PRIVATE_KEY_ALIAS");
		KEY_PASSWORD = properties.getProperty("KEY_PASSWORD");
		PRIVATE_STORE_PASSWORD = properties.getProperty("PRIVATE_STORE_PASSWORD");
		SUBJECT = properties.getProperty("SUBJECT");
		LICENSE_OUTPUT_PATH = properties.getProperty("LICENSE_OUTPUT_PATH");
		PRIVATE_STORE_PATH = properties.getProperty("PRIVATE_STORE_PATH");
		VALID_TIME = properties.getProperty("VALID_TIME");
		CONSUMER_TYPE = properties.getProperty("CONSUMER_TYPE");
		CONSUMER_AMOUNT = Integer.valueOf(properties.getProperty("CONSUMER_AMOUNT"));

		try {
			NOT_BEFORE_TIME = sdf.parse(properties.getProperty("NOT_BEFORE_TIME"));
			NOT_AFTER_TIME = sdf.parse(properties.getProperty("NOT_AFTER_TIME"));
			ISSUED_TIME = sdf.parse(properties.getProperty("ISSUED_TIME"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		DEFAULTHOLDERANDISSUER = new X500Principal(properties.getProperty("DEFAULTHOLDERANDISSUER"));
		MACHINECODE = properties.getProperty("MACHINECODE");
		INFO = properties.getProperty("INFO");

	}

	/**
	 * license参数
	 * @return
	 */
	private LicenseParam initLicenseParams() {
		Preferences preferences = Preferences.userNodeForPackage(LicenseGenerate.class);
		CipherParam cipherParam = new DefaultCipherParam(PRIVATE_STORE_PASSWORD);
		KeyStoreParam privateSoreParam = new DefaultKeyStoreParam(LicenseGenerate.class, PRIVATE_STORE_PATH,
				PRIVATE_KEY_ALIAS, PRIVATE_STORE_PASSWORD, KEY_PASSWORD);
		LicenseParam licenseParam = new DefaultLicenseParam(SUBJECT, preferences, privateSoreParam, cipherParam);
		return licenseParam;
	}

	/**
	 * license信息
	 * @return
	 */
	private final LicenseContent configuerLicenseContent() {
		LicenseContent content = new LicenseContent();
		content.setSubject(SUBJECT);
		content.setHolder(DEFAULTHOLDERANDISSUER);
		content.setIssuer(DEFAULTHOLDERANDISSUER);
		content.setIssued(ISSUED_TIME);
		content.setNotBefore(NOT_BEFORE_TIME);
		content.setNotAfter(NOT_AFTER_TIME);
		content.setExtra(MACHINECODE + VALID_TIME);
		content.setConsumerType(CONSUMER_TYPE);
		content.setConsumerAmount(CONSUMER_AMOUNT);
		content.setInfo(INFO);
		return content;
	}

	/**
	 * 创建license证书
	 *
	 * @return
	 */
	public boolean generate() {
		LicenseManager licenseManager = LicenseManagerHolder.getLicenseManager(initLicenseParams());
		try {
			licenseManager.store(configuerLicenseContent(), new File(LICENSE_OUTPUT_PATH));
		} catch (Exception e) {
			System.out.println("license生成失败");
			e.printStackTrace();
			return false;
		}
		System.out.println("license生成成功");
		return true;
	}

	/**
	 * jar包测试
	 * @param args
	 */
	public static void main(String[] args) {
		LicenseGenerate licenseGenerate = new LicenseGenerate();
		//获取jar包路径
		String jarPath = LicenseGenerate.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if(jarPath.endsWith(".jar") || jarPath.endsWith(".jar!"))
			jarPath = jarPath.substring(0, jarPath.lastIndexOf("/") + 1);
		String dirPath = jarPath + (jarPath.endsWith("/") ? "" : "/") + "config";
		InputStream in = null;
		try {
			in = new FileInputStream(dirPath + "/generate.properties");
		} catch (FileNotFoundException e) {
			System.out.println("读取配置文件失败");
		}
		licenseGenerate.loadProperties(in);
		// de.schlichtherle.license.KeyStoreParam
		// de.schlichtherle.license.AbstractKeyStoreParam
		// de.schlichtherle.license.DefaultKeyStoreParam
		// 密钥库文件不在项目内会报FileNotFound，源码 AbstractKeyStoreParam 类 getStream()方法，getResourceAsStream()在工程内获取
		// 修改了源码
		licenseGenerate.generate();
		try {
			if (in != null) {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
