/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
