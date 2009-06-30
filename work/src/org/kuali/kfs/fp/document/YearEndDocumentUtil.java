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

package org.kuali.kfs.fp.document;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.gl.service.SufficientFundsService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.exception.ReferentialIntegrityException;
import org.kuali.rice.kns.util.ObjectUtils;


/**
 * utils for <code>YearEndDocument</code>s
 * 
 * @see org.kuali.kfs.gl.service.SufficientFundsService
 */
public class YearEndDocumentUtil {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(YearEndDocumentUtil.class);
    
    private static final String FINAL_ACCOUNTING_PERIOD = "13";

    /**
     * @return the previous fiscal year used with all GLPE
     */
    public static final Integer getPreviousFiscalYear() {
        int i = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().intValue() - 1;
        return new Integer(i);
    }

    /**
     * @return the accounting period code used with all GLPE
     */
    public static final String getFinalAccountingPeriod() {
        return FINAL_ACCOUNTING_PERIOD;
    }

    /**
     * populates a <code>GeneralLedgerPendingEntry</code> populated with common year end document data into the explicit general
     * ledger pending entry. currently is the following:
     * <ol>
     * <li>fiscal period code = final accounting period code
     * <li>fiscal year= previous fiscal year
     * </ol>
     * 
     * @param transactionalDocument
     * @param accountingLine
     * @param explicitEntry
     */
    public static final void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        if (!YearEndDocument.class.isAssignableFrom(transactionalDocument.getClass())) {
            throw new IllegalArgumentException("invalid (not a year end document) for class:" + transactionalDocument.getClass());
        }
        YearEndDocument yearEndDocument = (YearEndDocument) transactionalDocument;
        explicitEntry.setUniversityFiscalPeriodCode(getFinalAccountingPeriod());
        explicitEntry.setUniversityFiscalYear(getPreviousFiscalYear());
    }
    
    /**
     * Populates an offset GeneralLedgerPendingEntry such that it uses the Offset Definition from the previous year
     * @param transactionalDocument the transactional document with general ledger pending entries on it
     * @param accountingLine the general ledger pending entry source which generated the explicit and offset entry
     * @param explicitEntry the explicit entry requiring this offset entry
     * @param offsetEntry the offset entry which is being customized
     * @return whether the offset could be successfully customized for year end or not
     */
    public static final boolean customizeOffsetGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        if (!(transactionalDocument instanceof YearEndDocument)) {
            throw new IllegalArgumentException("invalid (not a year end document) for class:" + transactionalDocument.getClass());
        }
        OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(getPreviousFiscalYear(), explicitEntry.getChartOfAccountsCode(), explicitEntry.getFinancialDocumentTypeCode(), explicitEntry.getFinancialBalanceTypeCode());
        if (!ObjectUtils.isNull(offsetDefinition)) {
            String offsetObjectCode = getOffsetFinancialObjectCode(offsetDefinition);
            offsetEntry.setFinancialObjectCode(offsetObjectCode);
            if (offsetObjectCode.equals(AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode())) {
                // no BO, so punt
                offsetEntry.setAcctSufficientFundsFinObjCd(AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode());
            }
            else {
                offsetDefinition.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_OBJECT);
                ObjectCode financialObject = offsetDefinition.getFinancialObject();
                // The ObjectCode reference may be invalid because a flexible offset account changed its chart code.
                if (ObjectUtils.isNull(financialObject)) {
                    throw new ReferentialIntegrityException("offset object code " + offsetEntry.getUniversityFiscalYear() + "-" + offsetEntry.getChartOfAccountsCode() + "-" + offsetEntry.getFinancialObjectCode());
                }
                offsetEntry.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
                offsetEntry.setAcctSufficientFundsFinObjCd(SpringContext.getBean(SufficientFundsService.class).getSufficientFundsObjectCode(financialObject, offsetEntry.getAccount().getAccountSufficientFundsCode()));
            }

            offsetEntry.setFinancialObjectTypeCode(getOffsetFinancialObjectTypeCode(offsetDefinition));
            
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Helper method that determines the offset entry's financial object code.
     * 
     * @param offsetDefinition
     * @return String
     */
    protected static String getOffsetFinancialObjectCode(OffsetDefinition offsetDefinition) {
        LOG.debug("getOffsetFinancialObjectCode(OffsetDefinition) - start");

        if (null != offsetDefinition) {
            String returnString = (!StringUtils.isBlank(offsetDefinition.getFinancialObjectCode())) ? offsetDefinition.getFinancialObjectCode() : AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode();
            LOG.debug("getOffsetFinancialObjectCode(OffsetDefinition) - end");
            return returnString;
        }
        else {
            LOG.debug("getOffsetFinancialObjectCode(OffsetDefinition) - end");
            return AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode();
        }

    }
    
    /**
     * Helper method that determines the offset entry's financial object type code.
     * 
     * @param offsetDefinition
     * @return String
     */
    protected static String getOffsetFinancialObjectTypeCode(OffsetDefinition offsetDefinition) {
        LOG.debug("getOffsetFinancialObjectTypeCode(OffsetDefinition) - start");

        if (null != offsetDefinition && null != offsetDefinition.getFinancialObject()) {
            String returnString = (!StringUtils.isBlank(offsetDefinition.getFinancialObject().getFinancialObjectTypeCode())) ? offsetDefinition.getFinancialObject().getFinancialObjectTypeCode() : AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectType();
            LOG.debug("getOffsetFinancialObjectTypeCode(OffsetDefinition) - end");
            return returnString;
        }
        else {
            LOG.debug("getOffsetFinancialObjectTypeCode(OffsetDefinition) - end");
            return AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectType();
        }

    }

    /**
     * Gets the FINAL_ACCOUNTING_PERIOD attribute.
     * 
     * @return Returns the FINAL_ACCOUNTING_PERIOD.
     */
    public static String getFINAL_ACCOUNTING_PERIOD() {
        return FINAL_ACCOUNTING_PERIOD;
    }
}
