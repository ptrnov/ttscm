package com.cudocomm.troubleticket.database;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.database.model.Penyebab;
import com.cudocomm.troubleticket.database.model.Program;
import com.cudocomm.troubleticket.database.model.SeverityModel;
import com.cudocomm.troubleticket.database.model.SeverityUpdateModel;
import com.cudocomm.troubleticket.database.model.StationModel;
import com.cudocomm.troubleticket.database.model.Suspect1Model;
import com.cudocomm.troubleticket.database.model.Suspect2Model;
import com.cudocomm.troubleticket.database.model.Suspect3Model;
import com.cudocomm.troubleticket.database.model.Suspect4Model;
import com.cudocomm.troubleticket.database.model.SuspectModel;
import com.cudocomm.troubleticket.database.model.UserLoginModel;
import com.cudocomm.troubleticket.util.Logcat;
import com.cudocomm.troubleticket.util.Preferences;
import com.cudocomm.troubleticket.util.SessionManager;
import com.cudocomm.troubleticket.util.TTSConfig;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = TTSConfig.DATABASE_NAME;
    private static final String DATABASE_PATH = "/data/data/" + TTSApplication.getContext().getPackageName() + "/databases/";
//    private static final String DATABASE_PATH = Environment.getExternalStorageDirectory() + File.separator + TTSApplication.getContext().getPackageName() + "/databases/";
	private static final int DATABASE_VERSION = TTSConfig.DATABASE_VERSION;
	private static final String PREFS_KEY_DATABASE_VERSION = "database_version";

	private Dao<SeverityModel, Integer> severityDao = null;
	private Dao<SeverityUpdateModel, Integer> severityUpdateDao = null;
	private Dao<SuspectModel, Integer> suspectDao = null;

	private Dao<Suspect1Model, Integer> suspect1Dao = null;
	private Dao<Suspect2Model, Integer> suspect2Dao = null;
	private Dao<Suspect3Model, Integer> suspect3Dao = null;
	private Dao<Suspect4Model, Integer> suspect4Dao = null;
	private Dao<UserLoginModel, Integer> userDao = null;
	private Dao<StationModel, Integer> stationDao = null;

	private Dao<Program, Integer> programDao = null;
	private Dao<Penyebab, Integer> penyebabDao = null;

	Preferences preferences;
	SessionManager sessionManager;

	// singleton
	private static DatabaseHelper instance;
	public static synchronized DatabaseHelper getInstance() {
		if(instance==null) instance = new DatabaseHelper();
		return instance;
	}


	private DatabaseHelper() {
		super(TTSApplication.getContext(), DATABASE_PATH + DATABASE_NAME, null, DATABASE_VERSION);
		preferences = new Preferences(TTSApplication.getContext());
		sessionManager = new SessionManager(TTSApplication.getContext());
		if(!databaseExists() || DATABASE_VERSION > getVersion()) {
			synchronized(this) {
				sessionManager.logoutUser();
				preferences.clearAllPreferences();
				/*boolean success = copyPrepopulatedDatabase();
				if(success) {
					setVersion(DATABASE_VERSION);
				}*/
				setVersion(DATABASE_VERSION);
			}
		}
	}


	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
	{
		try {
			Logcat.d("DatabaseHelper.onCreate()");
			TableUtils.createTable(connectionSource, SeverityModel.class);
			TableUtils.createTable(connectionSource, SeverityUpdateModel.class);
			TableUtils.createTable(connectionSource, SuspectModel.class);

			TableUtils.createTable(connectionSource, Suspect1Model.class);
			TableUtils.createTable(connectionSource, Suspect2Model.class);
			TableUtils.createTable(connectionSource, Suspect3Model.class);
			TableUtils.createTable(connectionSource, Suspect4Model.class);
			TableUtils.createTable(connectionSource, UserLoginModel.class);
			TableUtils.createTable(connectionSource, StationModel.class);
			/*TableUtils.createTable(connectionSource, SettingModel.class);
			TableUtils.createTable(connectionSource, UserLoginModel.class);
			TableUtils.createTable(connectionSource, KanwilModel.class);
			TableUtils.createTable(connectionSource, KondisiModel.class);
			TableUtils.createTable(connectionSource, StatusModel.class);
			TableUtils.createTable(connectionSource, FullUnitModel.class);
			TableUtils.createTable(connectionSource, AsetModel.class);
			TableUtils.createTable(connectionSource, SeverityModel.class);
			TableUtils.createTable(connectionSource, JadwalSurveyModel.class);
			TableUtils.createTable(connectionSource, JenisLogamModel.class);
			TableUtils.createTable(connectionSource, AsetUnregisterModel.class);
			TableUtils.createTable(connectionSource, KategoriModel.class);*/
		}
		catch(SQLException e)
		{
			Logcat.e("DatabaseHelper.onCreate(): can't create database", e);
			e.printStackTrace();
		}
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try
		{
			Logcat.d("DatabaseHelper.onUpgrade()");
			if(oldVersion < newVersion) {
				sessionManager.logoutUser();
				preferences.clearAllPreferences();
				clearDatabase();
			}
		}
		catch(android.database.SQLException e)
		{
			Logcat.e("DatabaseHelper.onUpgrade(): can't upgrade database", e);
			e.printStackTrace();
		}
	}


	@Override
	public void close() {
		super.close();
		severityDao = null;
		severityUpdateDao = null;
		suspectDao = null;

		suspect1Dao = null;
		suspect2Dao = null;
		suspect3Dao = null;
		suspect4Dao = null;
		userDao = null;
		stationDao = null;
		programDao = null;
		penyebabDao = null;
		/*settingDao = null;
		userLoginDao = null;
		kanwilDao = null;

		kondisiDao = null;
		settingDao = null;

		fullUnitDao = null;
		asetDao = null;
		asetEmasDao = null;
		jadwalSurveyDao = null;
		jenisLogamDao = null;
		asetUnregisterDao = null;
		kategoriDao = null;*/
	}


	public synchronized void clearDatabase() {
		try
		{
			Logcat.d("DatabaseHelper.clearDatabase()");
			TableUtils.createTableIfNotExists(getConnectionSource(), SeverityModel.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), SeverityUpdateModel.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), SuspectModel.class);

			TableUtils.createTableIfNotExists(getConnectionSource(), Suspect1Model.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), Suspect2Model.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), Suspect3Model.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), Suspect4Model.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), UserLoginModel.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), StationModel.class);
