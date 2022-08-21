package me.beaturing.license;

import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;

public class LicenseManagerHolder {
	private static LicenseManager licenseManager;

	public static synchronized LicenseManager getLicenseManager(LicenseParam licenseParams) {
		if (licenseManager == null) {
			licenseManager = new LicenseManager(licenseParams);
		}
		return licenseManager;
	}
}
