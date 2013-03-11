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
package org.kuali.kfs.sys.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentDao;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentAdHocService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is a Financial System specific Document Service class to allow for the
 * {@link #findByDocumentHeaderStatusCode(Class, String)} method.
 */
@Transactional
public class FinancialSystemDocumentServiceImpl implements FinancialSystemDocumentService {
    private FinancialSystemDocumentDao financialSystemDocumentDao;
    private DocumentService documentService;
    private ParameterService parameterService;
    protected DocumentAdHocService documentAdHocService;

    private static final int DEFAULT_FETCH_MORE_ITERATION_LIMIT = 10;

    public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinancialSystemDocumentServiceImpl.class);


    /**
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#findByDocumentHeaderStatusCode(java.lang.Class,
     *      java.lang.String)
     */
    @Override
    public <T extends Document> Collection<T> findByDocumentHeaderStatusCode(Class<T> clazz, String statusCode) throws WorkflowException {
        Collection<T> foundDocuments = getFinancialSystemDocumentDao().findByDocumentHeaderStatusCode(clazz, statusCode);
        for (Document doc : foundDocuments) {
            documentAdHocService.addAdHocs(doc);
        }

        Collection<T> returnDocuments = new ArrayList<T>();
        for (Iterator<T> iter = foundDocuments.iterator(); iter.hasNext();) {
            Document doc = iter.next();
            returnDocuments.add((T) getDocumentService().getByDocumentHeaderId(doc.getDocumentNumber()));
        }
        return returnDocuments;
    }


    /**
     * Returns the maximum number of results that should be returned from the document search.
     *
     * @param criteria the criteria in which to check for a max results value
     * @return the maximum number of results that should be returned from a document search
     */
    public int getMaxResultCap(DocumentSearchCriteria criteria) {
        int systemLimit = KewApiConstants.DOCUMENT_LOOKUP_DEFAULT_RESULT_CAP;
        String resultCapValue = getParameterService().getParameterValueAsString(KewApiConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE, KewApiConstants.DOC_SEARCH_RESULT_CAP);
        if (StringUtils.isNotBlank(resultCapValue)) {
            try {
                int configuredLimit = Integer.parseInt(resultCapValue);
                if (configuredLimit <= 0) {
                    LOG.warn(KewApiConstants.DOC_SEARCH_RESULT_CAP + " was less than or equal to zero.  Please use a positive integer.");
                } else {
                    systemLimit = configuredLimit;
                }
            } catch (NumberFormatException e) {
                LOG.warn(KewApiConstants.DOC_SEARCH_RESULT_CAP + " is not a valid number.  Value was " + resultCapValue + ".  Using default: " + KewApiConstants.DOCUMENT_LOOKUP_DEFAULT_RESULT_CAP);
            }
        }
        int maxResults = systemLimit;
        if (criteria.getMaxResults() != null) {
            int criteriaLimit = criteria.getMaxResults().intValue();
            if (criteriaLimit > systemLimit) {
                LOG.warn("Result set cap of " + criteriaLimit + " is greater than system value of " + systemLimit);
            } else {
                if (criteriaLimit < 0) {
                    LOG.warn("Criteria results limit was less than zero.");
                    criteriaLimit = 0;
                }
                maxResults = criteriaLimit;
            }
        }
        return maxResults;
    }

    public int getFetchMoreIterationLimit() {
        int fetchMoreLimit = DEFAULT_FETCH_MORE_ITERATION_LIMIT;
        String fetchMoreLimitValue = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KewApiConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE, KewApiConstants.DOC_SEARCH_FETCH_MORE_ITERATION_LIMIT);
        if (!StringUtils.isBlank(fetchMoreLimitValue)) {
            try {
                fetchMoreLimit = Integer.parseInt(fetchMoreLimitValue);
                if (fetchMoreLimit < 0) {
                    LOG.warn(KewApiConstants.DOC_SEARCH_FETCH_MORE_ITERATION_LIMIT + " was less than zero.  Please use a value greater than or equal to zero.");
                    fetchMoreLimit = DEFAULT_FETCH_MORE_ITERATION_LIMIT;
                }
            } catch (NumberFormatException e) {
                LOG.warn(KewApiConstants.DOC_SEARCH_FETCH_MORE_ITERATION_LIMIT + " is not a valid number.  Value was " + fetchMoreLimitValue);
            }
        }
        return fetchMoreLimit;
    }

    @Override
    public void prepareToCopy(FinancialSystemDocumentHeader oldDocumentHeader, FinancialSystemTransactionalDocument document) {
        // This method serves as a plugin to add logic to the copy functionality, when needed.
    }

    public FinancialSystemDocumentDao getFinancialSystemDocumentDao() {
        return financialSystemDocumentDao;
    }

    public void setFinancialSystemDocumentDao(FinancialSystemDocumentDao financialSystemDocumentDao) {
        this.financialSystemDocumentDao = financialSystemDocumentDao;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setDocumentAdHocService(DocumentAdHocService documentAdHocService) {
        this.documentAdHocService = documentAdHocService;
    }


    /**
     * Gets the parameterService attribute.
     *
     * @return Returns the parameterService
     */

    public ParameterService getParameterService() {
        return parameterService;
    }


    /**
     * Sets the parameterService attribute.
     *
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }





}
