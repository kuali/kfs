/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.batch;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.batch.service.CustomerLoadService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.rice.kns.util.TypedArrayList;

public class CustomerLoadStep extends AbstractStep {

    private BatchInputFileType batchInputFileType;
    private BatchInputFileService batchInputFileService;
    private CustomerLoadService batchService;
    
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        
        List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(batchInputFileType);
        
        //  fail if null returned by batchInputFileService
        if (fileNamesToLoad == null) {
            throw new RuntimeException("BatchInputFileService.listInputFileNamesWithDoneFile(" + 
                    batchInputFileType.getFileTypeIdentifer() + ") returned NULL which should never happen.");
        }
        
        List<String> processedFiles = new TypedArrayList(String.class);
        for (String inputFileName : fileNamesToLoad) {
            
            //  filenames returned should never be blank/empty/null
            if (StringUtils.isBlank(inputFileName)) {
                throw new RuntimeException("One of the file names returned as ready to process [" + inputFileName + 
                        "] was blank.  This should not happen, so throwing an error to investigate.");
            }
            
            if (batchService.loadFile(inputFileName)) {
                processedFiles.add(inputFileName);
            }
        }

        removeDoneFiles(processedFiles);

        return true;
    }

    /**
     * Clears out associated .done files for the processed data files.
     */
    private void removeDoneFiles(List<String> dataFileNames) {
        for (String dataFileName : dataFileNames) {
            File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
            if (doneFile.exists()) {
                doneFile.delete();
            }
        }
    }

    public void setBatchInputFileType(BatchInputFileType batchInputFileType) {
        this.batchInputFileType = batchInputFileType;
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

}
