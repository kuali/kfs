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
package org.kuali.kfs.module.endow.document.service;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocument;

public interface UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService {

    /**
     * Updates the tax lots related to the given transaction line based on the accounting method and the transaction sub-type.
     * 
     * @param isUpdate true if the method will update the tax lots for the transaction line, false if the transaction line is just
     *        added and the tax lots are computed for the first time
     * @param endowmentTaxLotLinesDocument the Endowment tax lot lines Document for which we compute the transaction line related
     *        tax lots
     * @param transLine the transaction line for which we update the tax lots
     */
    public void updateTransactionLineTaxLots(boolean isUpdate, EndowmentTaxLotLinesDocument endowmentTaxLotLinesDocument, EndowmentTransactionLine transLine);

}
