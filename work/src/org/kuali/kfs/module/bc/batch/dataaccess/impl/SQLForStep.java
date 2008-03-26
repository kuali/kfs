/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.dao.jdbc;

/**
 * 
 * implements a type to hold the raw SQL for each step in a process.  allows us to have an insertion point for system parameters which can only be 
 * incorporated into the SQL at run-time.  provides a method to insert the parameter string and return the finished SQL as a string,
 * and another to just return SQL as a string without inserting anything, therefore permitting most of the SQL to be built in a constructor and finished at run time.
 */

public class SQLForStep {

    // location in the StringBuilder in which a run-time parameter string is to be incorporated
    private int insertionPoint = -1;
    // string buffers are thread-safe, while string builders are not
    // string builders are more efficient, so use one here and never modify it in our public methods
    private StringBuilder sqlBuilder;
    
    public SQLForStep(StringBuilder sqlBuilder)
    {
        // use this constructor when the SQL needs no insertions
        // make a copy of the static SQL, so this type acts as a container for SQL to be passed ot other methods and used repeatedly.
        this.sqlBuilder = new StringBuilder(sqlBuilder);
    }
    
    public SQLForStep(StringBuilder sqlBuilder, int insertionPoint)
    {
        // use this constructor when there is a run-time string to be inserted into the SQL
        this.sqlBuilder = new StringBuilder(sqlBuilder);
        this.insertionPoint = insertionPoint;
    }
    
    public String getSQL(String parameterToInsert)
    {
        // make a copy of the SQL, so this routine is thread-safe, and evey caller gets her own copy of the static SQL to change.
        // string buffers are thread-safe, while string builders are not, but making a copy puts everything on the stack of the local thread anyway,
        // so we don't need the string buffer synchronization and can use the more efficient StringBuilder.
        // the StringBuilder will throw and out of bounds exception if we try to insert at -1 because no insertion point has been set.
        StringBuilder unfinishedSQL = new StringBuilder(sqlBuilder);
        unfinishedSQL.insert(insertionPoint,parameterToInsert);
        return(unfinishedSQL.toString());
    }
    
    public String getSQL()
    {
        // no changes needed to the StringBuilder.  just return a string built from the SQL StringBuilder.
        return(sqlBuilder.toString());
    }
}
