/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.dao.ojb;

import java.util.Map;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.accesslayer.RowReaderDefaultImpl;
import org.apache.ojb.broker.metadata.ClassDescriptor;

/**
 * (Inspired by example posted at http://nagoya.apache.org/eyebrowse/ReadMsg?listName=ojb-user@db.apache.org&msgId=749837)
 * 
 * This class enables mapping multiple (presumably similar) classes to a single database table. Subclasses must implement the
 * getDiscriminatorColumns method, returning a String array of columns to consider when determining which class to return, as well
 * as implement the corresponding chooseClass method that acts on received values for those columns.
 * 
 * Sample OBJ config:
 * 
 * <class-descriptor class="org.kuali.bo.ClassA" table="some_common_table"row-reader="org.kuali.dao.ojb.ClassADiscriminator"> ...
 * </class-descriptor>
 * 
 * <class-descriptor class="org.kuali.bo.ClassB" table="some_common_table"row-reader="org.kuali.dao.ojb.ClassBDiscriminator"> ...
 * </class-descriptor>
 * 
 * (where ClassADiscriminator and ClassBDiscriminator extend PolymorphicMultiColumnDiscriminator)
 * 
 * @author Kuali Financial Transactions Team ()
 */
public abstract class PolymorphicMultiColumnDiscriminator extends RowReaderDefaultImpl {

    /** Column(s) that distinguish the parent class */
    private String[] column = null;

    public PolymorphicMultiColumnDiscriminator(ClassDescriptor cld) {
        super(cld);
        column = getDiscriminatorColumns();
    }

    /**
     * 
     * This method should return the column(s) necessary to determine which class to cast to.
     * 
     * @return - one or more column names
     */
    public abstract String[] getDiscriminatorColumns();

    /**
     * 
     * Based on the received key values, this method determines the appropriate class.
     * 
     * @param values
     * @return an appropriately chosen class
     */
    public abstract Class chooseClass(String[] values);

    protected ClassDescriptor selectClassDescriptor(Map row) throws PersistenceBrokerException {
        String[] key = new String[column.length];

        for (int i = 0; i < column.length; i++) {
            key[i] = (String) row.get(column[i]);
        }

        Class clazz = null;

        if (key != null) {
            clazz = chooseClass(key);
        }
        if (clazz == null) {
            return getClassDescriptor();
        }

        PersistenceBroker broker = null;
        try {
            broker = PersistenceBrokerFactory.defaultPersistenceBroker();
            ClassDescriptor result = broker.getClassDescriptor(clazz);
            broker.close();
            if (result == null) {
                return getClassDescriptor();
            }
            else {
                return result;
            }
        }
        catch (PersistenceBrokerException e) {
            broker.close();
            throw e;
        }
    }

}
