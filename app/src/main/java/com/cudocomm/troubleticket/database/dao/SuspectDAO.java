package com.cudocomm.troubleticket.database.dao;

import com.cudocomm.troubleticket.database.DatabaseHelper;
import com.cudocomm.troubleticket.database.model.SuspectModel;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class SuspectDAO {
	
    private static Dao<SuspectModel, Integer> getDao() throws SQLException {
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		return databaseHelper.getSuspectDao();
	}
	
	
	public static int refresh(SuspectModel asetEmasModel) throws SQLException
	{
		Dao<SuspectModel, Integer> dao = getDao();
		return dao.refresh(asetEmasModel);
	}
	
	
	public static int create(SuspectModel asetEmasModel) throws SQLException
	{
		Dao<SuspectModel, Integer> dao = getDao();
		return dao.create(asetEmasModel);
	}

	public static long count() throws SQLException {
		Dao<SuspectModel, Integer> dao = getDao();
		return dao.countOf();
	}
	
	
	public static SuspectModel read(Integer id) throws SQLException
	{
		Dao<SuspectModel, Integer> dao = getDao();
		return dao.queryForId(id);
	}

	public static SuspectModel readByName(String name) throws SQLException {
		Dao<SuspectModel, Integer> dao = getDao();
		return dao.queryForEq(SuspectModel.NAME, name).get(0);
	}

	public static List<SuspectModel> readAll(long skip, long take) throws SQLException {
		Dao<SuspectModel, Integer> dao = getDao();
		List<SuspectModel> list;
		if(skip==-1l && take==-1l)
		{
			QueryBuilder<SuspectModel, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(SuspectModel.ID, true);
			list = dao.query(queryBuilder.prepare());
		}
		else
		{
			QueryBuilder<SuspectModel, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(SuspectModel.ID, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}
	
	
	public static int update(SuspectModel category) throws SQLException
	{
		Dao<SuspectModel, Integer> dao = getDao();
		return dao.update(category);
	}
	
	
	public static int delete(Integer id) throws SQLException
	{
		Dao<SuspectModel, Integer> dao = getDao();
		return dao.deleteById(id);
	}
	
	
	public static int deleteAll() throws SQLException
	{
		Dao<SuspectModel, Integer> dao = getDao();
		DeleteBuilder<SuspectModel, Integer> deleteBuilder = dao.deleteBuilder();
		return dao.delete(deleteBuilder.prepare());
	}

}
