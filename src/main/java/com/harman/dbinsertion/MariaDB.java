package com.harman.dbinsertion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.harman.Model.AppModel.AppAnalyticModel;
import com.harman.Model.AppModel.DeviceAnalyticModel;
import com.harman.models.DBkeys;
import com.harman.models.HarmanDeviceModel;
import com.harman.models.PRODUCT_TYPE;
import com.harman.utils.ErrorType;

public class MariaDB implements DBkeys {

	static MariaDB mariaModel;

	public static MariaDB getInstance() {
		if (mariaModel == null)
			mariaModel = new MariaDB();
		return mariaModel;
	}

	Connection connn = null;

	public Connection openConnection() {
		if (connn != null)
			return connn;
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			System.out.println("Connecting to a selected database...");
			connn = DriverManager.getConnection("jdbc:mariadb://10.0.0.5/DEVICE_INFO_STORE", "root", "");
			System.out.println("Connected database successfully...");
		} catch (SQLException e) {
			System.out.println("SQLException " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
		}
		return connn;
	}

	public void closeConnection() {
		try {
			if (connn != null) {
				connn.close();
			}
			connn=null;
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public ErrorType insertDeviceModel(HarmanDeviceModel mHarmanDeviceModel, Connection conn) {
		ErrorType response = ErrorType.NO_ERROR;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String query = "select * from " + harmanDevice + " where " + macAddress + " = " + "'"
					+ mHarmanDeviceModel.getMacAddress() + "'";
			ResultSet ifExistsResponse = stmt.executeQuery(query);
			ifExistsResponse.last();
			if (ifExistsResponse.getRow() == 0) {
				try {
					String queryInsertNewRow = "INSERT INTO " + harmanDevice + "(" + macAddress + "," + productId + ","
							+ colorId + "," + productName + "," + colorName + "," + FirmwareVersion + "," + AppVersion
							+ ") VALUE ('" + mHarmanDeviceModel.getMacAddress() + "','"
							+ mHarmanDeviceModel.getProductId() + "','" + mHarmanDeviceModel.getColorId() + "','"
							+ mHarmanDeviceModel.getProductName() + "','" + mHarmanDeviceModel.getColorName() + "','"
							+ mHarmanDeviceModel.getFirmwareVersion() + "','" + mHarmanDeviceModel.getAppVersion()
							+ "')";
					int result = stmt.executeUpdate(queryInsertNewRow);
					if (result == 0)
						response = ErrorType.ERROR_INSERTING_DB;
				} catch (SQLException se) {
					response = ErrorType.ERROR_INSERTING_DB;
				}
			} else {
				try {
					String queryUpdate = "update " + harmanDevice + " set " + FirmwareVersion + "= '"
							+ mHarmanDeviceModel.getFirmwareVersion() + "'," + AppVersion + " = '"
							+ mHarmanDeviceModel.getAppVersion() + "' where " + macAddress + " = '"
							+ mHarmanDeviceModel.getMacAddress() + "'";
					int result = stmt.executeUpdate(queryUpdate);
					if (result == 0)
						response = ErrorType.ERROR_UPDATING_DB;
				} catch (SQLException se) {
					response = ErrorType.ERROR_UPDATING_DB;
				}
			}
		} catch (Exception e) {
			response = ErrorType.NETWORK_NOT_AVAILBLE;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se) {
				response = ErrorType.ERROR_CLOSING_DB;
				System.out.println("SQLException while closing data");
			}
		}
		return response;
	}

	@SuppressWarnings("rawtypes")
	public StringBuffer createQuery(LinkedHashMap<String, Integer> keyValueMap, String table, String macAddress) {
		Iterator<Entry<String, Integer>> it = keyValueMap.entrySet().iterator();
		StringBuffer queryBuffer = new StringBuffer();

		StringBuffer bufferKey = new StringBuffer();
		StringBuffer bufferValue = new StringBuffer();

		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			if (it.hasNext()) {
				bufferKey.append(pair.getKey() + ",");
				bufferValue.append(pair.getValue() + ",");
			} else {
				bufferKey.append(pair.getKey());
				bufferValue.append(pair.getValue());
			}
			System.out.println(pair.getKey() + " = " + pair.getValue());
			it.remove(); // avoids a ConcurrentModificationException
		}
		queryBuffer.append("INSERT INTO " + table + "(harmanDevice_Id," + bufferKey + ") VALUE ( '" + macAddress + "',"
				+ bufferValue + " )");

		System.out.println(queryBuffer);
		return queryBuffer;
	}

	public ErrorType insertAppAnalytics(AppAnalyticModel mAppAnalyticsModel, Connection conn) {
		ErrorType response = ErrorType.NO_ERROR;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			try {
				String tableName = mAppAnalyticsModel.getDeviceType() == PRODUCT_TYPE.BOOMBOX ? BoomboxAppAnalytics
						: JBLXtremeCLAppAnalytics;
				String queryInsertNewRow = createQuery(mAppAnalyticsModel.getmDeviceAnaModelList(), tableName,
						mAppAnalyticsModel.getMacaddress()).toString();
				int result = stmt.executeUpdate(queryInsertNewRow);
				if (result == 0)
					response = ErrorType.ERROR_INSERTING_DB;
			} catch (SQLException se) {
				response = ErrorType.ERROR_INSERTING_DB;
			}

		} catch (Exception e) {
			response = ErrorType.NETWORK_NOT_AVAILBLE;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se) {
				response = ErrorType.ERROR_CLOSING_DB;
				System.out.println("SQLException while closing data");
			}
		}
		System.out.println(response.toString() + " insertAppAnalytics ");
		return response;
	}

	public ErrorType inserEmailCounter(int counter, Connection conn) {
		ErrorType response = ErrorType.NO_ERROR;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			try {
				String tableName = "PowerONOFF";
				String queryInsertNewRow = "INSERT INTO " + tableName + "( Counter ) VALUE (" + counter + ")";
				int result = stmt.executeUpdate(queryInsertNewRow);
				if (result == 0)
					response = ErrorType.ERROR_INSERTING_DB;
			} catch (SQLException se) {
				response = ErrorType.ERROR_INSERTING_DB;
			}

		} catch (Exception e) {
			response = ErrorType.NETWORK_NOT_AVAILBLE;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se) {
				response = ErrorType.ERROR_CLOSING_DB;
				System.out.println("SQLException while closing data");
			}
		}
		return response;
	}

	public ErrorType insertDeviceAnalytics(DeviceAnalyticModel mDeviceAnalyticsModel, Connection conn) {
		ErrorType response = ErrorType.NO_ERROR;
		Statement stmt = null;
		try {
			String tableName = mDeviceAnalyticsModel.getDeviceType() == PRODUCT_TYPE.BOOMBOX ? BoomboxDeviceAnalytics
					: JBLXtremeCLDeviceAnalytics;
			stmt = conn.createStatement();
			try {
				String queryInsertNewRow = createQuery(mDeviceAnalyticsModel.getmDeviceAnaModelList(), tableName,
						mDeviceAnalyticsModel.getMacaddress()).toString();
				int result = stmt.executeUpdate(queryInsertNewRow);
				if (result == 0)
					response = ErrorType.ERROR_INSERTING_DB;
			} catch (SQLException se) {
				response = ErrorType.ERROR_INSERTING_DB;
			}
		} catch (Exception e) {
			response = ErrorType.NETWORK_NOT_AVAILBLE;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se) {
				response = ErrorType.ERROR_CLOSING_DB;
				System.out.println("SQLException while closing data");
			}
		}
		System.out.println(response.toString() + " insertDeviceAnalytics ");
		return response;
	}
}
