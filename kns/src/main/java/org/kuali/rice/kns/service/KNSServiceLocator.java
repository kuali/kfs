/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.service;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.question.Question;

/**
 * Service locator for the KRAD Web module
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @deprecated As of release 2.0
 */
@Deprecated
public class KNSServiceLocator {

    public static final String BUSINESS_OBJECT_AUTHORIZATION_SERVICE = "businessObjectAuthorizationService";
    public static final String BUSINESS_OBJECT_METADATA_SERVICE = "businessObjectMetaDataService";
    public static final String BUSINESS_OBJECT_DICTIONARY_SERVICE = "businessObjectDictionaryService";
    public static final String DATA_DICTIONARY_SERVICE = "dataDictionaryService";
    public static final String DICTIONARY_VALIDATION_SERVICE = "knsDictionaryValidationService";
    public static final String DOCUMENT_HELPER_SERVICE = "documentHelperService";
    public static final String LOOKUP_RESULTS_SERVICE = "lookupResultsService";
    public static final String KUALI_INQUIRABLE = "kualiInquirable";
    public static final String KUALI_LOOKUPABLE = "kualiLookupable";
    public static final String MAINTENANCE_DOCUMENT_DICTIONARY_SERVICE = "maintenanceDocumentDictionaryService";
    public static final String TRANSACTIONAL_DOCUMENT_DICTIONARY_SERVICE = "transactionalDocumentDictionaryService";
    public static final String SESSION_DOCUMENT_SERVICE = "knsSessionDocumentService";

    public static <T extends Object> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
        return getService(BUSINESS_OBJECT_AUTHORIZATION_SERVICE);
    }

    public static BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
        return getService(BUSINESS_OBJECT_METADATA_SERVICE);
    }

    public static DictionaryValidationService getKNSDictionaryValidationService() {
	return (DictionaryValidationService) getService(DICTIONARY_VALIDATION_SERVICE);
    }

    public static LookupResultsService getLookupResultsService() {
        return (LookupResultsService) getService(LOOKUP_RESULTS_SERVICE);
    }

    public static Inquirable getKualiInquirable() {
        return getService(KUALI_INQUIRABLE);
    }

    public static Lookupable getKualiLookupable() {
        return getService(KUALI_LOOKUPABLE);
    }

    public static MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        return getService(MAINTENANCE_DOCUMENT_DICTIONARY_SERVICE);
    }

    public static TransactionalDocumentDictionaryService getTransactionalDocumentDictionaryService() {
        return (TransactionalDocumentDictionaryService) getService(TRANSACTIONAL_DOCUMENT_DICTIONARY_SERVICE);
    }

    public static SessionDocumentService getSessionDocumentService() {
        return  getService(SESSION_DOCUMENT_SERVICE);
    }

    public static Lookupable getLookupable(String lookupableName) {
        return getService(lookupableName);
    }

    public static DataDictionaryService getDataDictionaryService() {
        return getService(DATA_DICTIONARY_SERVICE);
    }

    public static BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
        return getService(BUSINESS_OBJECT_DICTIONARY_SERVICE);
    }

    public static DocumentHelperService getDocumentHelperService() {
        return getService(DOCUMENT_HELPER_SERVICE);
    }

    public static Question getQuestion(String questionName) {
        return (Question) getService(questionName);
    }
}
