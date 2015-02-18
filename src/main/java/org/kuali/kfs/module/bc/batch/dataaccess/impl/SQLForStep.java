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
package org.kuali.kfs.module.bc.batch.dataaccess.impl;

import java.util.ArrayList;

/**
 * 
 * implements a type to hold the raw SQL for each step in a process.  allows us to have an insertion point for system parameters which can only be 
 * incorporated into the SQL at run-time.  provides a method to insert the parameter string and return the finished SQL as a string,
 * and another to just return SQL as a string without inserting anything, therefore permitting most of the SQL to be built in a constructor and finished at run time.
 */

public class SQLForStep {

    // locations in the StringBuilder in which a run-time parameter string is to be incorporated
    // (maybe an int[] will be faster when a user is waiting for the SQL to be built 
    private int[] insertionPoints = {};
    // string buffers are thread-safe, while string builders are not
    // string builders are more efficient, so use one here and never modify it in our public methods
    private StringBuilder sqlBuilder;
    
    public SQLForStep(StringBuilder sqlBuilder)
    {
        // use this constructor when the SQL needs no insertions
        // make a copy of the static SQL, so this type acts as a container for SQL to be passed ot other methods and used repeatedly.
        this.sqlBuilder = new StringBuilder(sqlBuilder);
    }
    
    public SQLForStep(StringBuilder sqlBuilder, ArrayList<Integer> insertionPoints)
    {
        // use this constructor when there are run-time strings to be inserted into the SQL
        this.sqlBuilder = new StringBuilder(sqlBuilder);
        int pointCount = insertionPoints.size();
        this.insertionPoints = new int[pointCount];
        for (int idx = 0; idx < pointCount; idx++) 
        {
            this.insertionPoints[idx] = (int) insertionPoints.get(idx);
        }
    }
    
    public String getSQL(ArrayList<String> parameterToInsert)
    {
        // make a copy of the SQL, so this routine is thread-safe, and evey caller gets her own copy of the static SQL to change.
        // string buffers are thread-safe, while string builders are not, but making a copy puts everything on the stack of the local thread anyway,
        // so we don't need the string buffer synchronization and can use the more efficient StringBuilder.
        if (parameterToInsert.size() != insertionPoints.length) {
            // throw an out of bounds exception if there is a mismatch
            // we can't build the SQL
            throw(new IndexOutOfBoundsException("the number of strings to be inserted into SQL does not match the number of insertion points"));
        }
        StringBuilder unfinishedSQL = new StringBuilder(sqlBuilder);
        // insertions will add to the length of the String
        // we need to increase the value of the insertion point based on the leghtns
        int lengthSoFar = 0;
        for (int idx = 0; idx < insertionPoints.length; idx++)
        {
            String parameterString = parameterToInsert.get(idx);
            unfinishedSQL.insert(insertionPoints[idx]+lengthSoFar,parameterString);
            lengthSoFar = lengthSoFar+parameterString.length();
        }
        return(unfinishedSQL.toString());
    }
    
    public String getSQL()
    {
        // no changes needed to the StringBuilder.  just return a string built from the SQL StringBuilder.
        return(sqlBuilder.toString());
    }
}
