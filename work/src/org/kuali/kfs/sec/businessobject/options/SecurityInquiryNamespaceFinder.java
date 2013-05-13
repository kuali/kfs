/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.sec.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * Returns list of inquiry namespaces
 */
public class SecurityInquiryNamespaceFinder extends KeyValuesBase {

    protected static final List<KeyValue> OPTIONS = new ArrayList<KeyValue>();
    static {
        OPTIONS.add(new ConcreteKeyValue("",""));
        OPTIONS.add(new ConcreteKeyValue(KFSConstants.CoreModuleNamespaces.GL, KFSConstants.CoreModuleNamespaces.GL));
        OPTIONS.add(new ConcreteKeyValue(KFSConstants.OptionalModuleNamespaces.LABOR_DISTRIBUTION, KFSConstants.OptionalModuleNamespaces.LABOR_DISTRIBUTION));
    }
    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        return OPTIONS;
    }
}
