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
     * 
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
