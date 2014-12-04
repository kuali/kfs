/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ld.batch;

import org.kuali.kfs.gl.batch.EnterpriseFeederFileSetType;
import org.kuali.kfs.module.ld.batch.service.EnterpriseFeederService;
import org.kuali.kfs.sys.FileUtil;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class provides metadata for the batch upload screen to work for files associated with the enterprise feeder.
 */
public class LaborEnterpriseFeederFileSetType extends EnterpriseFeederFileSetType {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborEnterpriseFeederFileSetType.class);

    private static final String FILE_NAME_PREFIX = "laborEntpBatchFile";
    
    /**
     * Returns directory path for EnterpriseFeederService
     * 
     * @param fileType file type (not used)
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getDirectoryPath(java.lang.String)
     */

    public String getDirectoryPath(String fileType) {
        // all files in the file set go into the same directory
        String directoryPath = SpringContext.getBean(EnterpriseFeederService.class).getDirectoryName();
        FileUtil.createDirectory(directoryPath);
        return directoryPath;
    }
    
    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    public String getTitleKey() {
        return KFSKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_LABOR_ENTERPRISE_FEEDER;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getDoneFileDirectoryPath()
     */
    public String getDoneFileDirectoryPath() {
        return SpringContext.getBean(EnterpriseFeederService.class).getDirectoryName();
    }
    
    
}
