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
