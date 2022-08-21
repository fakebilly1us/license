package me.beaturing.machine.common;


import me.beaturing.machine.utils.EncryptUtil;
import me.beaturing.machine.utils.FileUtil;
import me.beaturing.machine.utils.MachineInfoUtil;

import java.io.UnsupportedEncodingException;


public class MachineGenerate {

	public static void main(String[] args) {
		String machineInfos = MachineInfoUtil.getMachineInfos();
		//sigar初始化失败
		if ("failed".equals(machineInfos)) {
			FileUtil.deleteFiles();
			FileUtil.writeMachineNumber("Failed", "生成失败");
			return;
		}
		String machineSerial = EncryptUtil.encodeMD5(machineInfos);
		try {
			String machineNumber = EncryptUtil.encodeBase64(machineSerial.getBytes("UTF-8"));
			System.out.println(machineNumber);
			FileUtil.writeMachineNumber("MchineCode", machineNumber);
		} catch (UnsupportedEncodingException e) {
			FileUtil.writeMachineNumber("Failed", "生成失败");
		} finally {
			FileUtil.deleteFiles();
		}

	}
}
