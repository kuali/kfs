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
package org.kuali.core.db.torque;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.engine.database.model.Database;
import org.xml.sax.Attributes;


/**
 * A class for holding application data structures.
 *
 * @author <a href="mailto:leon@opticode.co.za>Leon Messerschmidt</a>
 * @author <a href="mailto:jmcnally@collab.net>John McNally</a>
 * @author <a href="mailto:mpoeschl@marmot.at>Martin Poeschl</a>
 * @author <a href="mailto:dlr@collab.net>Daniel Rall</a>
 * @author <a href="mailto:byron_foster@byron_foster@yahoo.com>Byron Foster</a>
 * @author <a href="mailto:monroe@dukece.com>Greg Monroe</a>
 * @version $Id: KualiDatabase.java,v 1.1 2007-10-21 07:57:26 abyrne Exp $
 */
public class KualiDatabase extends Database
{
    /** Logging class from commons.logging */
    //private static Log log = LogFactory.getLog(KualiDatabase.class);

    private ArrayList<View> views = new ArrayList<View>(); 
    private ArrayList<Sequence> sequences = new ArrayList<Sequence>(); 

    /**
     * Creates a new instance for the specified database type.
     *
     * @param databaseType The default type for this database.
     */
    public KualiDatabase(String databaseType) {
    	super(databaseType);
    }

    public List<View> getViews() {
    	return views;
    }

    public List<Sequence> getSequences() {
    	return sequences;
    }
    
    public View addView( Attributes attrib ) {
    	View view = new View();
    	view.setName( attrib.getValue( "name" ) );
    	view.setDefinition( attrib.getValue( "viewdefinition" ) );
    	views.add( view );
    	return view;
    }
    public Sequence addSequence( Attributes attrib ) {
    	Sequence sequence = new Sequence();
    	sequence.setName( attrib.getValue( "name" ) );
    	sequence.setNextVal( attrib.getValue( "nextval" ) );
    	
    	sequences.add( sequence );
    	return sequence;
    }
}
