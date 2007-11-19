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
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.module.gl.service.CorrectionDocumentService;

/**
 * This class returns list of GLCP editing methods key value pairs, to populate a dropdown select control
 */
public class CorrectionEditMethodValuesFinder extends KeyValuesBase {

    /**
     * Returns a list of origin entry editing methods that the GLCP provides.
     * 
     * @return a List of editing method key/value pairs
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {
        List<KeyLabelPair> activeLabels = new ArrayList<KeyLabelPair>();
        activeLabels.add(new KeyLabelPair("", "Edit Method"));
        activeLabels.add(new KeyLabelPair(CorrectionDocumentService.CORRECTION_TYPE_CRITERIA, "Using Criteria"));
        activeLabels.add(new KeyLabelPair(CorrectionDocumentService.CORRECTION_TYPE_MANUAL, "Manual Edit"));
        activeLabels.add(new KeyLabelPair(CorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING, "Remove Group From Processing"));

        return activeLabels;
    }

}