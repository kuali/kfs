/*
 * Copyright 2007 The Kuali Foundation.
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
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OriginEntryGroupService;

/**
 * This class returns a list of correction groups with a process flag set to yes key value pairs.
 */
public class ProcessingCorrectionGroupEntriesFinder extends KeyValuesBase {

    /**
     * Returns a list of key-value pairs of origin entry groups with a process flag set to yes. The key is the correction id, and
     * the value is the name of the origin entry group
     * 
     * @return a List of key value pair options
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {
        List<KeyLabelPair> activeLabels = new ArrayList<KeyLabelPair>();

        OriginEntryGroupService originEntryGroupService = SpringContext.getBean(OriginEntryGroupService.class);


        Collection<OriginEntryGroup> groupList = originEntryGroupService.getAllOriginEntryGroup();

        List<OriginEntryGroup> sortedGroupList = (List<OriginEntryGroup>) groupList;

        OEGTypeComparator oegTypeComparator = new OEGTypeComparator();
        Collections.sort(sortedGroupList, oegTypeComparator);

        for (OriginEntryGroup oeg : sortedGroupList) {
            if (oeg.getProcess().booleanValue() & !oeg.getSourceCode().startsWith("L")) {
                activeLabels.add(new KeyLabelPair(oeg.getId().toString(), oeg.getName()));
            }
        }

        return activeLabels;
    }

}
