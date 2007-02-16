/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OriginEntryGroupService;

/**
 * This class returns list of payment method key value pairs.
 * 
 * 
 */
public class CorrectionPendingGroupEntriesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();

        OriginEntryGroupService originEntryGroupService = SpringServiceLocator.getOriginEntryGroupService();

        Collection<OriginEntryGroup> groupPendingList = originEntryGroupService.getOriginEntryGroupsPendingProcessing();
        // Collection groupPendingList = originEntryGroupService.getOriginEntryGroupsPendingProcessing();
        for (OriginEntryGroup oeg : groupPendingList) {

            String sourceName = "";
            if (oeg.getSource() != null) {
                sourceName = oeg.getSource().getName();
            }

            if (oeg.getValid()) {
                activeLabels.add(new KeyLabelPair(oeg.getId().toString(), oeg.getSourceCode() + " - " + sourceName + " - " + oeg.getId().toString() + " - " + oeg.getDate().toString()));
            }

        }


        return activeLabels;
    }

}
