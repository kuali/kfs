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
package org.kuali.kfs.module.tem.businessobject;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name="TEM_ATTENDEE_T")
public class Attendee  extends PersistableBusinessObjectBase{

    public static Logger LOG = Logger.getLogger(Attendee.class);

    @GeneratedValue(generator="TEM_ATTENDEE_ID_SEQ")
    @SequenceGenerator(name="TEM_ATTENDEE_ID_SEQ",sequenceName="TEM_ATTENDEE_ID_SEQ", allocationSize=5)
    private Integer id;
    private String company;
    private String title;
    private String attendeeType;
    private String name;
    private String documentNumber;


    @Id
    @Column(name="id",nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }
    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    @Column(name="FDOC_NBR")
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    @Column(name="COMPANY",length=40,nullable=false)
    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    @Column(name="TITLE",length=40,nullable=false)
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Column(name="ATTENDEE_TYPE",length=40,nullable=false)
    public String getAttendeeType() {
        return attendeeType;
    }
    public void setAttendeeType(String attendeeType) {
        this.attendeeType = attendeeType;
    }
    @Column(name="NAME",length=40,nullable=false)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("rawtypes")
    public String getSequenceName() {
        Class boClass = getClass();
        String retval = "";
        try {
            boolean rethrow = true;
            Exception e = null;
            while (rethrow) {
                LOG.debug("Looking for id in "+ boClass.getName());
                try {
                    final Field idField = boClass.getDeclaredField("id");
                    final SequenceGenerator sequenceInfo = idField.getAnnotation(SequenceGenerator.class);

                    return sequenceInfo.sequenceName();
                }
                catch (Exception ee) {
                    // ignore and try again
                    LOG.debug("Could not find id in "+ boClass.getName());

                    // At the end. Went all the way up the hierarchy until we got to Object
                    if (Object.class.equals(boClass)) {
                        rethrow = false;
                    }

                    // get the next superclass
                    boClass = boClass.getSuperclass();
                    e = ee;
                }
            }

            if (e != null) {
                throw e;
            }
        }
        catch (Exception e) {
            LOG.error("Could not get the sequence name for business object "+ getClass().getSimpleName());
            LOG.error(e.getMessage());
            if (LOG.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return retval;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("id", this.id);
        map.put("attendeeType", this.attendeeType);
        map.put("name", this.name);
        map.put("company", this.company);
        map.put("title", this.title);

        return map;
    }


}
