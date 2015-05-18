/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.rules;

import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.DictionaryValidationService;

/**
 * KNS version of DocumentRuleBase
 */
public class DocumentRuleBase extends org.kuali.rice.krad.rules.DocumentRuleBase {

    protected DictionaryValidationService getDictionaryValidationService() {
        return LazyServicesHolder.dictionaryValidationService;
    }

    // Lazy init holder class, see Effective Java #71
    private static class LazyServicesHolder {
        static final DictionaryValidationService dictionaryValidationService =
                KNSServiceLocator.getKNSDictionaryValidationService();
    }
}
