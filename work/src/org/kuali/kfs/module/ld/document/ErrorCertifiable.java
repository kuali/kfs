/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.document;

import org.kuali.kfs.module.ld.businessobject.ErrorCertification;

/**
 * Interface to go along with Error Certification object. Any document that implements this interface will be able to have an error
 * certification object and the related validation.
 */
public interface ErrorCertifiable {
    /**
     * Retrieves the ErrorCertification object in implementing class.
     *
     * @return ErrorCertification object
     */
    ErrorCertification getErrorCertification();

    /**
     * Attaches an ErrorCertification object to implementing class.
     *
     * @param errorCertification
     */
    void setErrorCertification(ErrorCertification errorCertification);
}
