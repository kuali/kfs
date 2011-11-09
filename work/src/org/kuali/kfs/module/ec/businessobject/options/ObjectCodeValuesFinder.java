/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ec.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.document.web.struts.CertificationReportForm;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class...
 */
public class ObjectCodeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        CertificationReportForm form = (CertificationReportForm) KNSGlobalVariables.getKualiForm();
        EffortCertificationDocument document = (EffortCertificationDocument)form.getDocument();
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        List<String> objectCodeList = document.getObjectCodeList();
        for (String objectCode : objectCodeList) {
            keyValues.add(new ConcreteKeyValue(objectCode, objectCode));
        }
        
        return keyValues;
    }

}
