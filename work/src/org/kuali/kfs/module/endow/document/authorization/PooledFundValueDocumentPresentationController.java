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
package org.kuali.kfs.module.endow.document.authorization;

import java.sql.Date;
import java.util.Set;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.PooledFundControlService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class PooledFundValueDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> fields = super.getConditionallyReadOnlyPropertyNames(document);

        PooledFundControlService pooledFundControlService = SpringContext.getBean(PooledFundControlService.class);

        // Make Valuation Date gray out (can't edit) when editing a maintenance doc
        if (KRADConstants.MAINTENANCE_EDIT_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction())) {
            fields.add(EndowPropertyConstants.VALUATION_DATE);

            PooledFundValue oldPooledFundValue = (PooledFundValue) document.getOldMaintainableObject().getBusinessObject();
            String pooledSecurityID = oldPooledFundValue.getPooledSecurityID();
            PooledFundControl theReferencePFC = pooledFundControlService.getByPrimaryKey(pooledSecurityID);
            Boolean distributeGainsAndLossesIndicator = theReferencePFC.isDistributeGainsAndLossesIndicator();

            /*
             * Business Rule:If the date for the distribution is prior to the current system Process Date and the Process Complete
             * flag is Yes, the distribution amount and date cannot be changed.
             */
            KEMService kemService = SpringContext.getBean(KEMService.class);
            Date currentDate = kemService.getCurrentDate();
            Date distributIncomeOnDate = oldPooledFundValue.getDistributeIncomeOnDate();

            if (oldPooledFundValue.isIncomeDistributionComplete() && distributIncomeOnDate != null && distributIncomeOnDate.before(currentDate)) {
                fields.add(EndowPropertyConstants.DISTRIBUTE_INCOME_ON_DATE);
                fields.add(EndowPropertyConstants.INCOME_DISTRIBUTION_PER_UNIT);
            }
            /*
             * Rule: If xx_ PROC_CMPLT is "Y", the corresponding amount and xx__PROC_ON_DT fields should be read only in edit mode.
             * Rule: In the pooled fund control, if the Distribute Gains And Losses = "N", LT_GAIN_LOSS / LT_PROC_ON_DT and
             * ST_GAIN_LOSS / ST_PROC_ON_DT fields in the PooledFundValue should be read only in the edit mode.
             */
            if (!distributeGainsAndLossesIndicator || oldPooledFundValue.isLongTermGainLossDistributionComplete()) {
                fields.add(EndowPropertyConstants.LONG_TERM_GAIN_LOSS_DISTRIBUTION_PER_UNIT);
                fields.add(EndowPropertyConstants.DISTRIBUTE_LONG_TERM_GAIN_LOSS_ON_DATE);
            }
            if (!distributeGainsAndLossesIndicator || oldPooledFundValue.isShortTermGainLossDistributionComplete()) {
                fields.add(EndowPropertyConstants.SHORT_TERM_GAIN_LOSS_DISTRIBUTION_PER_UNIT);
                fields.add(EndowPropertyConstants.DISTRIBUTE_SHORT_TERM_GAIN_LOSS_ON_DATE);
            }

        }

        /*
         * Rule: In the pooled fund control, if the Distribute Gains And Losses = "N", LT_GAIN_LOSS / LT_PROC_ON_DT and ST_GAIN_LOSS
         * / ST_PROC_ON_DT fields in the PooledFundValue should be read only in the create/copy mode.
         */
        if (KRADConstants.MAINTENANCE_NEW_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction()) || KRADConstants.MAINTENANCE_COPY_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction())) {
            PooledFundValue newPooledFundValue = (PooledFundValue) document.getNewMaintainableObject().getBusinessObject();
            if (ObjectUtils.isNotNull(newPooledFundValue)) {
                String pooledSecurityID = newPooledFundValue.getPooledSecurityID();
                PooledFundControl theReferencePFC = pooledFundControlService.getByPrimaryKey(pooledSecurityID);
                if (ObjectUtils.isNotNull(theReferencePFC)) {
                    Boolean distributeGainsAndLossesIndicator = theReferencePFC.isDistributeGainsAndLossesIndicator();
                    if (!distributeGainsAndLossesIndicator) {
                        fields.add(EndowPropertyConstants.LONG_TERM_GAIN_LOSS_DISTRIBUTION_PER_UNIT);
                        fields.add(EndowPropertyConstants.DISTRIBUTE_LONG_TERM_GAIN_LOSS_ON_DATE);
                        fields.add(EndowPropertyConstants.SHORT_TERM_GAIN_LOSS_DISTRIBUTION_PER_UNIT);
                        fields.add(EndowPropertyConstants.DISTRIBUTE_SHORT_TERM_GAIN_LOSS_ON_DATE);
                    }
                }
            }
        }
        return fields;
    }
}
