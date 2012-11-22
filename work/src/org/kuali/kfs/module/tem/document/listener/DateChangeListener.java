/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.listener;

import static org.kuali.kfs.sys.context.SpringContext.getBean;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.rice.krad.service.DocumentService;


/**
 * Executed when a {@link Date} is changed (as in tripBegin and tripEnd) on a {@link TravelReimbursementDocument}
 * 
 * @author Leo Przybylski (leo [at] rsmart.com
 */
public class DateChangeListener implements PropertyChangeListener, java.io.Serializable {

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        final TravelReimbursementDocument reimbursement = (TravelReimbursementDocument) event.getSource();
    }

    protected TravelReimbursementService getTravelReimbursementService() {
        return getBean(TravelReimbursementService.class);
    }

    protected DocumentService getDocumentService() {
        return getBean(DocumentService.class);
    }
}
