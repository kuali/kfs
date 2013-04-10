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
