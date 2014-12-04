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
package org.kuali.impex.clover.component;

import org.jetel.component.DBOutputTable;
import org.jetel.exception.XMLConfigurationException;
import org.jetel.graph.Node;
import org.jetel.graph.TransformationGraph;
import org.jetel.metadata.DataFieldMetadata;
import org.jetel.metadata.DataRecordMetadata;
import org.jetel.util.property.ComponentXMLAttributes;
import org.w3c.dom.Element;

/**
 * This class extends from DBOutputTable.
 * <p>Instead of reading from the database dictionary for the column to be inserted, it uses
 * the format file (metadata) passed in.
 * <p>The import graph will still complete even if the underline schema has more fields than what's 
 * specified in the format file.  
 */
public class KualiDBOutputTable extends DBOutputTable {
	
	public static final String UC_COMPONENT_TYPE = "KUL_DB_OUTPUT_TABLE";
	public static final String USE_INPUT_META_ATTRIBUTE = "metadata";
	
	
	public KualiDBOutputTable(String id, String dbConnectionName, String dbTableName) {
		super(id, dbConnectionName, dbTableName);
	}

	/**
	 * @param id Unique ID of component
	 * @param dbConnectionName Name of Clover's database connection to be used for communicating with DB
	 * @param sqlQuery set of sql queries
	 */
	public KualiDBOutputTable(String id, String dbConnectionName, String[] sqlQuery) {
		super(id, dbConnectionName, sqlQuery);
	}
	
	@Deprecated
	public KualiDBOutputTable(String id, String dbConnectionName, String sqlQuery, String[] cloverFields) {
		super(id, dbConnectionName, sqlQuery, cloverFields );
	}

    public static Node fromXML(TransformationGraph graph, Element xmlElement) throws XMLConfigurationException {
    	DBOutputTable outputTable = null;
    	
  		outputTable = (DBOutputTable) DBOutputTable.fromXML( graph, xmlElement );
    	
		ComponentXMLAttributes xattribs = new ComponentXMLAttributes(xmlElement, graph);

		try {
			if (xattribs.exists(USE_INPUT_META_ATTRIBUTE) )
			{
				DataRecordMetadata metadata = graph.getDataRecordMetadata( xattribs.getString(USE_INPUT_META_ATTRIBUTE));
				metadata.getFields();

				DataFieldMetadata[] fields = metadata.getFields();
				String[] dbFields = new String[fields.length];
				
				for (int i = 0; i < fields.length; i++)
				{
					dbFields[i] = fields[i].getName();
		        }
				outputTable.setDBFields(dbFields);
			}
			return outputTable;
			
		} catch (Exception ex) {
			ex.printStackTrace();
            throw new XMLConfigurationException(UC_COMPONENT_TYPE + ":" + xattribs.getString(XML_ID_ATTRIBUTE," unknown ID ") + ":" + ex.getMessage(),ex);
        }
	}
    
	public String getType(){
		return UC_COMPONENT_TYPE;
	}
}
	
