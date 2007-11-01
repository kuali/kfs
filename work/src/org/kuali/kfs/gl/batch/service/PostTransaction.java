/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Created on Oct 12, 2005
 *
 */
package org.kuali.module.gl.batch.poster;

import java.util.Date;

import org.kuali.module.gl.bo.Transaction;

/**
 * 
 * 
 */
public interface PostTransaction {
    /**
     * Post a single transaction to a single destination.
     * 
     * @param t Transaction to post
     * @param mode PosterService.MODE_ENTRIES or PosterService.MODE_REVERSAL
     * @param postDate post date/time
     * @return The letter I if a row was inserted, U updated, D deleted. The string can have multiple codes.
     */
    public String post(Transaction t, int mode, Date postDate);

    /**
     * The name of the destination for the post
     * 
     * @return name
     */
    public String getDestinationName();
}
