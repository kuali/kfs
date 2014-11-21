package org.apache.torque.task;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.transform.XmlToAppData;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.texen.ant.TexenTask;

/**
 * A base torque task that uses either a single XML schema
 * representing a data model, or a &lt;fileset&gt; of XML schemas.
 * We are making the assumption that an XML schema representing
 * a data model contains tables for a <strong>single</strong>
 * database.
 *
 * @author <a href="mailto:jvanzyl@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 */
public class TorqueDataModelTask extends TexenTask
{
    /**
     * XML that describes the database model, this is transformed
     * into the application model object.
     */
    protected String xmlFile;

    /** Fileset of XML schemas which represent our data models. */
    protected List filesets = new ArrayList();

    /** Data models that we collect. One from each XML schema file. */
    protected List dataModels = new ArrayList();

    /** Velocity context which exposes our objects in the templates. */
    protected Context context;

    /**
     * Map of data model name to database name.
     * Should probably stick to the convention of them being the same but
     * I know right now in a lot of cases they won't be.
     */
    protected Hashtable dataModelDbMap;

    /**
     * Hashtable containing the names of all the databases
     * in our collection of schemas.
     */
    protected Hashtable databaseNames;

    //!! This is probably a crappy idea having the sql file -> db map
    // here. I can't remember why I put it here at the moment ...
    // maybe I was going to map something else. It can probably
    // move into the SQL task.

    /**
     * Name of the properties file that maps an SQL file
     * to a particular database.
     */
    protected String sqldbmap;

    /** The target database(s) we are generating SQL for. */
    private String targetDatabase;

    /** Target Java package to place the generated files in. */
    private String targetPackage;


    /**
     * Set the sqldbmap.
     *
     * @param sqldbmap th db map
     */
    public void setSqlDbMap(String sqldbmap)
    {
        //!! Make all these references files not strings.
        this.sqldbmap = getProject().resolveFile(sqldbmap).toString();
    }

    /**
     * Get the sqldbmap.
     *
     * @return String sqldbmap.
     */
    public String getSqlDbMap()
    {
        return sqldbmap;
    }

    /**
     * Return the data models that have been processed.
     *
     * @return List data models
     */
    public List getDataModels()
    {
        return dataModels;
    }

    /**
     * Return the data model to database name map.
     *
     * @return Hashtable data model name to database name map.
     */
    public Hashtable getDataModelDbMap()
    {
        return dataModelDbMap;
    }

    /**
     * Get the xml schema describing the application model.
     *
     * @return  String xml schema file.
     */
    public String getXmlFile()
    {
        return xmlFile;
    }

    /**
     * Set the xml schema describing the application model.
     *
     * @param xmlFile The new XmlFile value
     */
    public void setXmlFile(String xmlFile)
    {
        this.xmlFile = getProject().resolveFile(xmlFile).toString();
    }

    /**
     * Adds a set of xml schema files (nested fileset attribute).
     *
     * @param set a Set of xml schema files
     */
    public void addFileset(FileSet set)
    {
        filesets.add(set);
    }

    /**
     * Get the current target database.
     *
     * @return String target database(s)
     */
    public String getTargetDatabase()
    {
        return targetDatabase;
    }

    /**
     * Set the current target database. (e.g. mysql, oracle, ..)
     *
     * @param v target database(s)
     */
    public void setTargetDatabase(String v)
    {
        targetDatabase = v;
    }

    /**
     * Get the current target package.
     *
     * @return return target java package.
     */
    public String getTargetPackage()
    {
        return targetPackage;
    }

    /**
     * Set the current target package. This is where generated java classes will
     * live.
     *
     * @param v target java package.
     */
    public void setTargetPackage(String v)
    {
        targetPackage = v;
    }

