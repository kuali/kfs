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
package org.kuali.kfs.pdp.util;

import org.kuali.rice.kim.api.identity.Person;

/**
 * PDP Batch Question Callback defines a callback method for post processing handling in the question interface.
 */
public interface PdpBatchQuestionCallback {

    /**
     * Hooks for performing different actions on batch after a question has been performed.
     * 
     * @param batchIdString the id of the batch
     * @param note a note from the user
     * @param user the user that perfoms the action
     * @return true if succesful, false otherwise
     */
    public boolean doPostQuestion(String batchIdString, String note, Person user);

}

