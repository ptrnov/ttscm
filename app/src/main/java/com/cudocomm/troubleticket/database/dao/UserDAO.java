package com.cudocomm.troubleticket.database.dao;

import com.cudocomm.troubleticket.database.DatabaseHelper;
import com.cudocomm.troubleticket.database.model.UserLoginModel;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class UserDAO {
	
    private static Dao<UserLoginModel, Integer> getDao() throws SQLException {
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		return databaseHelper.getUserDao();
	}
	
	
	public static int refresh(UserLoginModel userLoginModel) throws SQLException
	{
		Dao<UserLoginModel, Integer> dao = getDao();
		return dao.refresh(userLoginModel);
	}
	
	
	public static int create(UserLoginModel userLoginModel) throws SQLException
	{
		Dao<UserLoginModel, Integer> dao = getDao();
		return dao.create(userLoginModel);
	}

	public static long count() throws SQLException {
		Dao<UserLoginModel, Integer> dao = getDao();
		return dao.countOf();
	}
	
	
	public static UserLoginModel read(Integer id) throws SQLException
	{
		Dao<UserLoginModel, Integer> dao = getDao();
		return dao.queryForId(id);
	}

    public static UserLoginModel readByUserId(Integer userId) throws SQLException {
        Dao<UserLoginModel, Integer> dao = getDao();
        return dao.queryForEq(UserLoginModel.ID, userId).get(0);
    }

	public static UserLoginModel readByName(String name) throws SQLException {
		Dao<UserLoginModel, Integer> dao = getDao();
		return dao.queryForEq(UserLoginModel.NAME, name).get(0);
	}

	public static List<UserLoginModel> readAll(long skip, long take) throws SQLException {
		Dao<UserLoginModel, Integer> dao = getDao();
		List<UserLoginModel> list;
		if(skip==-1l && take==-1l)
		{
			QueryBuilder<UserLoginModel, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(UserLoginModel.ID, true);
			list = dao.query(queryBuilder.prepare());
		}
		else
		{
			QueryBuilder<UserLoginModel, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(UserLoginModel.ID, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}
	
	
	public static int update(UserLoginModel category) throws SQLException
	{
		Dao<UserLoginModel, Integer> dao = getDao();
		return dao.update(category);
	}
	
	
	public static int delete(Integer id) throws SQLException {
		Dao<UserLoginModel, Integer> dao = getDao();
		return dao.deleteById(id);
	}
	
	
	public static int deleteByUserId(Integer userId) throws SQLException
	{
		Dao<UserLoginModel, Integer> dao = getDao();
		DeleteBuilder<UserLoginModel, Integer> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.where().eq(UserLoginModel.ID, userId);
		return dao.delete(deleteBuilder.prepare());
	}


	public static int deleteAll() throws SQLException
	{
		Dao<UserLoginModel, Integer> dao = getDao();
		DeleteBuilder<UserLoginModel, Integer> deleteBuilder = dao.deleteBuilder();
		return dao.delete(deleteBuilder.prepare());
	}

}
