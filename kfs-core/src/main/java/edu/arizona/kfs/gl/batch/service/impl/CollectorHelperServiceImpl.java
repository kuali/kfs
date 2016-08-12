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
package edu.arizona.kfs.gl.batch.service.impl;

import edu.arizona.kfs.gl.service.GlobalTransactionEditService;
import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.batch.CollectorStep;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.util.MessageMap;

/**
 * The base implementation of CollectorHelperService
 *
 * @see org.kuali.kfs.gl.batch.service.CollectorService
 */
public class CollectorHelperServiceImpl extends org.kuali.kfs.gl.batch.service.impl.CollectorHelperServiceImpl {

    private GlobalTransactionEditService globalTransactionEditService;


    /**
     * Performs the following checks on the collector batch: Any errors will be contained in GlobalVariables.MessageMap
     *
     * @param batch      - batch to validate
     * @param MessageMap the map into which to put errors encountered during validation
     * @return boolean - true if validation was successful, false it not
     */
    protected boolean performValidation(CollectorBatch batch, MessageMap messageMap) {
        boolean valid = performCollectorHeaderValidation(batch, messageMap);

        performUppercasing(batch);

        boolean performDuplicateHeaderCheck = parameterService.getParameterValueAsBoolean(CollectorStep.class, KFSConstants.SystemGroupParameterNames.COLLECTOR_PERFORM_DUPLICATE_HEADER_CHECK);
        if (valid && performDuplicateHeaderCheck) {
            valid = duplicateHeaderCheck(batch, messageMap);
        }
        if (valid) {
            valid = checkForMixedDocumentTypes(batch, messageMap);
        }

        if (valid) {
            valid = checkForMixedBalanceTypes(batch, messageMap);
        }

        if (valid) {
            valid = checkDetailKeys(batch, messageMap);
        }

        if (valid) {
            valid = this.globalTransactionEditService.isAccountingLineBatchAllowable(batch, messageMap);
        }

        return valid;
    }


    public void setGlobalTransactionEditService(GlobalTransactionEditService globalTransactionEditService) {
        this.globalTransactionEditService = globalTransactionEditService;
    }

}
