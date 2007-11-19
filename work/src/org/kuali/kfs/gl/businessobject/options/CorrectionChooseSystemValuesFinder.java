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
 * This class returns list, ready for populating a drop down select control, of "systems" that can
 * be used by the GLCP
 */
public class CorrectionChooseSystemValuesFinder extends KeyValuesBase {

    /**
     * Returns the list of data sources that can feed data to the GLCP
     * 
     * @return a List of data sources
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();
        activeLabels.add(new KeyLabelPair("", "Select System"));
        activeLabels.add(new KeyLabelPair(CorrectionDocumentService.SYSTEM_DATABASE, "Database"));
        activeLabels.add(new KeyLabelPair(CorrectionDocumentService.SYSTEM_UPLOAD, "File Upload"));

        return activeLabels;
    }

}
