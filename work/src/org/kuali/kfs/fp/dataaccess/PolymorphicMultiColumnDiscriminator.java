/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.fp.dataaccess;

import java.util.Map;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.accesslayer.RowReaderDefaultImpl;
import org.apache.ojb.broker.metadata.ClassDescriptor;

/**
 * (Inspired by example posted at http://nagoya.apache.org/eyebrowse/ReadMsg?listName=ojb-user@db.apache.org&msgId=749837) This
 * class enables mapping multiple (presumably similar) classes to a single database table. Subclasses must implement the
 * getDiscriminatorColumns method, returning a String array of columns to consider when determining which class to return, as well
 * as implement the corresponding chooseClass method that acts on received values for those columns. Sample OBJ config:
 * <class-descriptor class="org.kuali.bo.ClassA" table="some_common_table" row-reader="org.kuali.dao.ojb.ClassADiscriminator"> ...
 * </class-descriptor> <class-descriptor class="org.kuali.bo.ClassB" table="some_common_table"
 * row-reader="org.kuali.dao.ojb.ClassBDiscriminator"> ... </class-descriptor> (where ClassADiscriminator and ClassBDiscriminator
 * extend PolymorphicMultiColumnDiscriminator)
 */
public abstract class PolymorphicMultiColumnDiscriminator extends RowReaderDefaultImpl {

    /** Column(s) that distinguish the parent class */
    private String[] column = null;

    public PolymorphicMultiColumnDiscriminator(ClassDescriptor cld) {
        super(cld);
        column = getDiscriminatorColumns();
    }

    /**
     * This method should return the column(s) necessary to determine which class to cast to.
     * 
     * @return one or more column names
     */
    public abstract String[] getDiscriminatorColumns();

    /**
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
