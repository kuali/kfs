package edu.arizona.kfs.module.cr.filefilter;

import java.io.File;
import java.io.FilenameFilter;

import edu.arizona.kfs.gl.GeneralLedgerConstants;

/**
 * Class for filtering files with the .done file extension.
 */
public class DoneFilenameFilter implements FilenameFilter {

    public boolean accept(File dir, String name) {
        return (name.endsWith(GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION));
    }

}
