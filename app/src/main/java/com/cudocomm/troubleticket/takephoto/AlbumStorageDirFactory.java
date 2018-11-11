package com.cudocomm.troubleticket.takephoto;

import java.io.File;

abstract class AlbumStorageDirFactory {

    public abstract File getAlbumStorageDir(String str);

    AlbumStorageDirFactory() {

    }

}