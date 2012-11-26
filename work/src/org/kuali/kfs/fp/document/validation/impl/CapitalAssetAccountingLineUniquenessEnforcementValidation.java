/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cab.CabParameterConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.document.validation.AccountingDocumentValidationBase;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Validates that, for accounting lines which are identified as capital asset accounting lines,
 * they are unique so they can be handled individually by the Capital Asset Builder module.
 */
public class CapitalAssetAccountingLineUniquenessEnforcementValidation extends AccountingDocumentValidationBase {

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        Collection<AccountingLine> capitalAssetLines = getCapitalAssetAccountingLines( getAllAccountingLines() );
        // no matching lines, nothing to validate
        if ( capitalAssetLines.isEmpty() ) {
            return true;
        }
        return validateLineUniqueness( capitalAssetLines );
    }


    protected Collection<String> getCapitalAssetObjectSubTypes() {
        return getParameterService().getParameterValuesAsString(
                KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class,
                CabParameterConstants.CapitalAsset.FINANCIAL_PROCESSING_CAPITAL_OBJECT_SUB_TYPES);
    }

    protected Collection<AccountingLine> getCapitalAssetAccountingLines( Collection<AccountingLine> allLines ) {
        Collection<AccountingLine> capitalAssetAccountingLines = new ArrayList<AccountingLine>(allLines.size());
        Collection<String> capitalAssetObjectSubTypes = getCapitalAssetObjectSubTypes();
        for ( AccountingLine line : allLines ) {
            // we only care about expenses (not encumbrance or budget)
            // many documents leave the balance type blank when going to use the "default" of "AC"
            if ( StringUtils.isNotBlank( line.getBalanceTypeCode() ) && !StringUtils.equals( line.getBalanceTypeCode(), KFSConstants.BALANCE_TYPE_ACTUAL ) ) {
                continue;
            }
            // need to ensure that we have a financial object to test the sub type against - only refresh if necessary
            if ( StringUtils.isBlank(line.getFinancialObjectCode()) ) {
                continue; // no object code
            }
            if ( ObjectUtils.isNull( line.getObjectCode() )
                    || !StringUtils.equals(line.getChartOfAccountsCode(), line.getObjectCode().getChartOfAccountsCode())
                    || !StringUtils.equals(line.getFinancialObjectCode(), line.getObjectCode().getFinancialObjectCode()) ) {
                line.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
            }
            if ( ObjectUtils.isNull( line.getObjectCode() ) ) {
                continue; // invalid object code
            }
            if ( capitalAssetObjectSubTypes.contains( line.getObjectCode().getFinancialObjectSubTypeCode() ) ) {
                capitalAssetAccountingLines.add( line );
            }
        }
        return capitalAssetAccountingLines;
    }

    protected boolean validateLineUniqueness( Collection<AccountingLine> capitalAssetAccountingLines ) {
        HashSet<String> uniqueLineKeys = new HashSet<String>();
        for ( AccountingLine line : capitalAssetAccountingLines ) {
            if ( !uniqueLineKeys.add( getLineUniquenessKey(line) ) ) {
                GlobalVariables.getMessageMap().putError("sourceAccountingLines", KFSKeyConstants.ERROR_NONUNIQUE_CAPITAL_ASSET_ACCOUNTING_LINE);
                return false;
            }
        }
        return true;
    }

    protected String getLineUniquenessKey( AccountingLine line ) {
        StringBuilder sb = new StringBuilder();
        sb.append( line.getChartOfAccountsCode() );
        sb.append( line.getAccountNumber() );
        sb.append( line.getSubAccountNumber() );
        sb.append( line.getFinancialObjectCode() );
        sb.append( line.getFinancialSubObjectCode() );
        sb.append( line.getProjectCode() );
        sb.append( line.getOrganizationReferenceId() );
        sb.append( line.getFinancialDocumentLineDescription() );
        if ( line instanceof SourceAccountingLine ) {
            sb.append( line.getAmount() );
        } else {
            sb.append( (line.getAmount() == null)?null:line.getAmount().negated() );
        }
        sb.append( line.getDebitCreditCode() );
        return sb.toString();
    }

}
