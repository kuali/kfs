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
package org.kuali.kfs.gl.batch.service;

import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.gl.batch.service.impl.ReconciliationBlock;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.Message;

/**
 * An object to reconcile origin entries. Since the {@link #reconcile(Iterator, ReconciliationBlock, List)} method below takes in an
 * iterator (which may load entries from the DB as the {@link Iterator#next()} method is called), it is probably desirable for
 * implementations to annotate the class as Transactional.
 */
public interface ReconciliationService {

    /**
     * Performs the reconciliation on origin entries using the data from the {@link ReconciliationBlock} parameter
     * 
     * @param entries origin entries
     * @param reconBlock reconciliation data
     * @param errorMessages a non-null list onto which error messages will be appended. This list will be modified by reference.
     */
    public void reconcile(Iterator<OriginEntryFull> entries, ReconciliationBlock reconBlock, List<Message> errorMessages);
}
