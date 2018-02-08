package com.harman.dbinsertion;

import java.sql.Connection;

import org.json.JSONException;
import org.json.JSONObject;

import com.harman.Model.AppModel.AppAnalyticModel;
import com.harman.Model.AppModel.DeviceAnalyticModel;
import com.harman.models.DBkeys;
import com.harman.models.HarmanDeviceModel;
import com.harman.utils.ErrorType;
import com.harman.utils.HarmanParser;

public class MariadbOperator implements DBkeys {

	private MariadbOperator() {

	}

	static MariadbOperator isInsertionIntoMariaDB = null;

	public static MariadbOperator getInstance() {
		if (isInsertionIntoMariaDB == null)
			isInsertionIntoMariaDB = new MariadbOperator();
		return isInsertionIntoMariaDB;
	}

	private int featureCounter = 0;

	public String insertIntoMariaDB(String record) {
		ErrorType errorType = ErrorType.NO_ERROR;
		JSONObject response = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(record);
			MariaDB mariaModel = MariaDB.getInstance();
			Connection connection = mariaModel.openConnection();
			HarmanParser harmanParser = new HarmanParser();
			HarmanDeviceModel deviceModel = null;
			try {

				deviceModel = harmanParser.getParseHarmanDevice(jsonObject.getJSONObject(harmanDevice));
				errorType = mariaModel.insertDeviceModel(deviceModel, connection);
				print("Inserted into harmanDevice Error Type =" + errorType.toString());
			} catch (JSONException e) {
				errorType = ErrorType.INVALID_JSON;
			}

			try {
				DeviceAnalyticModel deviceAnalyticsModel = harmanParser.parseDeviceAnalyticsModel(
						jsonObject.getJSONObject(DeviceAnalytics), deviceModel.getMacAddress(),
						deviceModel.getProductId());
				try {
					int num = deviceAnalyticsModel.getmDeviceAnaModelList().get(PowerOnOffCount) != null
							? deviceAnalyticsModel.getmDeviceAnaModelList().get(PowerOnOffCount)
							: deviceAnalyticsModel.getmDeviceAnaModelList().get("PowerONCount");
					updateFeatureCounter(num);
				} catch (Exception e) {
					print("Feature counter failed.");
				}
				errorType = mariaModel.insertDeviceAnalytics(deviceAnalyticsModel, connection);
				print("Inserted into DeviceAnalytics Error Type =" + errorType.toString());
			} catch (JSONException e) {
				errorType = ErrorType.INVALID_JSON;
			}

			try {
				AppAnalyticModel appAnalyticsModel = harmanParser.parseAppAnalyticsModel(
						jsonObject.getJSONObject(AppAnalytics), deviceModel.getMacAddress(),
						deviceModel.getProductId());
				errorType = mariaModel.insertAppAnalytics(appAnalyticsModel, connection);
				print("Inserted into AppAnalytics Error Type =" + errorType.toString());
			} catch (JSONException e) {
				errorType = ErrorType.INVALID_JSON;
			}

			switch (errorType) {
			case NO_ERROR:
				response.put("Status", 1);
				break;

			default:
				response.put("Status", 0);
				break;
			}
			response.put("cmd", "UpdateSmartAudioAnalyticsRes");
		} catch (Exception e) {
			response.put("Status", 0);
			response.put("cmd", "UpdateSmartAudioAnalyticsRes");
		} finally {
			MariaDB.getInstance().closeConnection();
		}
		System.out.println(errorType.name());
		return response.toString();
	}

	public int getFeatureCounter() {
		return featureCounter;
	}

	public void updateFeatureCounter(int featureCounter) {
		featureCounter += featureCounter;
	}

	public void resetFeatureCounter() {
		featureCounter = 0;
	}

	public void print(String message) {
		System.out.println(message);
	}

}