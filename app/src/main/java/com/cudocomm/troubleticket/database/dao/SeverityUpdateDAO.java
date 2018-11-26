package com.cudocomm.troubleticket.database.dao;

import com.cudocomm.troubleticket.database.DatabaseHelper;
import com.cudocomm.troubleticket.database.model.SeverityUpdateModel;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class SeverityUpdateDAO {
	
    private static Dao<SeverityUpdateModel, Integer> getDao() throws SQLException {
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		return databaseHelper.getSeverityUpdateDao();
	}
	
	
	public static int refresh(SeverityUpdateModel asetEmasModel) throws SQLException
	{
		Dao<SeverityUpdateModel, Integer> dao = getDao();
		return dao.refresh(asetEmasModel);
	}
	
	
	public static int create(SeverityUpdateModel asetEmasModel) throws SQLException
	{
		Dao<SeverityUpdateModel, Integer> dao = getDao();
		return dao.create(asetEmasModel);
	}

	public static long count() throws SQLException {
		Dao<SeverityUpdateModel, Integer> dao = getDao();
		return dao.countOf();
	}
	
	
	public static SeverityUpdateModel read(Integer id) throws SQLException
	{
		Dao<SeverityUpdateModel, Integer> dao = getDao();
		return dao.queryForId(id);
	}

	public static SeverityUpdateModel readByName(String name) throws SQLException {
		Dao<SeverityUpdateModel, Integer> dao = getDao();
		return dao.queryForEq(SeverityUpdateModel.NAME, name).get(0);
	}

	public static List<SeverityUpdateModel> readAll(long skip, long take) throws SQLException {
		Dao<SeverityUpdateModel, Integer> dao = getDao();
		List<SeverityUpdateModel> list;
		if(skip==-1l && take==-1l)
		{
			QueryBuilder<SeverityUpdateModel, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(SeverityUpdateModel.SEVERITY_ID, true);
			list = dao.query(queryBuilder.prepare());
		}
		else
		{
			QueryBuilder<SeverityUpdateModel, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(SeverityUpdateModel.SEVERITY_ID, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}
	
	
	public static int update(SeverityUpdateModel category) throws SQLException
	{
		Dao<SeverityUpdateModel, Integer> dao = getDao();
		return dao.update(category);
	}
	
	
	public static int delete(Integer id) throws SQLException
	{
		Dao<SeverityUpdateModel, Integer> dao = getDao();
		return dao.deleteById(id);
	}
	
	
	public static int deleteAll() throws SQLException
	{
		Dao<SeverityUpdateModel, Integer> dao = getDao();
		DeleteBuilder<SeverityUpdateModel, Integer> deleteBuilder = dao.deleteBuilder();
		return dao.delete(deleteBuilder.prepare());
	}

}
