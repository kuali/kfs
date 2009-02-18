/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.AttachmentTypeCodes;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

public class PurchaseOrderAttachmentTypeValuesFinder extends KeyValuesBase {

    public List getKeyValues() {
        List keyValues = new ArrayList();
        
        keyValues.add(new KeyLabelPair(AttachmentTypeCodes.ATTACHMENT_TYPE_CONTRACTS, AttachmentTypeCodes.ATTACHMENT_TYPE_CONTRACTS));
        keyValues.add(new KeyLabelPair(AttachmentTypeCodes.ATTACHMENT_TYPE_QUOTE, AttachmentTypeCodes.ATTACHMENT_TYPE_QUOTE));
        keyValues.add(new KeyLabelPair(AttachmentTypeCodes.ATTACHMENT_TYPE_RFP, AttachmentTypeCodes.ATTACHMENT_TYPE_RFP));
        keyValues.add(new KeyLabelPair(AttachmentTypeCodes.ATTACHMENT_TYPE_RFP_RESPONSES, AttachmentTypeCodes.ATTACHMENT_TYPE_RFP_RESPONSES));
        keyValues.add(new KeyLabelPair(AttachmentTypeCodes.ATTACHMENT_TYPE_CONTRACT_AMENDMENTS, AttachmentTypeCodes.ATTACHMENT_TYPE_CONTRACT_AMENDMENTS));
        keyValues.add(new KeyLabelPair(AttachmentTypeCodes.ATTACHMENT_TYPE_OTHER_RESTRICTED, AttachmentTypeCodes.ATTACHMENT_TYPE_OTHER_RESTRICTED));
        keyValues.add(new KeyLabelPair(AttachmentTypeCodes.ATTACHMENT_TYPE_OTHER, AttachmentTypeCodes.ATTACHMENT_TYPE_OTHER));

        return keyValues;
    }

}
