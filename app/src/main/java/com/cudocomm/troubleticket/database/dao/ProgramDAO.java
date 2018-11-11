package com.cudocomm.troubleticket.database.dao;

import com.cudocomm.troubleticket.database.DatabaseHelper;
import com.cudocomm.troubleticket.database.model.Program;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class ProgramDAO {

    private static Dao<Program, Integer> getDao() throws SQLException {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        return databaseHelper.getProgramDao();
    }


    public static int refresh(Program program) throws SQLException {
        Dao<Program, Integer> dao = getDao();
        return dao.refresh(program);
    }


    public static int create(Program program) throws SQLException {
        Dao<Program, Integer> dao = getDao();
        return dao.create(program);
    }

    public static long count() throws SQLException {
        Dao<Program, Integer> dao = getDao();
        return dao.countOf();
    }


    public static Program read(Integer id) throws SQLException {
        Dao<Program, Integer> dao = getDao();
        return dao.queryForId(id);
    }

    public static Program readByName(String name) {
        try {
            Dao<Program, Integer> dao = getDao();
            return dao.queryForEq(Program.PROGRAM_NAME, name).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException iiobe) {
            return null;
        }
        return null;
    }

    public static List<Program> readAll(long skip, long take) throws SQLException {
        Dao<Program, Integer> dao = getDao();
        List<Program> list;
        if(skip==-1L && take==-1L)
        {
            QueryBuilder<Program, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(Program.PROGRAM_ID, true);
            list = dao.query(queryBuilder.prepare());
        }
        else
        {
            QueryBuilder<Program, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(Program.PROGRAM_ID, true);
            queryBuilder.offset(skip).limit(take);
            list = dao.query(queryBuilder.prepare());
        }
        return list;
    }

    public static int update(Program category) throws SQLException {
        Dao<Program, Integer> dao = getDao();
        return dao.update(category);
    }


    public static int delete(Integer id) throws SQLException {
        Dao<Program, Integer> dao = getDao();
        return dao.deleteById(id);
    }


    public static int deleteAll() throws SQLException {
        Dao<Program, Integer> dao = getDao();
        DeleteBuilder<Program, Integer> deleteBuilder = dao.deleteBuilder();
        return dao.delete(deleteBuilder.prepare());
    }
}
