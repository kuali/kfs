/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.tem.document.service.TemCorrectionDocumentService;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;

public class CorrectionEditMethodValuesFinder extends KeyValuesBase{
    
    @Override
    public List<KeyLabelPair> getKeyValues() {
        List<KeyLabelPair> activeLabels = new ArrayList<KeyLabelPair>();
        activeLabels.add(new KeyLabelPair("", "Edit Method"));
        activeLabels.add(new KeyLabelPair(TemCorrectionDocumentService.CORRECTION_TYPE_MANUAL, "Manual Edit"));
        activeLabels.add(new KeyLabelPair(TemCorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING, "Remove Group From Processing"));

        return activeLabels;
    }

}
