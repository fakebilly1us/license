package me.beaturing.machine.utils;

import org.hyperic.sigar.*;


/**
 *  机器硬件信息工具类
 */
public class MachineInfoUtil {

	private static Sigar sigar;

	/**
	 * cpu
	 * @return
	 */
	private static String getCPUSerial() {
		StringBuilder cpuStr = new StringBuilder();
		try {
			CpuInfo[] cpuInfoArr = sigar.getCpuInfoList();
			for (CpuInfo info : cpuInfoArr) {
				//CPU-MHz CPU-Vendor CPU-Model CPU-CacheSize
				cpuStr.append(info.getMhz()).append(info.getVendor()).append(info.getModel()).append(info.getCacheSize()).append(";");
			}
		} catch (SigarException e) {
			return "unknow";
		}
		return cpuStr.toString().trim().toUpperCase();
	}

	/**
	 * 硬盘
	 * @return
	 */
	private static String getDiskSerial() {
		StringBuilder diskStr = new StringBuilder();
		try {
			FileSystem[] fileSystemArr = sigar.getFileSystemList();
			for (FileSystem fileSystem : fileSystemArr) {
				diskStr.append(fileSystem.getFlags()).append(";");
			}
		} catch (SigarException e) {
			return "unknow";
		}
		return diskStr.toString().trim().toUpperCase();
	}

	/**
	 * mac地址
	 * @return
	 */
	private static String getMACAddress() {
		StringBuilder macStr = new StringBuilder();
		try {
			String[] netInterfaceArr = sigar.getNetInterfaceList();
			for (String netInterfaceStr : netInterfaceArr) {
				NetInterfaceConfig config = sigar.getNetInterfaceConfig(netInterfaceStr);
			if (NetFlags.LOOPBACK_ADDRESS.equals(config.getAddress()) || (config.getFlags() & NetFlags.IFF_LOOPBACK) != 0
				|| NetFlags.NULL_HWADDR.equals(config.getHwaddr()))
				continue;
				macStr.append(config.getHwaddr()).append(";");
			}
		} catch (SigarException e) {
			return "unknow";
		}
		return macStr.toString().trim().toUpperCase();
	}

	/**
	 * 操作系统
	 * @return
	 */
	private static String getOSInfo() {
		OperatingSystem OS = OperatingSystem.getInstance();
		StringBuilder osStr = new StringBuilder();
		// 操作系统,操作系统CpuEndian,操作系统DataModel,系统描述,操作系统的卖主,卖主名称,操作系统名称,操作系统卖主类型,操作系统的版本号
		osStr.append(OS.getArch()).append(";").append(OS.getCpuEndian()).append(";").append(OS.getDataModel()).append(";");
		osStr.append(OS.getDescription()).append(";").append(OS.getVendor()).append(";").append(OS.getVendorCodeName()).append(";");
		osStr.append(OS.getVendorName()).append(";").append(OS.getVendorVersion()).append(";").append(OS.getVersion());
		return osStr.toString().trim().toUpperCase();
	}

	public static String getMachineInfos() {
		sigar = SigarUtil.sigarInit();
		if (sigar == null)
			return "failed";
		String cpuSerial = getCPUSerial();
		String diskSerial = getDiskSerial();
		String macAddress = getMACAddress();
		String osInfo = getOSInfo();
		StringBuilder text = new StringBuilder();
		text.append(cpuSerial).append(diskSerial).append(macAddress).append(osInfo);
		return text.toString();
	}

}
