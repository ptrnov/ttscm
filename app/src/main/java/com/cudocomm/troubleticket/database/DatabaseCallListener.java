package com.cudocomm.troubleticket.database;

import com.cudocomm.troubleticket.database.data.Data;

public interface DatabaseCallListener {

    public void onDatabaseCallRespond(DatabaseCallTask task, Data<?> data);

    public void onDatabaseCallFail(DatabaseCallTask task, Exception exception);

}