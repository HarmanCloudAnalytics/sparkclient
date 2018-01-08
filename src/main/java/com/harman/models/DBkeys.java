package com.harman.models;

public interface DBkeys {


	public String harmanDevice = "harmanDevice";
	public String macAddress = "macAddress", productId = "productId", colorId = "colorId", productName = "productName",
			colorName = "colorName", FirmwareVersion = "FirmwareVersion", AppVersion = "AppVersion";

	public String DeviceAnalytics = "DeviceAnalytics";
	public String Broadcaster = "Broadcaster", Receiver = "Receiver",
			CriticalTemperatureShutDown = "CriticalTemperatureShutDown", PowerOnOffCount = "PowerOnOffCount",
			EQSettings = "EQSettings", PowerBankUsage = "PowerBankUsage";

	public String AppAnalytics = "AppAnalytics";
	public String SpeakerMode_Stereo = "SpeakerMode_Stereo", SpeakerMode_Party = "SpeakerMode_Party",
			SpeakerMode_Single = "SpeakerMode_Single";
	public String AppToneToggle = "AppToneToggle", AppMFBMode = "AppMFBMode";
	public String AppHFPToggle = "AppHFPToggle", AppEQMode = "AppEQMode";
	public static String TAG = "*****************[TA]";

}
