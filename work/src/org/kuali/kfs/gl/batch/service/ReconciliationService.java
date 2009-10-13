/*
 * Copyright 2007 The Kuali Foundation
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
