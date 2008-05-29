/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.purap.service.impl;

import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.service.impl.AccountingLineRuleHelperServiceImpl;
import org.kuali.module.purap.service.PurapAccountingLineRuleHelperService;

public class PurapAccountingLineRuleHelperServiceImpl extends AccountingLineRuleHelperServiceImpl implements PurapAccountingLineRuleHelperService{

    /**
     * @see org.kuali.kfs.service.impl.AccountingLineRuleHelperServiceImpl#hasRequiredOverrides(org.kuali.kfs.bo.AccountingLine, java.lang.String)
     * in purap implementation this does nothing since it is handled in our rule classes
     */
    @Override
    public boolean hasRequiredOverrides(AccountingLine line, String overrideCode) {
        return true;
    }

}
