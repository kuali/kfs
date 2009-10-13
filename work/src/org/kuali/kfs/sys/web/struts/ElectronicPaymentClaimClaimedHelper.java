/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.web.struts;


/**
 * A class that helps Struts record which "claimed" checkboxes were clicked on the Electronic Fund Transfer
 * claiming screen.
 */
public class ElectronicPaymentClaimClaimedHelper {
    private String electronicPaymentClaimRepresentation;
    
    /**
     * Gets the electronicPaymentClaimRepresentation attribute. 
     * @return Returns the electronicPaymentClaimRepresentation.
     */
    public String getElectronicPaymentClaimRepresentation() {
        return electronicPaymentClaimRepresentation;
    }

    /**
     * Sets the electronicPaymentClaimRepresentation attribute value.
     * @param electronicPaymentClaimRepresentation The electronicPaymentClaimRepresentation to set.
     */
    public void setElectronicPaymentClaimRepresentation(String electronicPaymentClaimRepresentation) {
        this.electronicPaymentClaimRepresentation = electronicPaymentClaimRepresentation;
    }
}
