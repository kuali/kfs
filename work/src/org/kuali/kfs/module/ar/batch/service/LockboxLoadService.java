package org.kuali.kfs.module.ar.batch.service;

import org.kuali.kfs.sys.batch.FlatFileDataHandler;

/**
 *
 * @author mramawat
 *
 * Defines methods for loading Lockbox payment files
 */

public interface LockboxLoadService extends FlatFileDataHandler {

	 /**
     * Validates file and if successful, upload the entries to
     * AR_LOCKBOX_T table.
     * @return True if the file load and store was successful, false otherwise.
     */
    public boolean loadFile();
}
