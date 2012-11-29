/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.web.bean.AccountingLineDistributionKey;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;

public class TEMAccountingLineTotalsValidation extends GenericValidation {

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean rulePassed = true;
        TravelDocument travelDocument = (TravelDocument) event.getDocument();

        List<AccountingDistribution> distributions = SpringContext.getBean(AccountingDistributionService.class).buildDistributionFrom(travelDocument);
        KualiDecimal totalRemaining = KualiDecimal.ZERO;
        Map<AccountingLineDistributionKey,KualiDecimal> amounts = new HashMap<AccountingLineDistributionKey, KualiDecimal>();
        Map<AccountingLineDistributionKey,KualiDecimal> finalAmounts = new HashMap<AccountingLineDistributionKey, KualiDecimal>();
        Map<AccountingLineDistributionKey,Integer> lineIndexes = new HashMap<AccountingLineDistributionKey, Integer>();

        for (final AccountingDistribution dist : distributions) {
            AccountingLineDistributionKey key = new AccountingLineDistributionKey(dist.getObjectCode(), dist.getCardType());
            if (amounts.containsKey(key)){
                KualiDecimal tempAmount = dist.getSubTotal().add(amounts.get(key));
                amounts.put(key, tempAmount);
                finalAmounts.put(key, tempAmount);
            }
            else{
                amounts.put(key, dist.getSubTotal());
                finalAmounts.put(key, dist.getSubTotal());
            }
        }

        if (travelDocument.getSourceAccountingLines() != null && !travelDocument.getSourceAccountingLines().isEmpty()) {

            List errors = GlobalVariables.getMessageMap().getErrorPath();
            GlobalVariables.getMessageMap().clearErrorPath();
            //check the current accounting line
            for (TemSourceAccountingLine line : (List<TemSourceAccountingLine>)travelDocument.getSourceAccountingLines()){
                AccountingLineDistributionKey key = new AccountingLineDistributionKey(line.getFinancialObjectCode(),line.getCardType());
                if (amounts.containsKey(key)){
                    if (amounts.get(key).isGreaterEqual(line.getAmount())){
                        KualiDecimal tempAmount = amounts.get(key).subtract(line.getAmount());
                        amounts.put(key, tempAmount);
                        lineIndexes.put(key, line.getSequenceNumber());
                    }
                    else{
                        GlobalVariables.getMessageMap().putError(KRADPropertyConstants.DOCUMENT + "." + TemPropertyConstants.SOURCE_ACCOUNTING_LINE + "[" + (line.getSequenceNumber().intValue()-1) + "]." + TravelAuthorizationFields.FIN_OBJ_CD, TemKeyConstants.ERROR_TEM_ACCOUNTING_LINES_OBJECT_CODE_CARD_TYPE_TOTAL, key.getFinancialObjectCode(), key.getCardType(), finalAmounts.get(key).toString());
                        rulePassed = false;
                    }
                }
                else{
                    if (!(event.getDocument() instanceof TravelAuthorizationDocument)) {
                        GlobalVariables.getMessageMap().putError(KRADPropertyConstants.DOCUMENT + "." + TemPropertyConstants.SOURCE_ACCOUNTING_LINE + "[" + (line.getSequenceNumber().intValue()-1) + "]." + TravelAuthorizationFields.FIN_OBJ_CD, TemKeyConstants.ERROR_TEM_ACCOUNTING_LINES_OBJECT_CODE_CARD_TYPE, line.getFinancialObjectCode(), line.getCardType());
                        rulePassed = false;
                    }
                }
            }

            if (rulePassed){
                for (AccountingLineDistributionKey key : amounts.keySet()){
                    KualiDecimal tempAmount = amounts.get(key);
                    if (!tempAmount.isZero()){
                        if (lineIndexes.containsKey(key)){
                            GlobalVariables.getMessageMap().putError(KRADPropertyConstants.DOCUMENT + "." + TemPropertyConstants.SOURCE_ACCOUNTING_LINE + "[" + (lineIndexes.get(key).intValue()-1) + "]." + TravelAuthorizationFields.FIN_OBJ_CD, TemKeyConstants.ERROR_TEM_ACCOUNTING_LINES_OBJECT_CODE_CARD_TYPE_TOTAL, key.getFinancialObjectCode(), key.getCardType(), finalAmounts.get(key).toString());
                        }
                        else{
                            GlobalVariables.getMessageMap().putError(TemPropertyConstants.NEW_SOURCE_ACCTG_LINE + "." + TravelAuthorizationFields.FIN_OBJ_CD, TemKeyConstants.ERROR_TEM_ACCOUNTING_LINES_OBJECT_CODE_CARD_TYPE_TOTAL, key.getFinancialObjectCode(), key.getCardType(), finalAmounts.get(key).toString());
                        }
                        rulePassed = false;
                    }
                }
                GlobalVariables.getMessageMap().getErrorPath().addAll(errors);
            }
        }

        GlobalVariables.getMessageMap().clearErrorPath();
        return rulePassed;
    }
}
