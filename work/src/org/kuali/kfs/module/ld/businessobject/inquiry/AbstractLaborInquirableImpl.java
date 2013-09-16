/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.businessobject.inquiry;

import org.kuali.kfs.integration.ld.businessobject.inquiry.AbstractLaborIntegrationInquirableImpl;
import org.kuali.kfs.module.ld.LaborConstants;



/**
 * This class is the template class for the customized inqurable implementations used to generate balance inquiry screens.
 */
public abstract class AbstractLaborInquirableImpl extends AbstractLaborIntegrationInquirableImpl {

    @Override
    protected String getPositionNumberKeyValue() {
        return LaborConstants.getDashPositionNumber();
    }

    @Override
    protected String getFinancialBalanceTypeCodeKeyValue() {
        return LaborConstants.BalanceInquiries.BALANCE_TYPE_AC_AND_A21;
    }

}
