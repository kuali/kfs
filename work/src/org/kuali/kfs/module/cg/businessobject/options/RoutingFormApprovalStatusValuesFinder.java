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
package org.kuali.module.kra.routingform.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.module.kra.KraConstants;

public class RoutingFormApprovalStatusValuesFinder extends KeyValuesBase {
    
    public RoutingFormApprovalStatusValuesFinder() {
        super();
    }

    public List getKeyValues() {
        
        List approvalTypeKeyLabelPairList = new ArrayList();
        approvalTypeKeyLabelPairList.add(new KeyLabelPair(KraConstants.RESEARCH_RISK_STUDY_STATUS_PENDING, "Pending"));
        approvalTypeKeyLabelPairList.add(new KeyLabelPair(KraConstants.RESEARCH_RISK_STUDY_STATUS_APPROVED, "Approved"));

        return approvalTypeKeyLabelPairList;
    }
}
