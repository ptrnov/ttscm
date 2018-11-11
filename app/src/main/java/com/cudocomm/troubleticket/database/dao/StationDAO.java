package com.cudocomm.troubleticket.database.dao;

import com.cudocomm.troubleticket.database.DatabaseHelper;
import com.cudocomm.troubleticket.database.model.StationModel;
import com.cudocomm.troubleticket.util.Logcat;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class StationDAO {
	
    private static Dao<StationModel, Integer> getDao() throws SQLException {
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		return databaseHelper.getStationDao();
	}
	
	
	public static int refresh(StationModel asetEmasModel) throws SQLException
	{
		Dao<StationModel, Integer> dao = getDao();
		return dao.refresh(asetEmasModel);
	}
	
	
	public static int create(StationModel asetEmasModel) throws SQLException
	{
		Dao<StationModel, Integer> dao = getDao();
		return dao.create(asetEmasModel);
	}

	public static long count() throws SQLException {
		Dao<StationModel, Integer> dao = getDao();
		return dao.countOf();
	}
	
	
	public static StationModel read(Integer id) throws SQLException
	{
		Dao<StationModel, Integer> dao = getDao();
		return dao.queryForId(id);
	}

	public static StationModel readById(Integer stationId) throws SQLException {
		Dao<StationModel, Integer> dao = getDao();
		List<StationModel> stationModels;
		try {
			stationModels = dao.queryForEq(StationModel.STATION_ID, stationId);
			if(!stationModels.isEmpty()) {
				return stationModels.get(0);
			}
		} catch (SQLException e) {
			Logcat.e("DATA NOT FOUND");
		}
		return null;
//		return dao.queryForEq(StationModel.NAME, name).get(0);
	}

	public static StationModel readByName(String name) throws SQLException {
		Dao<StationModel, Integer> dao = getDao();
		List<StationModel> stationModels;
		try {
			stationModels = dao.queryForEq(StationModel.STATION_NAME, name);
			if(!stationModels.isEmpty()) {
				return stationModels.get(0);
			}
		} catch (SQLException e) {
			Logcat.e("DATA NOT FOUND");
		}
		return null;
//		return dao.queryForEq(StationModel.NAME, name).get(0);
	}

	public static List<StationModel> readAll(long skip, long take) throws SQLException {
		Dao<StationModel, Integer> dao = getDao();
		List<StationModel> list;
		if(skip==-1l && take==-1l)
		{
			QueryBuilder<StationModel, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.where();
			queryBuilder.orderBy(StationModel.ID, true);
			list = dao.query(queryBuilder.prepare());
		}
		else
		{
			QueryBuilder<StationModel, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(StationModel.ID, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}

	public static int update(StationModel category) throws SQLException
	{
		Dao<StationModel, Integer> dao = getDao();
		return dao.update(category);
	}
	
	
	public static int delete(Integer id) throws SQLException
	{
		Dao<StationModel, Integer> dao = getDao();
		return dao.deleteById(id);
	}
	
	
	public static int deleteAll() throws SQLException
	{
		Dao<StationModel, Integer> dao = getDao();
		DeleteBuilder<StationModel, Integer> deleteBuilder = dao.deleteBuilder();
		return dao.delete(deleteBuilder.prepare());
	}

}
