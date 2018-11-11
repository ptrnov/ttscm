package com.cudocomm.troubleticket.database.dao;

import com.cudocomm.troubleticket.database.DatabaseHelper;
import com.cudocomm.troubleticket.database.model.Suspect3Model;
import com.cudocomm.troubleticket.util.Logcat;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class Suspect3DAO {
	
    private static Dao<Suspect3Model, Integer> getDao() throws SQLException {
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		return databaseHelper.getSuspect3Dao();
	}
	
	
	public static int refresh(Suspect3Model asetEmasModel) throws SQLException
	{
		Dao<Suspect3Model, Integer> dao = getDao();
		return dao.refresh(asetEmasModel);
	}
	
	
	public static int create(Suspect3Model asetEmasModel) throws SQLException
	{
		Dao<Suspect3Model, Integer> dao = getDao();
		return dao.create(asetEmasModel);
	}

	public static long count() throws SQLException {
		Dao<Suspect3Model, Integer> dao = getDao();
		return dao.countOf();
	}
	
	
	public static Suspect3Model read(Integer id) throws SQLException
	{
		Dao<Suspect3Model, Integer> dao = getDao();
		return dao.queryForId(id);
	}

	public static Suspect3Model readById(Integer id) throws SQLException {
		Dao<Suspect3Model, Integer> dao = getDao();
		List<Suspect3Model> suspect1Models;
		try {
			suspect1Models = dao.queryForEq(Suspect3Model.SUSPECT_ID, id);
			if(!suspect1Models.isEmpty()) {
				return suspect1Models.get(0);
			}
		} catch (SQLException e) {
			Logcat.e("DATA NOT FOUND");
		}
		return null;
//		return dao.queryForEq(StationModel.NAME, name).get(0);
	}

	public static Suspect3Model readByName(String name) throws SQLException {
		Dao<Suspect3Model, Integer> dao = getDao();
		List<Suspect3Model> suspect3Models;
		try {
			suspect3Models = dao.queryForEq(Suspect3Model.NAME, name);
			if(!suspect3Models.isEmpty()) {
				return suspect3Models.get(0);
			}
		} catch (SQLException e) {
			Logcat.e("DATA NOT FOUND");
		}
		return null;
//		return dao.queryForEq(Suspect1Model.NAME, name).get(0);
	}

	public static List<Suspect3Model> readAll(long skip, long take) throws SQLException {
		Dao<Suspect3Model, Integer> dao = getDao();
		List<Suspect3Model> list;
		if(skip==-1l && take==-1l)
		{
			QueryBuilder<Suspect3Model, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.where();
			queryBuilder.orderBy(Suspect3Model.SUSPECT_ID, true);
			list = dao.query(queryBuilder.prepare());
		}
		else
		{
			QueryBuilder<Suspect3Model, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(Suspect3Model.SUSPECT_ID, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}

	public static List<Suspect3Model> readAllByParent(long skip, long take, Integer parentId) throws SQLException {
		Dao<Suspect3Model, Integer> dao = getDao();
		List<Suspect3Model> list;
		if(skip==-1l && take==-1l)
		{
			QueryBuilder<Suspect3Model, Integer> queryBuilder = dao.queryBuilder();
			if (parentId != null) {
				queryBuilder
						.where()
						.eq(Suspect3Model.PARENT, parentId);
			}
			queryBuilder.orderBy(Suspect3Model.SUSPECT_ID, true);
			list = dao.query(queryBuilder.prepare());
		} else {
			QueryBuilder<Suspect3Model, Integer> queryBuilder = dao.queryBuilder();
			if (parentId != null) {
				queryBuilder
						.where()
						.eq(Suspect3Model.PARENT, parentId);
			}
			queryBuilder.orderBy(Suspect3Model.SUSPECT_ID, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}
	
	
	public static int update(Suspect3Model category) throws SQLException
	{
		Dao<Suspect3Model, Integer> dao = getDao();
		return dao.update(category);
	}
	
	
	public static int delete(Integer id) throws SQLException
	{
		Dao<Suspect3Model, Integer> dao = getDao();
		return dao.deleteById(id);
	}
	
	
	public static int deleteAll() throws SQLException
	{
		Dao<Suspect3Model, Integer> dao = getDao();
		DeleteBuilder<Suspect3Model, Integer> deleteBuilder = dao.deleteBuilder();
		return dao.delete(deleteBuilder.prepare());
	}

}
