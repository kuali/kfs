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
package org.kuali.kfs.integration.ar;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

/**
 * Integration interface for InvoiceTemplate
 */
public interface AccountsReceivableInvoiceTemplate extends MutableInactivatable, ExternalizableBusinessObject {


    /**
     * Gets the date attribute.
     *
     * @return Returns the date.
     */
    public String getDate();

    /**
     * Gets the filepath attribute.
     *
     * @return Returns the filepath.
     */
    public String getFilepath();

    /**
     * Gets the accessRestrictedIndicator attribute.
     *
     * @return Returns the accessRestrictedIndicator.
     */
    public boolean isAccessRestrictedIndicator();

    /**
     * Sets the billByChartOfAccountCode attribute value.
     *
     * @param billByChartOfAccountCode The billByChartOfAccountCode to set.
     */
    public void setBillByChartOfAccountCode(String billByChartOfAccountCode);

    /**
     * Sets the billedByOrganizationCode attribute value.
     *
     * @param billedByOrganizationCode The billedByOrganizationCode to set.
     */
    public void setBilledByOrganizationCode(String billedByOrganizationCode);

    /**
     * Sets the billByChartOfAccount attribute value.
     *
     * @param billByChartOfAccount The billByChartOfAccount to set.
     */
    public void setBillByChartOfAccount(Chart billByChartOfAccount);

    /**
     * Sets the billedByOrganization attribute value.
     *
     * @param billedByOrganization The billedByOrganization to set.
     */
    public void setBilledByOrganization(Organization billedByOrganization);

    /**
     * Gets the filename attribute.
     *
     * @return Returns the filename.
     */
    public String getFilename();


    public String getInvoiceTemplateCode();

    public String getInvoiceTemplateDescription();

    /**
     * This method returns if the current user has a valid organization as compared to the organization associated with this invoice
     * template
     *
     * @return
     */
    public boolean isValidOrganization();
}
