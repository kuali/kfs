/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.purap.fixtures;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.document.RequisitionDocument;

public enum RequisitionDocumentFixture {

    REQ_ONLY_REQUIRED_FIELDS(null, null, null, null, null, null, null, null, null),
    REQ_BREAK_APO_ENTER_ALTERNATE_VENDOR_NAMES(null, null, null, "NFL Shop", "Dicks Sporting Goods", null, null, null, null),
    ;

    public final String requisitionOrganizationReference1Text;
    public final String requisitionOrganizationReference2Text;
    public final String requisitionOrganizationReference3Text;
    public final String alternate1VendorName;
    public final String alternate2VendorName;
    public final String alternate3VendorName;
    public final String alternate4VendorName;
    public final String alternate5VendorName;
    public final KualiDecimal organizationAutomaticPurchaseOrderLimit;

    private RequisitionDocumentFixture(
            String requisitionOrganizationReference1Text,
            String requisitionOrganizationReference2Text,
            String requisitionOrganizationReference3Text,
            String alternate1VendorName,
            String alternate2VendorName,
            String alternate3VendorName,
            String alternate4VendorName,
            String alternate5VendorName,
            KualiDecimal organizationAutomaticPurchaseOrderLimit) {
        this.requisitionOrganizationReference1Text = requisitionOrganizationReference1Text;
        this.requisitionOrganizationReference2Text = requisitionOrganizationReference2Text;
        this.requisitionOrganizationReference3Text = requisitionOrganizationReference3Text;
        this.alternate1VendorName = alternate1VendorName;
        this.alternate2VendorName = alternate2VendorName;
        this.alternate3VendorName = alternate3VendorName;
        this.alternate4VendorName = alternate4VendorName;
        this.alternate5VendorName = alternate5VendorName;
        this.organizationAutomaticPurchaseOrderLimit = organizationAutomaticPurchaseOrderLimit;
    }

    public RequisitionDocument createRequisitionDocument(PurchasingAccountsPayableDocumentFixture purapFixture, 
            PurchasingDocumentFixture purFixture) {
        RequisitionDocument doc = purFixture.createRequisitionDocument(purapFixture);
        doc.setRequisitionOrganizationReference1Text(this.requisitionOrganizationReference1Text);
        doc.setRequisitionOrganizationReference2Text(this.requisitionOrganizationReference2Text);
        doc.setRequisitionOrganizationReference3Text(this.requisitionOrganizationReference3Text);
        doc.setAlternate1VendorName(this.alternate1VendorName);
        doc.setAlternate2VendorName(this.alternate2VendorName);
        doc.setAlternate3VendorName(this.alternate3VendorName);
        doc.setAlternate4VendorName(this.alternate4VendorName);
        doc.setAlternate5VendorName(this.alternate5VendorName);
        doc.setOrganizationAutomaticPurchaseOrderLimit(this.organizationAutomaticPurchaseOrderLimit);

        //need to call refresh here to update all references
        doc.refresh();
        return doc;
    }
    
}
