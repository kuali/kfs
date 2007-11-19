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
package org.kuali.module.gl.web.optionfinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OriginEntryGroupService;

/**
 * This class returns list of correction group entries key value pairs.
 */
public class CorrectionGroupEntriesFinder extends KeyValuesBase {

    /**
     * Returns a list of key/value pairs to display correction groups that can be used in a Labor Ledger Correction Document
     * 
     * @return a List of key/value pairs for correction groups
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();


        OriginEntryGroupService originEntryGroupService = SpringContext.getBean(OriginEntryGroupService.class);

        Collection<OriginEntryGroup> groupList = originEntryGroupService.getAllOriginEntryGroup();

        List<OriginEntryGroup> sortedGroupList = (List) groupList;

        OEGTypeComparator oegTypeComparator = new OEGTypeComparator();
        Collections.sort(sortedGroupList, oegTypeComparator);

        String groupException = "";
        for (int i = 0; i < KFSConstants.LLCP_GROUP_FILTER_EXCEPTION.length; i++) {
            groupException += KFSConstants.LLCP_GROUP_FILTER_EXCEPTION[i] + " ";
        }

        for (OriginEntryGroup oeg : sortedGroupList) {
            if (!oeg.getSourceCode().startsWith("L") || groupException.contains(oeg.getSourceCode())) {
                activeLabels.add(new KeyLabelPair(oeg.getId().toString(), oeg.getName()));
            }
        }
        return activeLabels;
    }

}
