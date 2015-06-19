/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;
import org.kuali.kfs.sys.businessobject.datadictionary.FinancialSystemBusinessObjectEntry;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.krad.bo.PersistableBusinessObjectExtension;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.datadictionary.exception.CompletionException;
import org.kuali.rice.krad.datadictionary.parse.StringListConverter;
import org.kuali.rice.krad.datadictionary.parse.StringMapConverter;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.uif.util.ComponentBeanPostProcessor;
import org.kuali.rice.krad.uif.util.UifBeanFactoryPostProcessor;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.KualiDefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.ResourceUtils;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collection of named BusinessObjectEntry objects, each of which contains
 * information relating to the display, validation, and general maintenance of a
 * BusinessObject.
 *
 * THIS OVERRIDE OF THE RICE DATA DICTIONARY IS A TOTAL BAND-AID.
 * It allows us to pass in the Spring ApplicationContext to retrieve resources, which means we can use file globs
 * to pull in those resources.  Hopefully, as KFS starts pulling in Rice client functionality, the Rice DataDictionary
 * will be improved to pull multiple files in.
 */
public class DataDictionary  {

    protected KualiDefaultListableBeanFactory ddBeans = new KualiDefaultListableBeanFactory();
    protected XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ddBeans);

    // logger
    private static final Log LOG = LogFactory.getLog(DataDictionary.class);

    /**
     * The encapsulation of DataDictionary indices
     */
    protected DataDictionaryIndex ddIndex = new DataDictionaryIndex(ddBeans);

    // View indices
    protected UifDictionaryIndex uifIndex = new UifDictionaryIndex(ddBeans);

    /**
     * The DataDictionaryMapper
     * The default mapper simply consults the initialized indices
     * on workflow document type
     */
    protected DataDictionaryMapper ddMapper = new DataDictionaryIndexMapper();

    protected List<String> configFileLocations = new ArrayList<String>();

    protected static Pattern resourceJarUrlPattern = Pattern.compile("^.*?\\.jar!(.+)$");

    public List<String> getConfigFileLocations() {
        return this.configFileLocations;
    }

    public void setConfigFileLocations(List<String> configFileLocations) {
        this.configFileLocations = configFileLocations;
    }

    public void addConfigFileLocation( String location ) throws IOException {
        indexSource( location );
    }

    /**
     * ApplicationContext aware version of method
     */
    public void addConfigFileLocation( String location, ApplicationContext applicationContext ) throws IOException {
        indexSource( location, applicationContext );
    }

    /**
     * Sets the DataDictionaryMapper
     * @param mapper the datadictionary mapper
     */
    public void setDataDictionaryMapper(DataDictionaryMapper mapper) {
        this.ddMapper = mapper;
    }

    private void indexSource(String sourceName) throws IOException {
        if (sourceName == null) {
            throw new DataDictionaryException("Source Name given is null");
        }

        if (!sourceName.endsWith(".xml") ) {
            Resource resource = getFileResource(sourceName);
            if (resource.exists()) {
                indexSource(resource.getFile());
            } else {
                LOG.warn("Could not find " + sourceName);
                throw new DataDictionaryException("DD Resource " + sourceName + " not found");
            }
        } else {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("adding sourceName " + sourceName + " ");
            }
            Resource resource = getFileResource(sourceName);
            if (! resource.exists()) {
                throw new DataDictionaryException("DD Resource " + sourceName + " not found");
            }

            String indexName = sourceName.substring(sourceName.lastIndexOf("/") + 1, sourceName.indexOf(".xml"));
            configFileLocations.add( sourceName );
        }
    }

    /**
     * ApplicationContext aware version of method
     */
    private void indexSource(String sourceName, ApplicationContext applicationContext) throws IOException {
        if (sourceName == null) {
            throw new DataDictionaryException("Source Name given is null");
        }

        if (sourceName.endsWith(".xml")) {
            final Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(applicationContext).getResources(sourceName);
            for (Resource resource: resources) {
                if (resource.exists()) {
                    final String resourcePath = parseResourcePathFromUrl(resource);
                    if (!StringUtils.isBlank(resourcePath)) {
                        configFileLocations.add(resourcePath);
                    }
                } else {
                    LOG.warn("Could not find " + sourceName);
                    throw new DataDictionaryException("DD Resource " + sourceName + " not found");
                }
            }
        } else {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("adding sourceName " + sourceName + " ");
            }
            Resource resource = getFileResource(sourceName, applicationContext);
            if (! resource.exists()) {
                throw new DataDictionaryException("DD Resource " + sourceName + " not found");
            }

            String indexName = sourceName.substring(sourceName.lastIndexOf("/") + 1, sourceName.indexOf(".xml"));
            configFileLocations.add( sourceName );
        }
    }

    protected Resource getFileResource(String sourceName) {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
        return resourceLoader.getResource(sourceName);
    }

    /**
     * Parses the path name from a resource's description
     * @param resource a resource which hides a path from us
     * @return the path name if we could parse it out
     */
    protected String parseResourcePathFromUrl(Resource resource) throws IOException {
        final URL resourceUrl = resource.getURL();
        if (ResourceUtils.isJarURL(resourceUrl)) {
            final Matcher resourceUrlPathMatcher = resourceJarUrlPattern.matcher(resourceUrl.getPath());
            if (resourceUrlPathMatcher.matches() && !StringUtils.isBlank(resourceUrlPathMatcher.group(1))) {
                return "classpath:" + resourceUrlPathMatcher.group(1);
            }
        } else if (ResourceUtils.URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol()) && resource.exists()) {
            return "file:" + resourceUrl.getFile();
        }
        return null;
    }

    /**
     * ApplicationContext aware version of method
     */
    protected Resource getFileResource(String sourceName, ApplicationContext applicationContext) {
        return applicationContext.getResource(sourceName);
    }

    private void indexSource(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                indexSource(file);
            } else if (file.getName().endsWith(".xml") ) {
                configFileLocations.add( "file:" + file.getAbsolutePath());
            } else {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("Skipping non xml file " + file.getAbsolutePath() + " in DD load");
                }
            }
        }
    }

    public void parseDataDictionaryConfigurationFiles( boolean allowConcurrentValidation ) {
        // configure the bean factory, setup component decorator post processor
        // and allow Spring EL
        try {
            BeanPostProcessor idPostProcessor = ComponentBeanPostProcessor.class.newInstance();
            ddBeans.addBeanPostProcessor(idPostProcessor);
            ddBeans.setBeanExpressionResolver(new StandardBeanExpressionResolver());

            GenericConversionService conversionService = new GenericConversionService();
            conversionService.addConverter(new StringMapConverter());
            conversionService.addConverter(new StringListConverter());
            ddBeans.setConversionService(conversionService);
        } catch (Exception e1) {
            LOG.error("Cannot create component decorator post processor: " + e1.getMessage(), e1);
            throw new RuntimeException("Cannot create component decorator post processor: " + e1.getMessage(), e1);
        }

        LOG.info("Load data dictionary from Mongo");
        DictionaryIndex mongoDictionaryIndex = new MongoDictionaryIndex();
        mongoDictionaryIndex.index();

        if (mongoDictionaryIndex.getBusinessObjectEntries().isEmpty()) {
            // expand configuration locations into files
            LOG.info("Starting DD XML File Load");

            String[] configFileLocationsArray = new String[configFileLocations.size()];
            configFileLocationsArray = configFileLocations.toArray(configFileLocationsArray);
            // configFileLocations.clear(); // empty the list out so other items can be added
            try {
                xmlReader.loadBeanDefinitions(configFileLocationsArray);
            } catch (Exception e) {
                LOG.error("Error loading bean definitions", e);
                throw new DataDictionaryException("Error loading bean definitions: " + e.getLocalizedMessage());
            }
            LOG.info("Completed DD XML File Load");

            LOG.info("Loading business objects into Mongo");
            Map<String, FinancialSystemBusinessObjectEntry> businessObjectEntryMap = ddBeans.getBeansOfType(FinancialSystemBusinessObjectEntry.class);
            writeToMongo("localhost", "kfs_dd", "business_objects", businessObjectEntryMap);
            mongoDictionaryIndex.index();
        }



//        UifBeanFactoryPostProcessor factoryPostProcessor = new UifBeanFactoryPostProcessor();
//        factoryPostProcessor.postProcessBeanFactory(ddBeans);
//
//        // indexing
//        if (allowConcurrentValidation) {
//            Thread t = new Thread(ddIndex);
//            t.start();
//
//            Thread t2 = new Thread(uifIndex);
//            t2.start();
//        } else {
//            ddIndex.run();
//            uifIndex.run();
//        }
    }

    protected void writeToMongo(String hostUrl, String databaseName, String collectionName, Map<String, FinancialSystemBusinessObjectEntry> businessObjectEntryMap) {
        MongoDatabase database;
        try (MongoClient client = new MongoClient(new MongoClientURI("mongodb://" + hostUrl + ":27017"))) {
            database = client.getDatabase(databaseName);
            MongoCollection dds = database.getCollection(collectionName);
            Iterator<FinancialSystemBusinessObjectEntry> iterator = businessObjectEntryMap.values().iterator();
            ObjectMapper mapper = new ObjectMapper();
            while(iterator.hasNext()) {
                FinancialSystemBusinessObjectEntry businessObjectEntry = iterator.next();
                try {
                    Document doc = Document.parse(mapper.writeValueAsString(businessObjectEntry));
                    dds.insertOne(doc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public long parseDataDictionaryFromDatastore(boolean allowConcurrentValidation) {
        // configure the bean factory, setup component decorator post processor
        // and allow Spring EL
        try {
            BeanPostProcessor idPostProcessor = ComponentBeanPostProcessor.class.newInstance();
            ddBeans.addBeanPostProcessor(idPostProcessor);
            ddBeans.setBeanExpressionResolver(new StandardBeanExpressionResolver());

            GenericConversionService conversionService = new GenericConversionService();
            conversionService.addConverter(new StringMapConverter());
            conversionService.addConverter(new StringListConverter());
            ddBeans.setConversionService(conversionService);
        } catch (Exception e1) {
            LOG.error("Cannot create component decorator post processor: " + e1.getMessage(), e1);
            throw new RuntimeException("Cannot create component decorator post processor: " + e1.getMessage(), e1);
        }

        // expand configuration locations into files
        LOG.info("Starting DD Datastore Load");

        MongoClient client = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = client.getDatabase("kfs_dd");
        MongoCollection dds = database.getCollection("data_dictionary");
        FindIterable iterable = dds.find();
        MongoCursor cursor = iterable.iterator();
        while (cursor.hasNext()) {
            Document doc = (Document)cursor.next();
            String ddValue = (String)doc.get("xml");
            LOG.info(ddValue);
            ByteArrayResource resource = new ByteArrayResource(ddValue.getBytes());
            xmlReader.loadBeanDefinitions(resource);
        }

        LOG.info("Completed DD Datastore Load");

        UifBeanFactoryPostProcessor factoryPostProcessor = new UifBeanFactoryPostProcessor();
        factoryPostProcessor.postProcessBeanFactory(ddBeans);

        // indexing
        if (allowConcurrentValidation) {
            Thread t = new Thread(ddIndex);
            t.start();

            Thread t2 = new Thread(uifIndex);
            t2.start();
        } else {
            ddIndex.run();
            uifIndex.run();
        }
        return dds.count();
    }

    public void persistDataDictionaryToDatastore() {
        LOG.info("Starting DD datastore persist");
        MongoClient client = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = client.getDatabase("kfs_dd");
        MongoCollection dds = database.getCollection("data_dictionary");
        dds.drop();

        String[] configFileLocationsArray = new String[configFileLocations.size()];
        configFileLocationsArray = configFileLocations.toArray(configFileLocationsArray);
        configFileLocations.clear(); // empty the list out so other items can be added
        List<Document> dataDictionaries = new ArrayList();
        int count = 0;
        try {
            for (String config : configFileLocationsArray) {
                InputStream inputStream = getFileResource(config).getInputStream();

                String theString = IOUtils.toString(inputStream, "UTF-8");
                Document doc  = new Document();
                doc.put("key", count++);
                doc.put("xml", theString);
                dataDictionaries.add(doc);
            }

            dds = database.getCollection("data_dictionary");
            dds.insertMany(dataDictionaries);
        } catch (Exception e) {
            LOG.error("Error loading bean definitions", e);
            throw new DataDictionaryException("Error loading bean definitions: " + e.getLocalizedMessage());
        }
        LOG.info("Completed DD datastore persist");

    }

    static boolean validateEBOs = true;

    public void validateDD( boolean validateEbos ) {
        DataDictionary.validateEBOs = validateEbos;
        Map<String,DataObjectEntry> doBeans = ddBeans.getBeansOfType(DataObjectEntry.class);
        for ( DataObjectEntry entry : doBeans.values() ) {
            entry.completeValidation();
        }
        Map<String,DocumentEntry> docBeans = ddBeans.getBeansOfType(DocumentEntry.class);
        for ( DocumentEntry entry : docBeans.values() ) {
            entry.completeValidation();
        }
    }

    public void validateDD() {
        validateDD(true);
    }

    /**
     * @param className
     * @return BusinessObjectEntry for the named class, or null if none exists
     */
    @Deprecated
    public BusinessObjectEntry getBusinessObjectEntry(String className ) {
        return ddMapper.getBusinessObjectEntry(ddIndex, className);
    }

    /**
     * @param className
     * @return BusinessObjectEntry for the named class, or null if none exists
     */
    public DataObjectEntry getDataObjectEntry(String className ) {
        return ddMapper.getDataObjectEntry(ddIndex, className);
    }

    /**
     * This method gets the business object entry for a concrete class
     *
     * @param className
     * @return
     */
    public BusinessObjectEntry getBusinessObjectEntryForConcreteClass(String className){
        return ddMapper.getBusinessObjectEntryForConcreteClass(ddIndex, className);
    }

    /**
     * @return List of businessObject classnames
     */
    public List<String> getBusinessObjectClassNames() {
        return ddMapper.getBusinessObjectClassNames(ddIndex);
    }

    /**
     * @return Map of (classname, BusinessObjectEntry) pairs
     */
    public Map<String, BusinessObjectEntry> getBusinessObjectEntries() {
        return ddMapper.getBusinessObjectEntries(ddIndex);
    }

    /**
     * @param className
     * @return DataDictionaryEntryBase for the named class, or null if none
     *         exists
     */
    public DataDictionaryEntry getDictionaryObjectEntry(String className) {
        return ddMapper.getDictionaryObjectEntry(ddIndex, className);
    }

    /**
     * Returns the KNS document entry for the given lookup key.  The documentTypeDDKey is interpreted
     * successively in the following ways until a mapping is found (or none if found):
     * <ol>
     * <li>KEW/workflow document type</li>
     * <li>business object class name</li>
     * <li>maintainable class name</li>
     * </ol>
     * This mapping is compiled when DataDictionary files are parsed on startup (or demand).  Currently this
     * means the mapping is static, and one-to-one (one KNS document maps directly to one and only
     * one key).
     *
     * @param documentTypeDDKey the KEW/workflow document type name
     * @return the KNS DocumentEntry if it exists
     */
    public DocumentEntry getDocumentEntry(String documentTypeDDKey ) {
        return ddMapper.getDocumentEntry(ddIndex, documentTypeDDKey);
    }

    /**
     * Note: only MaintenanceDocuments are indexed by businessObject Class
     *
     * This is a special case that is referenced in one location. Do we need
     * another map for this stuff??
     *
     * @param businessObjectClass
     * @return DocumentEntry associated with the given Class, or null if there
     *         is none
     */
    public MaintenanceDocumentEntry getMaintenanceDocumentEntryForBusinessObjectClass(Class<?> businessObjectClass) {
        return ddMapper.getMaintenanceDocumentEntryForBusinessObjectClass(ddIndex, businessObjectClass);
    }

    public Map<String, DocumentEntry> getDocumentEntries() {
        return ddMapper.getDocumentEntries(ddIndex);
    }

    /**
     * Returns the View entry identified by the given id
     *
     * @param viewId - unique id for view
     * @return View instance associated with the id
     */
    public View getViewById(String viewId) {
        return ddMapper.getViewById(uifIndex, viewId);
    }

    /**
     * Returns View instance identified by the view type name and index
     *
     * @param viewTypeName
     *            - type name for the view
     * @param indexKey
     *            - Map of index key parameters, these are the parameters the
     *            indexer used to index the view initially and needs to identify
     *            an unique view instance
     * @return View instance that matches the given index
     */
    public View getViewByTypeIndex(ViewType viewTypeName, Map<String, String> indexKey) {
        return ddMapper.getViewByTypeIndex(uifIndex, viewTypeName, indexKey);
    }

    /**
     * Indicates whether a <code>View</code> exists for the given view type and index information
     *
     * @param viewTypeName - type name for the view
     * @param indexKey - Map of index key parameters, these are the parameters the
     * indexer used to index the view initially and needs to identify
     * an unique view instance
     * @return boolean true if view exists, false if not
     */
    public boolean viewByTypeExist(ViewType viewTypeName, Map<String, String> indexKey) {
        return ddMapper.viewByTypeExist(uifIndex, viewTypeName, indexKey);
    }

    /**
     * Gets all <code>View</code> prototypes configured for the given view type
     * name
     *
     * @param viewTypeName
     *            - view type name to retrieve
     * @return List<View> view prototypes with the given type name, or empty
     *         list
     */
    public List<View> getViewsForType(ViewType viewTypeName) {
        return ddMapper.getViewsForType(uifIndex, viewTypeName);
    }

    /**
     * Returns an object from the dictionary by its spring bean name
     *
     * @param beanName - id or name for the bean definition
     * @return Object object instance created or the singleton being maintained
     */
    public Object getDictionaryObject(String beanName) {
        return ddBeans.getBean(beanName);
    }

    /**
     * Indicates whether the data dictionary contains a bean with the given id
     *
     * @param id - id of the bean to check for
     * @return boolean true if dictionary contains bean, false otherwise
     */
    public boolean containsDictionaryObject(String id) {
        return ddBeans.containsBean(id);
    }

    /**
     * Retrieves the configured property values for the view bean definition associated with the given id
     *
     * <p>
     * Since constructing the View object can be expensive, when metadata only is needed this method can be used
     * to retrieve the configured property values. Note this looks at the merged bean definition
     * </p>
     *
     * @param viewId - id for the view to retrieve
     * @return PropertyValues configured on the view bean definition, or null if view is not found
     */
    public PropertyValues getViewPropertiesById(String viewId) {
        return ddMapper.getViewPropertiesById(uifIndex, viewId);
    }

    /**
     * Retrieves the configured property values for the view bean definition associated with the given type and
     * index
     *
     * <p>
     * Since constructing the View object can be expensive, when metadata only is needed this method can be used
     * to retrieve the configured property values. Note this looks at the merged bean definition
     * </p>
     *
     * @param viewTypeName - type name for the view
     * @param indexKey - Map of index key parameters, these are the parameters the indexer used to index
     * the view initially and needs to identify an unique view instance
     * @return PropertyValues configured on the view bean definition, or null if view is not found
     */
    public PropertyValues getViewPropertiesByType(ViewType viewTypeName, Map<String, String> indexKey) {
        return ddMapper.getViewPropertiesByType(uifIndex, viewTypeName, indexKey);
    }

    /**
     * @param targetClass
     * @param propertyName
     * @return true if the given propertyName names a property of the given class
     * @throws CompletionException if there is a problem accessing the named property on the given class
     */
    public static boolean isPropertyOf(Class targetClass, String propertyName) {
        if (targetClass == null) {
            throw new IllegalArgumentException("invalid (null) targetClass");
        }
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("invalid (blank) propertyName");
        }

        PropertyDescriptor propertyDescriptor = buildReadDescriptor(targetClass, propertyName);

        boolean isPropertyOf = (propertyDescriptor != null);
        return isPropertyOf;
    }

    /**
     * @param targetClass
     * @param propertyName
     * @return true if the given propertyName names a Collection property of the given class
     * @throws CompletionException if there is a problem accessing the named property on the given class
     */
    public static boolean isCollectionPropertyOf(Class targetClass, String propertyName) {
        boolean isCollectionPropertyOf = false;

        PropertyDescriptor propertyDescriptor = buildReadDescriptor(targetClass, propertyName);
        if (propertyDescriptor != null) {
            Class clazz = propertyDescriptor.getPropertyType();

            if ((clazz != null) && Collection.class.isAssignableFrom(clazz)) {
                isCollectionPropertyOf = true;
            }
        }

        return isCollectionPropertyOf;
    }

    public static PersistenceStructureService persistenceStructureService;

    /**
     * @return the persistenceStructureService
     */
    public static PersistenceStructureService getPersistenceStructureService() {
        if ( persistenceStructureService == null ) {
            persistenceStructureService = KRADServiceLocator.getPersistenceStructureService();
        }
        return persistenceStructureService;
    }

    /**
     * This method determines the Class of the attributeName passed in. Null will be returned if the member is not available, or if
     * a reflection exception is thrown.
     *
     * @param boClass - Class that the attributeName property exists in.
     * @param attributeName - Name of the attribute you want a class for.
     * @return The Class of the attributeName, if the attribute exists on the rootClass. Null otherwise.
     */
    public static Class getAttributeClass(Class boClass, String attributeName) {

        // fail loudly if the attributeName isnt a member of rootClass
        if (!isPropertyOf(boClass, attributeName)) {
            throw new AttributeValidationException("unable to find attribute '" + attributeName + "' in rootClass '" + boClass.getName() + "'");
        }

        //Implementing Externalizable Business Object Services...
        //The boClass can be an interface, hence handling this separately, 
        //since the original method was throwing exception if the class could not be instantiated.
        if(boClass.isInterface())
            return getAttributeClassWhenBOIsInterface(boClass, attributeName);
        else
            return getAttributeClassWhenBOIsClass(boClass, attributeName);

    }

    /**
     *
     * This method gets the property type of the given attributeName when the bo class is a concrete class
     *
     * @param boClass
     * @param attributeName
     * @return
     */
    private static Class getAttributeClassWhenBOIsClass(Class boClass, String attributeName){
        Object boInstance;
        try {
            boInstance = boClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate Data Object: " + boClass, e);
        }

        // attempt to retrieve the class of the property
        try {
            return ObjectUtils.getPropertyType(boInstance, attributeName, getPersistenceStructureService());
        } catch (Exception e) {
            throw new RuntimeException("Unable to determine property type for: " + boClass.getName() + "." + attributeName, e);
        }
    }

    /**
     *
     * This method gets the property type of the given attributeName when the bo class is an interface
     * This method will also work if the bo class is not an interface, 
     * but that case requires special handling, hence a separate method getAttributeClassWhenBOIsClass 
     *
     * @param boClass
     * @param attributeName
     * @return
     */
    private static Class getAttributeClassWhenBOIsInterface(Class boClass, String attributeName){
        if (boClass == null) {
            throw new IllegalArgumentException("invalid (null) boClass");
        }
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }

        PropertyDescriptor propertyDescriptor = null;

        String[] intermediateProperties = attributeName.split("\\.");
        int lastLevel = intermediateProperties.length - 1;
        Class currentClass = boClass;

        for (int i = 0; i <= lastLevel; ++i) {

            String currentPropertyName = intermediateProperties[i];
            propertyDescriptor = buildSimpleReadDescriptor(currentClass, currentPropertyName);

            if (propertyDescriptor != null) {

                Class propertyType = propertyDescriptor.getPropertyType();
                if ( propertyType.equals( PersistableBusinessObjectExtension.class ) ) {
                    propertyType = getPersistenceStructureService().getBusinessObjectAttributeClass( currentClass, currentPropertyName );
                }
                if (Collection.class.isAssignableFrom(propertyType)) {
                    // TODO: determine property type using generics type definition
                    throw new AttributeValidationException("Can't determine the Class of Collection elements because when the business object is an (possibly ExternalizableBusinessObject) interface.");
                }
                else {
                    currentClass = propertyType;
                }
            }
            else {
                throw new AttributeValidationException("Can't find getter method of " + boClass.getName() + " for property " + attributeName);
            }
        }
        return currentClass;
    }

    /**
     * This method determines the Class of the elements in the collectionName passed in.
     *
     * @param boClass Class that the collectionName collection exists in.
     * @param collectionName the name of the collection you want the element class for
     * @return
     */
    public static Class getCollectionElementClass(Class boClass, String collectionName) {
        if (boClass == null) {
            throw new IllegalArgumentException("invalid (null) boClass");
        }
        if (StringUtils.isBlank(collectionName)) {
            throw new IllegalArgumentException("invalid (blank) collectionName");
        }

        PropertyDescriptor propertyDescriptor = null;

        String[] intermediateProperties = collectionName.split("\\.");
        Class currentClass = boClass;

        for (int i = 0; i <intermediateProperties.length; ++i) {

            String currentPropertyName = intermediateProperties[i];
            propertyDescriptor = buildSimpleReadDescriptor(currentClass, currentPropertyName);


            if (propertyDescriptor != null) {

                Class type = propertyDescriptor.getPropertyType();
                if (Collection.class.isAssignableFrom(type)) {

                    if (getPersistenceStructureService().isPersistable(currentClass)) {

                        Map<String, Class> collectionClasses = new HashMap<String, Class>();
                        collectionClasses = getPersistenceStructureService().listCollectionObjectTypes(currentClass);
                        currentClass = collectionClasses.get(currentPropertyName);

                    }
                    else {
                        throw new RuntimeException("Can't determine the Class of Collection elements because persistenceStructureService.isPersistable(" + currentClass.getName() + ") returns false.");
                    }

                }
                else {

                    currentClass = propertyDescriptor.getPropertyType();

                }
            }
        }

        return currentClass;
    }

    static private Map<String, Map<String, PropertyDescriptor>> cache = new TreeMap<String, Map<String, PropertyDescriptor>>();

    /**
     * @param propertyClass
     * @param propertyName
     * @return PropertyDescriptor for the getter for the named property of the given class, if one exists.
     */
    public static PropertyDescriptor buildReadDescriptor(Class propertyClass, String propertyName) {
        if (propertyClass == null) {
            throw new IllegalArgumentException("invalid (null) propertyClass");
        }
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("invalid (blank) propertyName");
        }

        PropertyDescriptor propertyDescriptor = null;

        String[] intermediateProperties = propertyName.split("\\.");
        int lastLevel = intermediateProperties.length - 1;
        Class currentClass = propertyClass;

        for (int i = 0; i <= lastLevel; ++i) {

            String currentPropertyName = intermediateProperties[i];
            propertyDescriptor = buildSimpleReadDescriptor(currentClass, currentPropertyName);

            if (i < lastLevel) {

                if (propertyDescriptor != null) {

                    Class propertyType = propertyDescriptor.getPropertyType();
                    if ( propertyType.equals( PersistableBusinessObjectExtension.class ) ) {
                        propertyType = getPersistenceStructureService().getBusinessObjectAttributeClass( currentClass, currentPropertyName );
                    }
                    if (Collection.class.isAssignableFrom(propertyType)) {

                        if (getPersistenceStructureService().isPersistable(currentClass)) {

                            Map<String, Class> collectionClasses = new HashMap<String, Class>();
                            collectionClasses = getPersistenceStructureService().listCollectionObjectTypes(currentClass);
                            currentClass = collectionClasses.get(currentPropertyName);

                        }
                        else {

                            throw new RuntimeException("Can't determine the Class of Collection elements because persistenceStructureService.isPersistable(" + currentClass.getName() + ") returns false.");

                        }

                    }
                    else {

                        currentClass = propertyType;

                    }

                }

            }

        }

        return propertyDescriptor;
    }

    /**
     * @param propertyClass
     * @param propertyName
     * @return PropertyDescriptor for the getter for the named property of the given class, if one exists.
     */
    public static PropertyDescriptor buildSimpleReadDescriptor(Class propertyClass, String propertyName) {
        if (propertyClass == null) {
            throw new IllegalArgumentException("invalid (null) propertyClass");
        }
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("invalid (blank) propertyName");
        }

        PropertyDescriptor p = null;

        // check to see if we've cached this descriptor already. if yes, return true.
        String propertyClassName = propertyClass.getName();
        Map<String, PropertyDescriptor> m = cache.get(propertyClassName);
        if (null != m) {
            p = m.get(propertyName);
            if (null != p) {
                return p;
            }
        }

        // Use PropertyUtils.getPropertyDescriptors instead of manually constructing PropertyDescriptor because of
        // issues with introspection and generic/co-variant return types
        // See https://issues.apache.org/jira/browse/BEANUTILS-340 for more details

        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(propertyClass);
        if (ArrayUtils.isNotEmpty(descriptors)) {
            for (PropertyDescriptor descriptor : descriptors) {
                if (descriptor.getName().equals(propertyName)) {
                    p = descriptor;
                }
            }
        }

        // cache the property descriptor if we found it.
        if (p != null) {
            if (m == null) {
                m = new TreeMap<String, PropertyDescriptor>();
                cache.put(propertyClassName, m);
            }
            m.put(propertyName, p);
        }

        return p;
    }

    public Set<InactivationBlockingMetadata> getAllInactivationBlockingMetadatas(Class blockedClass) {
        return ddMapper.getAllInactivationBlockingMetadatas(ddIndex, blockedClass);
    }

    /**
     * This method gathers beans of type BeanOverride and invokes each one's performOverride() method.
     */
    // KULRICE-4513
    public void performBeanOverrides()
    {
        Collection<BeanOverride> beanOverrides = ddBeans.getBeansOfType(BeanOverride.class).values();

        if (beanOverrides.isEmpty()){
            LOG.info("DataDictionary.performOverrides(): No beans to override");
        }
        for (BeanOverride beanOverride : beanOverrides) {

            Object bean = ddBeans.getBean(beanOverride.getBeanName());
            beanOverride.performOverride(bean);
            LOG.info("DataDictionary.performOverrides(): Performing override on bean: " + bean.toString());
        }
    }
}
