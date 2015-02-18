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
package org.kuali.kfs.fp.document.validation.event;

import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.document.Document;

/**
 * An event which is fired when a member of the Cash Receipt family of documents deletes a check. 
 */
public class DeleteCheckEvent extends AttributedDocumentEventBase implements CheckEvent {
    private final Check check;
    
    /**
     * Initializes fields common to all subclasses
     * 
     * @param description
     * @param errorPathPrefix
     * @param document
     * @param check
     */
    public DeleteCheckEvent(String description, String errorPathPrefix, Document document, Check check) {
        super(description, errorPathPrefix, document);

        this.check = check;
    }
    
    /**
     * Constructs a DeleteCheckEvent with an empty description
     * @param errorPathPrefix
     * @param document
     * @param check
     */
    public DeleteCheckEvent(String errorPathPrefix, Document document, Check check) {
        this("", errorPathPrefix, document, check);
    }
    
    /**
     * @see org.kuali.rice.krad.rule.event.CheckEvent#getCheck()
     */
    public Check getCheck() {
        return check;
    }
}
