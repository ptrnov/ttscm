package com.cudocomm.troubleticket.database.dao;

import com.cudocomm.troubleticket.database.DatabaseHelper;
import com.cudocomm.troubleticket.database.model.Suspect4Model;
import com.cudocomm.troubleticket.util.Logcat;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class Suspect4DAO {
	
    private static Dao<Suspect4Model, Integer> getDao() throws SQLException {
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		return databaseHelper.getSuspect4Dao();
	}
	
	
	public static int refresh(Suspect4Model asetEmasModel) throws SQLException
	{
		Dao<Suspect4Model, Integer> dao = getDao();
		return dao.refresh(asetEmasModel);
	}
	
	
	public static int create(Suspect4Model asetEmasModel) throws SQLException
	{
		Dao<Suspect4Model, Integer> dao = getDao();
		return dao.create(asetEmasModel);
	}

	public static long count() throws SQLException {
		Dao<Suspect4Model, Integer> dao = getDao();
		return dao.countOf();
	}
	
	
	public static Suspect4Model read(Integer id) throws SQLException
	{
		Dao<Suspect4Model, Integer> dao = getDao();
		return dao.queryForId(id);
	}

	public static Suspect4Model readById(Integer id) throws SQLException {
		Dao<Suspect4Model, Integer> dao = getDao();
		List<Suspect4Model> suspect1Models;
		try {
			suspect1Models = dao.queryForEq(Suspect4Model.SUSPECT_ID, id);
			if(!suspect1Models.isEmpty()) {
				return suspect1Models.get(0);
			}
		} catch (SQLException e) {
			Logcat.e("DATA NOT FOUND");
		}
		return null;
//		return dao.queryForEq(StationModel.NAME, name).get(0);
	}

	public static Suspect4Model readByName(String name) throws SQLException {
		Dao<Suspect4Model, Integer> dao = getDao();
		List<Suspect4Model> suspect4Models;
		try {
			suspect4Models = dao.queryForEq(Suspect4Model.NAME, name);
			if(!suspect4Models.isEmpty()) {
				return suspect4Models.get(0);
			}
		} catch (SQLException e) {
			Logcat.e("DATA NOT FOUND");
		}
		return null;
//		return dao.queryForEq(Suspect1Model.NAME, name).get(0);
	}

	public static List<Suspect4Model> readAll(long skip, long take) throws SQLException {
		Dao<Suspect4Model, Integer> dao = getDao();
		List<Suspect4Model> list;
		if(skip==-1l && take==-1l)
		{
			QueryBuilder<Suspect4Model, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.where();
			queryBuilder.orderBy(Suspect4Model.ID, true);
			list = dao.query(queryBuilder.prepare());
		}
		else
		{
			QueryBuilder<Suspect4Model, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(Suspect4Model.SUSPECT_ID, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}

	public static List<Suspect4Model> readAllByParent(long skip, long take, Integer parentId) throws SQLException {
		Dao<Suspect4Model, Integer> dao = getDao();
		List<Suspect4Model> list;
		if(skip==-1l && take==-1l)
		{
			QueryBuilder<Suspect4Model, Integer> queryBuilder = dao.queryBuilder();
			if (parentId != null) {
				queryBuilder
						.where()
						.eq(Suspect4Model.PARENT, parentId);
			}
			queryBuilder.orderBy(Suspect4Model.SUSPECT_ID, true);
			list = dao.query(queryBuilder.prepare());
		} else {
			QueryBuilder<Suspect4Model, Integer> queryBuilder = dao.queryBuilder();
			if (parentId != null) {
				queryBuilder
						.where()
						.eq(Suspect4Model.PARENT, parentId);
			}
			queryBuilder.orderBy(Suspect4Model.SUSPECT_ID, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}
	
	
	public static int update(Suspect4Model category) throws SQLException
	{
		Dao<Suspect4Model, Integer> dao = getDao();
		return dao.update(category);
	}
	
	
	public static int delete(Integer id) throws SQLException
	{
		Dao<Suspect4Model, Integer> dao = getDao();
		return dao.deleteById(id);
	}
	
	
	public static int deleteAll() throws SQLException
	{
		Dao<Suspect4Model, Integer> dao = getDao();
		DeleteBuilder<Suspect4Model, Integer> deleteBuilder = dao.deleteBuilder();
		return dao.delete(deleteBuilder.prepare());
	}

}
