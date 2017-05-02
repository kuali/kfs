package edu.arizona.kfs.module.purap.document.service;

import org.kuali.rice.kew.api.action.ActionRequest;

public interface PurApWorkflowIntegrationService extends org.kuali.kfs.module.purap.document.service.PurApWorkflowIntegrationService {

    /**
     * Clears an FYI request as the specified super user
     *
     * @param actionRequest
     * @return
     */
    public boolean clearFYIRequestAsSuperUser(ActionRequest actionRequest, String annotation);

}
