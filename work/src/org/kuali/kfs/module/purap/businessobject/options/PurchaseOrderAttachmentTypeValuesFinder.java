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
package org.kuali.kfs.module.purap.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants.AttachmentTypeCodes;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class PurchaseOrderAttachmentTypeValuesFinder extends KeyValuesBase {

    public List getKeyValues() {
        List keyValues = new ArrayList();

        keyValues.add(new ConcreteKeyValue("", ""));
        keyValues.add(new ConcreteKeyValue(AttachmentTypeCodes.ATTACHMENT_TYPE_CONTRACTS, AttachmentTypeCodes.ATTACHMENT_TYPE_CONTRACTS));
        keyValues.add(new ConcreteKeyValue(AttachmentTypeCodes.ATTACHMENT_TYPE_QUOTE, AttachmentTypeCodes.ATTACHMENT_TYPE_QUOTE));
        keyValues.add(new ConcreteKeyValue(AttachmentTypeCodes.ATTACHMENT_TYPE_RFP, AttachmentTypeCodes.ATTACHMENT_TYPE_RFP));
        keyValues.add(new ConcreteKeyValue(AttachmentTypeCodes.ATTACHMENT_TYPE_RFP_RESPONSES, AttachmentTypeCodes.ATTACHMENT_TYPE_RFP_RESPONSES));
        keyValues.add(new ConcreteKeyValue(AttachmentTypeCodes.ATTACHMENT_TYPE_CONTRACT_AMENDMENTS, AttachmentTypeCodes.ATTACHMENT_TYPE_CONTRACT_AMENDMENTS));
        keyValues.add(new ConcreteKeyValue(AttachmentTypeCodes.ATTACHMENT_TYPE_OTHER_RESTRICTED, AttachmentTypeCodes.ATTACHMENT_TYPE_OTHER_RESTRICTED));
        keyValues.add(new ConcreteKeyValue(AttachmentTypeCodes.ATTACHMENT_TYPE_OTHER, AttachmentTypeCodes.ATTACHMENT_TYPE_OTHER));

        return keyValues;
    }

}
