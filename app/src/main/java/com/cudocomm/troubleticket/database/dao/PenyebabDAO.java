package com.cudocomm.troubleticket.database.dao;

import com.cudocomm.troubleticket.database.DatabaseHelper;
import com.cudocomm.troubleticket.database.model.Penyebab;
import com.cudocomm.troubleticket.database.model.Program;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class PenyebabDAO {

    private static Dao<Penyebab, Integer> getDao() throws SQLException {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        return databaseHelper.getPenyebabDao();
    }


    public static int refresh(Penyebab penyebab) throws SQLException {
        Dao<Penyebab, Integer> dao = getDao();
        return dao.refresh(penyebab);
    }


    public static int create(Penyebab penyebab) throws SQLException {
        Dao<Penyebab, Integer> dao = getDao();
        return dao.create(penyebab);
    }

    public static long count() throws SQLException {
        Dao<Penyebab, Integer> dao = getDao();
        return dao.countOf();
    }


    public static Penyebab read(Integer id) throws SQLException {
        Dao<Penyebab, Integer> dao = getDao();
        return dao.queryForId(id);
    }

    public static Penyebab readByName(String name) {
        try {
            Dao<Penyebab, Integer> dao = getDao();
            return dao.queryForEq(Penyebab.PROGRAM_NAME, name).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException iiobe) {
            return null;
        }
        return null;
    }

    public static List<Penyebab> readAll(long skip, long take) throws SQLException {
        Dao<Penyebab, Integer> dao = getDao();
        List<Penyebab> list;
        if(skip==-1L && take==-1L)
        {
            QueryBuilder<Penyebab, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(Penyebab.PROGRAM_ID, true);
            list = dao.query(queryBuilder.prepare());
        }
        else
        {
            QueryBuilder<Penyebab, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(Penyebab.PROGRAM_ID, true);
            queryBuilder.offset(skip).limit(take);
            list = dao.query(queryBuilder.prepare());
        }
        return list;
    }

    public static int update(Penyebab category) throws SQLException {
        Dao<Penyebab, Integer> dao = getDao();
        return dao.update(category);
    }


    public static int delete(Integer id) throws SQLException {
        Dao<Penyebab, Integer> dao = getDao();
        return dao.deleteById(id);
    }


    public static int deleteAll() throws SQLException {
        Dao<Penyebab, Integer> dao = getDao();
        DeleteBuilder<Penyebab, Integer> deleteBuilder = dao.deleteBuilder();
        return dao.delete(deleteBuilder.prepare());
    }
}
