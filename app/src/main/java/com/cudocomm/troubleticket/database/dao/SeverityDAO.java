package com.cudocomm.troubleticket.database.dao;

import com.cudocomm.troubleticket.database.DatabaseHelper;
import com.cudocomm.troubleticket.database.model.SeverityModel;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class SeverityDAO {
	
    private static Dao<SeverityModel, Integer> getDao() throws SQLException {
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		return databaseHelper.getSeverityDao();
	}
	
	
	public static int refresh(SeverityModel asetEmasModel) throws SQLException
	{
		Dao<SeverityModel, Integer> dao = getDao();
		return dao.refresh(asetEmasModel);
	}
	
	
	public static int create(SeverityModel asetEmasModel) throws SQLException
	{
		Dao<SeverityModel, Integer> dao = getDao();
		return dao.create(asetEmasModel);
	}

	public static long count() throws SQLException {
		Dao<SeverityModel, Integer> dao = getDao();
		return dao.countOf();
	}
	
	
	public static SeverityModel read(Integer id) throws SQLException
	{
		Dao<SeverityModel, Integer> dao = getDao();
		return dao.queryForId(id);
	}

	public static SeverityModel readByName(String name) throws SQLException {
		Dao<SeverityModel, Integer> dao = getDao();
		return dao.queryForEq(SeverityModel.NAME, name).get(0);
	}

	public static List<SeverityModel> readAll(long skip, long take) throws SQLException {
		Dao<SeverityModel, Integer> dao = getDao();
		List<SeverityModel> list;
		if(skip==-1l && take==-1l)
		{
			QueryBuilder<SeverityModel, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(SeverityModel.SEVERITY_ID, true);
			list = dao.query(queryBuilder.prepare());
		}
		else
		{
			QueryBuilder<SeverityModel, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(SeverityModel.SEVERITY_ID, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}
	
	
	public static int update(SeverityModel category) throws SQLException
	{
		Dao<SeverityModel, Integer> dao = getDao();
		return dao.update(category);
	}
	
	
	public static int delete(Integer id) throws SQLException
	{
		Dao<SeverityModel, Integer> dao = getDao();
		return dao.deleteById(id);
	}
	
	
	public static int deleteAll() throws SQLException
	{
		Dao<SeverityModel, Integer> dao = getDao();
		DeleteBuilder<SeverityModel, Integer> deleteBuilder = dao.deleteBuilder();
		return dao.delete(deleteBuilder.prepare());
	}

}