/*
			TableUtils.createTableIfNotExists(getConnectionSource(), SettingModel.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), UserLoginModel.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), KanwilModel.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), KondisiModel.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), StatusModel.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), FullUnitModel.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), AsetModel.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), JadwalSurveyModel.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), JenisLogamModel.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), AsetUnregisterModel.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), SeverityModel.class);
			TableUtils.createTableIfNotExists(getConnectionSource(), KategoriModel.class);*/
		} catch(SQLException e) {
			Logcat.e("DatabaseHelper.clearDatabase(): can't clear database", e);
			e.printStackTrace();
		}
	}

	public synchronized void commitClearDatabase() {
		try
		{
			Logcat.d("DatabaseHelper.clearDatabase()");

			TableUtils.dropTable(getConnectionSource(), SeverityModel.class, true);
			TableUtils.dropTable(getConnectionSource(), SeverityUpdateModel.class, true);
			TableUtils.dropTable(getConnectionSource(), SuspectModel.class, true);

			TableUtils.dropTable(getConnectionSource(), Suspect1Model.class, true);
			TableUtils.dropTable(getConnectionSource(), Suspect2Model.class, true);
			TableUtils.dropTable(getConnectionSource(), Suspect3Model.class, true);
			TableUtils.dropTable(getConnectionSource(), Suspect4Model.class, true);
			TableUtils.dropTable(getConnectionSource(), UserLoginModel.class, true);
			TableUtils.dropTable(getConnectionSource(), StationModel.class, true);
		} catch(SQLException e) {
			Logcat.e("DatabaseHelper.clearDatabase(): can't clear database", e);
			e.printStackTrace();
		}
	}

	public synchronized Dao<SeverityModel, Integer> getSeverityDao() throws SQLException {
		if(severityDao==null) {
			severityDao = getDao(SeverityModel.class);
		}
		return severityDao;
	}

	public synchronized Dao<SeverityUpdateModel, Integer> getSeverityUpdateDao() throws SQLException {
		if(severityUpdateDao==null) {
			severityUpdateDao = getDao(SeverityUpdateModel.class);
		}
		return severityUpdateDao;
	}

	public synchronized Dao<SuspectModel, Integer> getSuspectDao() throws SQLException {
		if(suspectDao==null) {
			suspectDao = getDao(SuspectModel.class);
		}
		return suspectDao;
	}

	public synchronized Dao<Suspect1Model, Integer> getSuspect1Dao() throws SQLException {
		if(suspect1Dao==null) {
			suspect1Dao = getDao(Suspect1Model.class);
		}
		return suspect1Dao;
	}

	public synchronized Dao<Suspect2Model, Integer> getSuspect2Dao() throws SQLException {
		if(suspect2Dao==null) {
			suspect2Dao = getDao(Suspect2Model.class);
		}
		return suspect2Dao;
	}

	public synchronized Dao<Suspect3Model, Integer> getSuspect3Dao() throws SQLException {
		if(suspect3Dao==null) {
			suspect3Dao = getDao(Suspect3Model.class);
		}
		return suspect3Dao;
	}

	public synchronized Dao<Suspect4Model, Integer> getSuspect4Dao() throws SQLException {
		if(suspect4Dao==null) {
			suspect4Dao = getDao(Suspect4Model.class);
		}
		return suspect4Dao;
	}

	public synchronized Dao<UserLoginModel, Integer> getUserDao() throws SQLException {
		if(userDao==null) {
			userDao = getDao(UserLoginModel.class);
		}
		return userDao;
	}

	public synchronized Dao<StationModel, Integer> getStationDao() throws SQLException {
		if(stationDao==null) {
			stationDao = getDao(StationModel.class);
		}
		return stationDao;
	}

	private boolean databaseExists() {
		File file = new File(DATABASE_PATH + DATABASE_NAME);
		boolean exists = file.exists();
		Logcat.d("DatabaseHelper.databaseExists(): " + exists);
		return exists;
	}


	private boolean copyPrepopulatedDatabase() {
		// copy database from assets
		try
		{
			// create directories
			File dir = new File(DATABASE_PATH);
			dir.mkdirs();

			// output file name
			String outputFileName = DATABASE_PATH + DATABASE_NAME;
			Logcat.d("DatabaseHelper.copyDatabase(): " + outputFileName);

			// create streams
			InputStream inputStream = TTSApplication.getContext().getAssets().open(DATABASE_NAME);
			OutputStream outputStream = new FileOutputStream(outputFileName);

			// write input to output
			byte[] buffer = new byte[1024];
			int length;
			while((length = inputStream.read(buffer))>0)
			{
				outputStream.write(buffer, 0, length);
			}

			// close streams
			outputStream.flush();
			outputStream.close();
			inputStream.close();
			return true;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}


	private int getVersion() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TTSApplication.getContext());
		return sharedPreferences.getInt(PREFS_KEY_DATABASE_VERSION, 0);
	}


	private void setVersion(int version) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TTSApplication.getContext());
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(PREFS_KEY_DATABASE_VERSION, version);
		editor.commit();
	}

	public synchronized Dao<Program, Integer> getProgramDao() throws SQLException {
		if(programDao==null) {
			programDao = getDao(Program.class);
		}
		return programDao;
	}

	public synchronized Dao<Penyebab, Integer> getPenyebabDao() throws SQLException {
		if(penyebabDao==null) {
			penyebabDao = getDao(Penyebab.class);
		}
		return penyebabDao;
	}
}
