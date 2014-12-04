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
package org.kuali.kfs.module.ar.document.validation;

import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.rice.krad.document.TransactionalDocument;

/**
 * Interface for business rule of Collection Activity Document Detail.
 */
public interface AddCollectionActivityDocumentRule<F extends TransactionalDocument>  {

    /**
     * This method is called when a collection event is added
     *
     * @param transactionalDocument the event document
     * @param event the event to be added
     * @return true if valid to be added, false otherwise
     */
    public boolean processAddCollectionEventBusinessRules(F transactionalDocument, CollectionEvent event);
}
