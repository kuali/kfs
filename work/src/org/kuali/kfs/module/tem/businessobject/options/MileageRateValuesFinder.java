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
package org.kuali.kfs.module.tem.businessobject.options;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class MileageRateValuesFinder extends KeyValuesBase {
   protected static Logger LOG = Logger.getLogger(MileageRateValuesFinder.class);

   private String queryDate;

   /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
   @Override
   public List<KeyValue> getKeyValues() {
       java.sql.Date searchDate = getSearchDateFromDocument();
       if (searchDate == null) {
           searchDate = getSearchDateFromQueryDate();
       }
       return getTravelDocumentService().getMileageRateKeyValues(searchDate);
   }

   /**
    * @return the date to search for mileage rates on, based on the effective date from the document if possible
    */
   protected java.sql.Date getSearchDateFromDocument() {
       final KualiForm currentForm = KNSGlobalVariables.getKualiForm();
       if (currentForm instanceof KualiDocumentFormBase && ((KualiDocumentFormBase)currentForm).getDocument() instanceof TravelDocument) {
           final TravelDocument travelDoc = (TravelDocument)((KualiDocumentFormBase)currentForm).getDocument();
           return travelDoc.getEffectiveDateForMileageRate(new ActualExpense());
       }
       return null;
   }

   /**
    * @return gets the date to search for mileage rates by from the injected query date
    */
   protected java.sql.Date getSearchDateFromQueryDate() {
       java.util.Date javaDate = null;
       final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
       if(!StringUtils.isBlank(queryDate)) {
           try {
               javaDate = df.parse(queryDate);
           } catch (ParseException ex) {
               LOG.error("unable to parse date: " + queryDate);
           }
       }
       Date searchDate = null;
       try {
           searchDate = getDateTimeService().convertToSqlDate(df.format(javaDate));
       }
       catch (ParseException ex) {
           LOG.error("unable to convert date: " + queryDate);
       }
       return searchDate;
   }

   /**
     * Gets the queryDate attribute.
     * @return Returns the queryDate.
     */
   public String getQueryDate() {
       return queryDate;
   }

   /**
     * Sets the queryDate attribute value.
     * @param queryDate The queryDate to set.
     */
   public void setQueryDate(String queryDate) {
       this.queryDate = queryDate;
   }

   protected TravelDocumentService getTravelDocumentService() {
       return SpringContext.getBean(TravelDocumentService.class);
   }

   protected DateTimeService getDateTimeService() {
       return SpringContext.getBean(DateTimeService.class);
   }

}

