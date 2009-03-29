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
package org.kuali.kfs.gl.businessobject.options;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * This class returns list of correction group entries key value pairs.
 */
public class CorrectionGroupEntriesFinder extends KeyValuesBase {

    /**
     * Returns a list of key/value pairs to display correction groups that can be used in a Labor Ledger Correction Document
     * 
     * @return a List of key/value pairs for correction groups
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();


        OriginEntryGroupService originEntryGroupService = SpringContext.getBean(OriginEntryGroupService.class);
        
        //TODO: Shawn - need to change using file
        //Collection<OriginEntryGroup> groupList = originEntryGroupService.getAllOriginEntryGroup();
        //List<OriginEntryGroup> sortedGroupList = (List) groupList;

        File[] fileList = originEntryGroupService.getAllFileInBatchDirectory();
        
        
        //TODO: Shawn - need to ask to Sterling for group name sorting.
//        OriginEntryGroup.GroupTypeComparator oegTypeComparator = new OriginEntryGroup.GroupTypeComparator();
//        Collections.sort(sortedGroupList, oegTypeComparator);
        if (fileList != null){
            for (File file : fileList){
                String fileName = file.getName();
                if (fileName.contains(GeneralLedgerConstants.BatchFileSystem.EXTENSION) || fileName.contains(GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION)){

                    //build display file name with date and size
                    Date date = new Date(file.lastModified());
                    String timeInfo = "(Date: " + date.toLocaleString() + ")";
                    String sizeInfo = "(Size: " +  (new Long(file.length())).toString() + ")";
                                        
                    activeLabels.add(new KeyLabelPair(fileName,  timeInfo + " " + fileName + " " + sizeInfo ));
                }
                
                
            }    
        }
        
        //TODO: Shawn - need to keep this part??
//        String groupException = "";
//        for (int i = 0; i < KFSConstants.LLCP_GROUP_FILTER_EXCEPTION.length; i++) {
//            groupException += KFSConstants.LLCP_GROUP_FILTER_EXCEPTION[i] + " ";
//        }

        
//        for (OriginEntryGroup oeg : sortedGroupList) {
//            if (!oeg.getSourceCode().startsWith("L") || groupException.contains(oeg.getSourceCode())) {
//                activeLabels.add(new KeyLabelPair(oeg.getId().toString(), oeg.getName()));
//            }
//        }
        return activeLabels;
    }

}
