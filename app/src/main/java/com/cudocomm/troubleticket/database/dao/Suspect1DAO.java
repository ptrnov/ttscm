package com.cudocomm.troubleticket.database.dao;

import com.cudocomm.troubleticket.database.DatabaseHelper;
import com.cudocomm.troubleticket.database.model.Suspect1Model;
import com.cudocomm.troubleticket.util.Logcat;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class Suspect1DAO {
	
    private static Dao<Suspect1Model, Integer> getDao() throws SQLException {
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		return databaseHelper.getSuspect1Dao();
	}
	
	
	public static int refresh(Suspect1Model asetEmasModel) throws SQLException
	{
		Dao<Suspect1Model, Integer> dao = getDao();
		return dao.refresh(asetEmasModel);
	}
	
	
	public static int create(Suspect1Model asetEmasModel) throws SQLException
	{
		Dao<Suspect1Model, Integer> dao = getDao();
		return dao.create(asetEmasModel);
	}

	public static long count() throws SQLException {
		Dao<Suspect1Model, Integer> dao = getDao();
		return dao.countOf();
	}
	
	
	public static Suspect1Model read(Integer id) throws SQLException
	{
		Dao<Suspect1Model, Integer> dao = getDao();
		return dao.queryForId(id);
	}

	public static Suspect1Model readById(Integer id) throws SQLException {
		Dao<Suspect1Model, Integer> dao = getDao();
		List<Suspect1Model> suspect1Models;
		try {
			suspect1Models = dao.queryForEq(Suspect1Model.SUSPECT_ID, id);
			if(!suspect1Models.isEmpty()) {
				return suspect1Models.get(0);
			}
		} catch (SQLException e) {
			Logcat.e("DATA NOT FOUND");
		}
		return null;
//		return dao.queryForEq(StationModel.NAME, name).get(0);
	}

	public static Suspect1Model readByName(String name) throws SQLException {
		Dao<Suspect1Model, Integer> dao = getDao();
		List<Suspect1Model> suspect1Models;
		try {
			suspect1Models = dao.queryForEq(Suspect1Model.NAME, name);
			if(!suspect1Models.isEmpty()) {
				return suspect1Models.get(0);
			}
		} catch (SQLException e) {
			Logcat.e("DATA NOT FOUND");
		}
		return null;
//		return dao.queryForEq(Suspect1Model.NAME, name).get(0);
	}

	public static List<Suspect1Model> readAll(long skip, long take) throws SQLException {
		Dao<Suspect1Model, Integer> dao = getDao();
		List<Suspect1Model> list;
		if(skip==-1l && take==-1l)
		{
			QueryBuilder<Suspect1Model, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.where();
			queryBuilder.orderBy(Suspect1Model.SUSPECT_ID, true);
			list = dao.query(queryBuilder.prepare());
		}
		else
		{
			QueryBuilder<Suspect1Model, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(Suspect1Model.SUSPECT_ID, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}

	public static List<Suspect1Model> readAllByModule(long skip, long take, Integer moduleId) throws SQLException {
		Dao<Suspect1Model, Integer> dao = getDao();
		List<Suspect1Model> list;
		if(skip==-1l && take==-1l)
		{
			QueryBuilder<Suspect1Model, Integer> queryBuilder = dao.queryBuilder();
			if (moduleId != null) {
				queryBuilder
						.where()
						.eq(Suspect1Model.MODULE, moduleId);
			}
			queryBuilder.orderBy(Suspect1Model.SUSPECT_ID, true);
			list = dao.query(queryBuilder.prepare());
		} else {
			QueryBuilder<Suspect1Model, Integer> queryBuilder = dao.queryBuilder();
			if (moduleId != null) {
				queryBuilder
						.where()
						.eq(Suspect1Model.MODULE, moduleId);
			}
			queryBuilder.orderBy(Suspect1Model.SUSPECT_ID, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}

	public static int update(Suspect1Model category) throws SQLException
	{
		Dao<Suspect1Model, Integer> dao = getDao();
		return dao.update(category);
	}
	
	
	public static int delete(Integer id) throws SQLException
	{
		Dao<Suspect1Model, Integer> dao = getDao();
		return dao.deleteById(id);
	}
	
	
	public static int deleteAll() throws SQLException
	{
		Dao<Suspect1Model, Integer> dao = getDao();
		DeleteBuilder<Suspect1Model, Integer> deleteBuilder = dao.deleteBuilder();
		return dao.delete(deleteBuilder.prepare());
	}

}
