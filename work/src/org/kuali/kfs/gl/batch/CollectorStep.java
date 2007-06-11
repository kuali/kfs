/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.batch.collector;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.kfs.batch.BatchInputFileType;
import org.kuali.kfs.service.BatchInputFileService;
import org.kuali.module.gl.service.CollectorService;

/**
 * Batch step that controls the collector process. 
 * The basic steps in the collector process are the following:
 * 
 * 1) Retrieves files that need processed
 * 2) Parses each file into a CollectorBatch object using the collector digester rules
 * 3) Validation of contents in CollectorService
 * 4) Stores origin group, gl entries, and id billings for each batch
 * 5) Sends email to workgroup listed in the batch file header with process results
 * 6) Cleans up .done files
 */
public class CollectorStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectorStep.class);

    private CollectorService collectorService;
    private BatchInputFileService batchInputFileService;
    private BatchInputFileType collectorInputFileType;

    /**
     * Controls the collector process.
     */
    public boolean execute(String jobName) {
        List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(collectorInputFileType);
     
        boolean processSuccess = true;
        List<String> processedFiles = new ArrayList();
        for(String inputFileName: fileNamesToLoad) {
            processSuccess = collectorService.loadCollectorFile(inputFileName);
            if (processSuccess) {
                processedFiles.add(inputFileName);
            }
        }
        
        removeDoneFiles(processedFiles);
        
        return processSuccess;
    }

    /**
     * Clears out associated .done files for the processed data files.
     */
    private void removeDoneFiles(List<String> dataFileNames) {
        for(String dataFileName: dataFileNames) {
            File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
            if (doneFile.exists()) {
                doneFile.delete();
            }
        }
    }

    public void setCollectorService(CollectorService collectorService) {
        this.collectorService = collectorService;
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }
    
    public void setCollectorInputFileType(BatchInputFileType collectorInputFileType) {
        this.collectorInputFileType = collectorInputFileType;
    }
    
}
