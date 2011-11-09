/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.coa.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class creates a new finder for our forms view (creates a drop-down of {@link BasicAccountingCategory})
 */
public class OrgReviewRolesValuesFinder extends KeyValuesBase {

    /**
     * Creates a list of {@link BasicAccountingCategory} with their code as the key and their code and description as the display
     * value
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", ""));
        labels.add(new ConcreteKeyValue(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_ONLY_CODE, KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_ONLY_TEXT));
        labels.add(new ConcreteKeyValue(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ONLY_CODE, KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ONLY_TEXT));
        labels.add(new ConcreteKeyValue(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_BOTH_CODE, KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_BOTH_TEXT));
        return labels;
    }

}
