/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cg.document.validation.impl;

import java.sql.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * PreRules checks for the Account that needs to occur while still in the Struts processing. This includes defaults, confirmations,
 * etc.
 */
public class AwardPreRules extends MaintenancePreRulesBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AwardPreRules.class);

    protected ConfigurationService configService;
    protected DataDictionaryService dataDictionaryService;

    protected Award newAward;

    /**
     * Constructs a AwardPreRules.java.
     */
    public AwardPreRules() {
        dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        configService = SpringContext.getBean(ConfigurationService.class);
    }

    /**
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        boolean proceed = true;
        if (proceed) {
            proceed = continueIfEntryDateBeforeBeginDate();
        }
        if (proceed) {
            proceed = continueIfSubcontractorTotalGreaterThanAwardTotal();
        }

        if (!proceed) {
            abortRulesCheck();
        }

        return true;
    }

    /**
     * Checks if the entry date is before the begin date. if so asks the user if they want to continue validation. if no is selected
     * further validation is aborted and the user is returned to the award document.
     * 
     * @return true if the user selects yes, false otherwise
     */
    protected boolean continueIfEntryDateBeforeBeginDate() {
        boolean proceed = true;
        Date entryDate = newAward.getAwardEntryDate();
        Date beginDate = newAward.getAwardBeginningDate();

        if (ObjectUtils.isNotNull(entryDate) && ObjectUtils.isNotNull(beginDate) && entryDate.before(beginDate)) {
            String entryDateLabel = dataDictionaryService.getAttributeErrorLabel(Award.class, KFSPropertyConstants.AWARD_ENTRY_DATE);
            String beginDateLabel = dataDictionaryService.getAttributeErrorLabel(Award.class, KFSPropertyConstants.AWARD_BEGINNING_DATE);
            proceed = askOrAnalyzeYesNoQuestion("entryDateBeforeStartDate", buildConfirmationQuestion(KFSKeyConstants.WARNING_AWARD_ENTRY_BEFORE_START_DATE, entryDateLabel, beginDateLabel));
        }
        return proceed;
    }

    /**
     * Checks if the {@link Subcontractor} total amount is greater than the award total. If so asks the user if they want to
     * continue validation. if no is selected further validation is aborted and the user is returned to the award document.
     * 
     * @return true if the user selects yes, false otherwise
     */
    protected boolean continueIfSubcontractorTotalGreaterThanAwardTotal() {
        boolean proceed = true;

        KualiDecimal awardTotal = newAward.getAwardTotalAmount();
        KualiDecimal subcontractorTotal = newAward.getAwardSubcontractorsTotalAmount();
        if ((ObjectUtils.isNotNull(awardTotal) && subcontractorTotal.isGreaterThan(awardTotal)) || (ObjectUtils.isNull(awardTotal) && subcontractorTotal.isPositive())) {

            String subcontracorLabel = dataDictionaryService.getCollectionLabel(Award.class, KFSPropertyConstants.AWARD_SUBCONTRACTORS);
            String awardLabel = dataDictionaryService.getAttributeErrorLabel(Award.class, KFSPropertyConstants.AWARD_TOTAL_AMOUNT);

            proceed = askOrAnalyzeYesNoQuestion("subcontractorTotalGreaterThanAwardTotal", buildConfirmationQuestion(KFSKeyConstants.WARNING_AWARD_SUBCONTRACTOR_TOTAL_GREATER_THAN_AWARD_TOTAL, subcontracorLabel, awardLabel));
        }

        return proceed;
    }

    /**
     * Builds out the confirmation question.
     * 
     * @param messageKey
     * @param parameters
     * @return
     */
    protected String buildConfirmationQuestion(String messageKey, String... parameters) {
        String result = configService.getPropertyValueAsString(messageKey);
        if (null != parameters) {
            for (int i = 0; i < parameters.length; i++) {
                result = StringUtils.replace(result, "{" + i + "}", parameters[i]);
            }
        }
        return result;
    }

    /**
     * @param document
     */
    protected void setupConvenienceObjects(MaintenanceDocument document) {
        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newAward = (Award) document.getNewMaintainableObject().getBusinessObject();
    }

}