    /**
     * Set up the initial context for generating the SQL from the XML schema.
     *
     * @return the context
     * @throws Exception
     */
    public Context initControlContext() throws Exception
    {
        XmlToAppData xmlParser;

        if (xmlFile == null && filesets.isEmpty())
        {
            throw new BuildException("You must specify an XML schema or "
                    + "fileset of XML schemas!");
        }

        try
        {
            if (xmlFile != null)
            {
                // Transform the XML database schema into
                // data model object.
                xmlParser = new XmlToAppData(getTargetDatabase(),
                        getTargetPackage());
                Database ad = xmlParser.parseFile(xmlFile);
                ad.setFileName(grokName(xmlFile));
                dataModels.add(ad);
            }
            else
            {
                // Deal with the filesets.
                for (int i = 0; i < filesets.size(); i++)
                {
                    FileSet fs = (FileSet) filesets.get(i);
                    DirectoryScanner ds = fs.getDirectoryScanner(getProject());
                    File srcDir = fs.getDir(getProject());

                    String[] dataModelFiles = ds.getIncludedFiles();

                    // Make a transaction for each file
                    for (int j = 0; j < dataModelFiles.length; j++)
                    {
                        File f = new File(srcDir, dataModelFiles[j]);
                        xmlParser = new XmlToAppData(getTargetDatabase(),
                                getTargetPackage());
                        Database ad = xmlParser.parseFile(f.toString());
                        ad.setFileName(grokName(f.toString()));
                        dataModels.add(ad);
                    }
                }
            }

            Iterator i = dataModels.iterator();
            databaseNames = new Hashtable();
            dataModelDbMap = new Hashtable();

            // Different datamodels may state the same database
            // names, we just want the unique names of databases.
            while (i.hasNext())
            {
                Database database = (Database) i.next();
                databaseNames.put(database.getName(), database.getName());
                dataModelDbMap.put(database.getFileName(), database.getName());
            }
        }
        catch (EngineException ee)
        {
            throw new BuildException(ee);
        }

        context = new VelocityContext();

        // Place our set of data models into the context along
        // with the names of the databases as a convenience for now.
        context.put("dataModels", dataModels);
        context.put("databaseNames", databaseNames);
        context.put("targetDatabase", targetDatabase);
        context.put("targetPackage", targetPackage);

        return context;
    }

    /**
     * Change type of "now" to java.util.Date
     *
     * @see org.apache.velocity.texen.ant.TexenTask#populateInitialContext(org.apache.velocity.context.Context)
     */
    protected void populateInitialContext(Context context) throws Exception
    {
        super.populateInitialContext(context);
        context.put("now", new Date());
    }

    /**
     * Gets a name to use for the application's data model.
     *
     * @param xmlFile The path to the XML file housing the data model.
     * @return The name to use for the <code>AppData</code>.
     */
    private String grokName(String xmlFile)
    {
        // This can't be set from the file name as it is an unreliable
        // method of naming the descriptor. Not everyone uses the same
        // method as I do in the TDK. jvz.

        String name = "data-model";
        int i = xmlFile.lastIndexOf(System.getProperty("file.separator"));
        if (i != -1)
        {
            // Creep forward to the start of the file name.
            i++;

            int j = xmlFile.lastIndexOf('.');
            if (i < j)
            {
                name = xmlFile.substring(i, j);
            }
            else
            {
                // Weirdo
                name = xmlFile.substring(i);
            }
        }
        return name;
    }

    /**
     * Override Texen's context properties to map the
     * torque.xxx properties (including defaults set by the
     * org/apache/torque/defaults.properties) to just xxx.
     *
     * <p>
     * Also, move xxx.yyy properties to xxxYyy as Velocity
     * doesn't like the xxx.yyy syntax.
     * </p>
     *
     * @param file the file to read the properties from
     */
    public void setContextProperties(String file)
    {
        super.setContextProperties(file);

        // Map the torque.xxx elements from the env to the contextProperties
        Hashtable env = super.getProject().getProperties();
        for (Iterator i = env.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();
            String key = (String) entry.getKey();
            if (key.startsWith("torque."))
            {
                String newKey = key.substring("torque.".length());
                int j = newKey.indexOf(".");
                while (j != -1)
                {
                    newKey =
                        newKey.substring(0, j)
                        +  StringUtils.capitalize(newKey.substring(j + 1));
                    j = newKey.indexOf(".");
                }

                contextProperties.setProperty(newKey, entry.getValue());
            }
        }
    }
}
