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
package org.kuali.kfs.module.tem.service;

import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;

/**
 * define the notification to the traveler based on the email preferences stored in the traveler profile
 */
public interface TravelDocumentNotificationService {

    /**
     * send notification when the status of the given travel document is changed
     *
     * @param travelDocument the given travel document
     * @param statusChangeDTO the given document status change
     */
    void sendNotificationOnChange(TravelDocument travelDocument, DocumentRouteStatusChange statusChangeDTO);
}
