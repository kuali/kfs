/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.cg.lookup;

import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.bo.BusinessObject;
import org.kuali.kfs.KFSConstants;

/**
 * User: Laran Evans <lc278@cornell.edu>
 * Date: Jun 5, 2007
 * Time: 10:48:15 AM
 */
public class AwardLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    @Override
    public String getMaintenanceUrl(BusinessObject businessObject, String methodToCall) {
        if (methodToCall.equals(KFSConstants.COPY_METHOD)) {
            return KFSConstants.EMPTY_STRING;
        } else {
            return super.getMaintenanceUrl(businessObject, methodToCall);
        }
    }
}
