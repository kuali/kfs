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
package org.kuali.kfs.fp.batch.service.impl;

import java.util.Collection;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.VelocityEmailService;
import org.kuali.kfs.sys.service.impl.VelocityEmailServiceBase;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ProcurementCardCreateEmailServiceImpl extends VelocityEmailServiceBase implements VelocityEmailService{
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardCreateEmailServiceImpl.class);
    private String templateUrl;

    @Override
    public String getEmailSubject() {
        return "KFS Pcard Load Summary ";
    }

    @Override
    public Collection<String> getProdEmailReceivers() {
        Collection<String> emailReceivers = CoreFrameworkServiceLocator.getParameterService().getParameterValuesAsString(KFSConstants.CoreModuleNamespaces.FINANCIAL, KFSConstants.ProcurementCardParameters.PCARD_BATCH_CREATE_DOC_STEP, KFSConstants.ProcurementCardParameters.PCARD_BATCH_SUMMARY_TO_EMAIL_ADDRESSES);
        return emailReceivers;
    }

    /**
     * @see org.kuali.kfs.sys.service.impl.VelocityEmailServiceBase#getTemplateUrl()
     */
    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    /**
     * Sets the templateUrl attribute value.
     *
     * @param templateUrl The templateUrl to set.
     */
    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

}
