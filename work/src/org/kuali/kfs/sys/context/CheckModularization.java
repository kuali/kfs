/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.sys.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.collections.bidimap.TreeBidiMap;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.util.LogErrorHandler;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.kns.datadictionary.KNSDocumentEntry;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.InactivationBlockingDefinition;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.springframework.core.io.DefaultResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CheckModularization {

    /*
     * Since endowment is currently turned off in KFS foundation base code, it is not included in the
     * optional namespace codes below. If the endowment module is turned back on, it should be added
     * to the list below with
     *
     *         OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-ENDOW", "endow");
     *
     */
    private static final Map<String, String> OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX = new HashMap<String, String>();
    // NOTE: Access Security (KFS-SEC) is considered a "core" module, but one which other modules shouldn't depend
    // upon, so it's included in this group to verify that no other modules depend upon it.
    static {
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-AR", "ar");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-BC", "bc");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-CAB", "cab");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-CAM", "cam");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-CG", "cg");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-EC", "ec");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-LD", "ld");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-PURAP", "purap");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-SEC", "sec");
    }

    private static final Map<String, String> OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES =
            new TreeBidiMap(OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX).inverseBidiMap();

    private static final Map<String, String> SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX = new HashMap<String, String>();
    static {
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-COA", "coa");
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-FP", "fp");
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-GL", "gl");
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-PDP", "pdp");
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-SYS", "sys");
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-VND", "vnd");
    }

    private static Map<String,List<String>> PACKAGE_PREFIXES_BY_MODULE = new HashMap<String, List<String>>();
    private static Map<String,List<String>> OJB_FILES_BY_MODULE = new HashMap<String, List<String>>();
    private static Map<String,List<String>> DWR_FILES_BY_MODULE = new HashMap<String, List<String>>();

    private static String MODULE_SPRING_PATH_PATTERN = "org/kuali/kfs/module/{0}/spring-{0}.xml";
    private static String KFS_SEC_MODULE_SPRING_PATH_PATTERN = "org/kuali/kfs/{0}/spring-{0}.xml";

    /*
     * open up classpath:configuration.properties - get locations of spring files?
     * alter the spring.source.files property and re-save
     * hold original version and restore?
     *
     * use location of config.properties as the class root for scanning source files?
     * How do you test .class files for symbols?
     */
    static String coreSpringFiles;
    static String coreSpringTestFiles;
    static File configPropertiesFile;
    public static void main(String[] args) {
        CheckModularization mt = new CheckModularization();
        try {
            Properties configProps = new Properties();
            URL propLocation = CheckModularization.class.getClassLoader().getResource("configuration.properties" );
            System.out.println( "URL: " + propLocation );
            System.out.println( "Path: " + propLocation.getPath() );
            configPropertiesFile = new File( propLocation.getPath() );
            configProps.load( CheckModularization.class.getClassLoader().getResourceAsStream("configuration.properties") );
            coreSpringFiles = configProps.getProperty("core.spring.source.files") + configProps.getProperty("integration.spring.files") + ",classpath:org/kuali/kfs/sys/context/spring-kfs-checkmodularization-overrides.xml";
            coreSpringTestFiles = configProps.getProperty("core.spring.test.files");

            try {
                SpringContextForBatchRunner.initializeKfs();
                KualiModuleService kualiModuleService = SpringContext.getBean(KualiModuleService.class);

                for ( ModuleService module : kualiModuleService.getInstalledModuleServices() ) {
                    PACKAGE_PREFIXES_BY_MODULE.put(module.getModuleConfiguration().getNamespaceCode(), module.getModuleConfiguration().getPackagePrefixes() );
                    OJB_FILES_BY_MODULE.put(module.getModuleConfiguration().getNamespaceCode(), module.getModuleConfiguration().getDatabaseRepositoryFilePaths() );
                    DWR_FILES_BY_MODULE.put(module.getModuleConfiguration().getNamespaceCode(), module.getModuleConfiguration().getScriptConfigurationFilePaths() );
                }

            } catch ( Exception ex ) {
                ex.printStackTrace();
            } finally {
                stopSpringContext();
            }

            // bring up Spring once to get all the configuration information, store by namespace code
            // list of core namespaces, all others must be independent

            // test class references
            boolean testsPassed = true;
            System.out.println( "**************************************************");
            System.out.println( "Testing Spring Startup");
            System.out.println( "**************************************************");
            if ( !mt.testSpring() ) {
                System.out.println( "FAILED" );
                testsPassed = false;
            } else {
                System.out.println( "SUCCEEDED" );
            }
            System.out.println( "**************************************************");
            System.out.println( "Testing OJB References");
            System.out.println( "**************************************************");
            if ( !mt.testOjb() ) {
                System.out.println( "FAILED" );
                testsPassed = false;
            } else {
                System.out.println( "SUCCEEDED" );
            }
            System.out.println( "**************************************************");
            System.out.println( "Testing DWR References");
            System.out.println( "**************************************************");
            if ( !mt.testDwr() ) {
                System.out.println( "FAILED" );
                testsPassed = false;
            } else {
                System.out.println( "SUCCEEDED" );
            }
            System.out.println( "**************************************************");
            System.out.println( "Testing DD Class References");
            System.out.println( "**************************************************");
            if ( !mt.testDd() ) {
                System.out.println( "FAILED" );
                testsPassed = false;
            } else {
                System.out.println( "SUCCEEDED" );
            }

            if ( !testsPassed ) {
                System.exit(1);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }


    protected String buildOptionalModuleSpringFileList( ModuleGroup moduleGroup ) {
        StringBuffer sb = new StringBuffer();
        sb.append( MessageFormat.format(getModuleSpringPathPattern(moduleGroup.namespaceCode), OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.get( moduleGroup.namespaceCode ) ) );
        for ( String depMod : moduleGroup.optionalModuleDependencyNamespaceCodes ) {
            sb.append( ',' );
            sb.append( MessageFormat.format(getModuleSpringPathPattern(depMod), OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.get( depMod ) ) );
        }
        return sb.toString();
    }

    /**
     * Since we're treating Access Security like an optional module for modularization validation purposes
     * but it follows the core package naming convention, we have to account for that, which this method does.
     *
     * @param moduleNamespaceCode
     * @return String the correct module string path pattern for this module
     */
    private String getModuleSpringPathPattern(String moduleNamespaceCode) {
        if (moduleNamespaceCode.equals(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY)) {
            return KFS_SEC_MODULE_SPRING_PATH_PATTERN;
        } else {
            return MODULE_SPRING_PATH_PATTERN;
        }
    }

    StringBuffer dwrErrorMessage = new StringBuffer("The following optional modules have interdependencies in DWR configuration:");
    boolean dwrTestSucceeded = true;
    StringBuffer ddErrorMessage = new StringBuffer("The following optional modules have interdependencies in DD class references:");
    boolean ddTestSucceeded = true;

    public boolean testSpring() throws Exception {
        boolean testSucceeded = true;
        StringBuffer errorMessage = new StringBuffer();
        // test the core modules alone
        System.out.println( "\n\n------>Testing for core modules:");
        System.out.println( "------>Using Base Configuration:   " + coreSpringFiles );
        testSucceeded &= testOptionalModuleSpringConfiguration(new ModuleGroup(KFSConstants.ParameterNamespaces.KFS), coreSpringFiles, errorMessage);
        if ( !testSucceeded ) {
            errorMessage.insert( 0, "The Core modules have dependencies on the optional modules:\n" );
        }

        errorMessage.append( "The following optional modules have interdependencies in Spring configuration:\n");
        List<ModuleGroup> optionalModuleGroups = retrieveOptionalModuleGroups();
        for (ModuleGroup optionalModuleGroup : optionalModuleGroups) {
/*
 * For ease in debugging, uncomment the following, changing namespaceCode as necessary, to limit the testing
 * to a single module
 */
//            if ( !optionalModuleGroup.namespaceCode.equals( "KFS-EC" ) ) {
//                continue;
//            }
            System.out.println( "\n\n------>Testing for optional module group: " + optionalModuleGroup );
            System.out.println( "------>Using Base Configuration:   " + coreSpringFiles );
            String moduleConfigFiles = buildOptionalModuleSpringFileList(optionalModuleGroup);
            System.out.println( "------>Module configuration files: " + moduleConfigFiles );
            testSucceeded &= testOptionalModuleSpringConfiguration(optionalModuleGroup, coreSpringFiles+","+moduleConfigFiles, errorMessage);
        }
        if (!testSucceeded) {
            System.out.print(errorMessage.append("\n\n").toString());
        }
        return testSucceeded;
    }

    protected boolean testOptionalModuleSpringConfiguration(ModuleGroup optionalModuleGroup, String springConfigFiles, StringBuffer errorMessage) {
        try {
            // update the configuration.properties file
            PropertyLoadingFactoryBean.clear();
            Properties configProps = new Properties();
            configProps.load( new FileInputStream( configPropertiesFile ) );
            configProps.setProperty( "spring.source.files", springConfigFiles );
            configProps.setProperty( "spring.test.files", coreSpringTestFiles );
            configProps.setProperty( "validate.data.dictionary.ebo.references", "false" );
            configProps.store( new FileOutputStream( configPropertiesFile ), "Testing Module: " + optionalModuleGroup.namespaceCode );
            configProps.load( new FileInputStream( configPropertiesFile ) );

            // clear out  existing services before new services get added or else they just keep accumulating and that's not what we want
            KualiModuleService kualiModuleService = SpringContext.getBean(KualiModuleService.class);
            kualiModuleService.setInstalledModuleServices(new ArrayList<ModuleService>());
            SpringContextForBatchRunner.initializeKfs();

            dwrTestSucceeded &= testDwrModuleConfiguration(optionalModuleGroup, dwrErrorMessage);
            ddTestSucceeded &= testDdModuleConfiguration(optionalModuleGroup, ddErrorMessage);
            return true;
        } catch (Exception e) {
            errorMessage.append("\n\n").append(optionalModuleGroup.namespaceCode).append("\n\t").append(e.getMessage());
            dwrErrorMessage.append( "\n\n" + optionalModuleGroup.namespaceCode + " : Unable to test due to Spring test failure." );
            ddErrorMessage.append( "\n\n" + optionalModuleGroup.namespaceCode + " : Unable to test due to Spring test failure." );
            ddTestSucceeded &= false;
            dwrTestSucceeded &= false;
            e.printStackTrace();
            return false;
        }
        finally {
            stopSpringContext();
        }
    }

    public boolean testOjb() throws Exception {
        boolean testSucceeded = true;
        StringBuffer errorMessage = new StringBuffer("The following optional modules have interdependencies in OJB configuration:");
        List<ModuleGroup> allModuleGroups = retrieveModuleGroups();
        for (ModuleGroup moduleGroup : allModuleGroups) {
            testSucceeded = testSucceeded & testOptionalModuleOjbConfiguration(moduleGroup, errorMessage);
        }
        if (!testSucceeded) {
            System.out.print(errorMessage.append("\n\n").toString());
        }
        return testSucceeded;
    }

    protected boolean testOptionalModuleOjbConfiguration(ModuleGroup moduleGroup, StringBuffer errorMessage) throws FileNotFoundException {
        boolean testSucceeded = true;
        for (String referencedNamespaceCode : OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.keySet()) {
            if (!(moduleGroup.namespaceCode.equals(referencedNamespaceCode) || moduleGroup.optionalModuleDependencyNamespaceCodes.contains(referencedNamespaceCode))) {
                if ( OJB_FILES_BY_MODULE.get(moduleGroup.namespaceCode) == null || OJB_FILES_BY_MODULE.get(moduleGroup.namespaceCode).isEmpty() ) {
                    continue;
                }
                String firstDatabaseRepositoryFilePath = OJB_FILES_BY_MODULE.get(moduleGroup.namespaceCode).iterator().next();
                // the first database repository file path is typically the file that comes shipped with KFS.  If institutions override it, this unit test will not test them
                Scanner scanner = new Scanner(new File("work/src/" + firstDatabaseRepositoryFilePath));
                int count = 0;
                while (scanner.hasNext()) {
                    String token = scanner.next();
                    if (PACKAGE_PREFIXES_BY_MODULE.get( referencedNamespaceCode ) != null) {
                        String firstPackagePrefix = PACKAGE_PREFIXES_BY_MODULE.get( referencedNamespaceCode ).iterator().next();
                        // A module may be responsible for many packages, but the first one should be the KFS built-in package that is *not* the module's integration package
                        if (token.contains(firstPackagePrefix)) {
                            count++;
                        }
                    }
                }
                if (count > 0) {
                    if (testSucceeded) {
                        testSucceeded = false;
                        errorMessage.append("\n").append(moduleGroup.namespaceCode).append(": ");
                    }
                    else {
                        errorMessage.append(", ");
                    }
                    errorMessage.append(count).append(" references to ").append(referencedNamespaceCode);
                }
            }
        }
        return testSucceeded;
    }

    protected boolean testDwr() throws Exception {
        if (!dwrTestSucceeded) {
            System.out.print(dwrErrorMessage.append("\n\n").toString());
        }
        return dwrTestSucceeded;
    }

    protected boolean testDwrModuleConfiguration(ModuleGroup moduleGroup, StringBuffer errorMessage) throws Exception {
        List<String> dwrFiles = DWR_FILES_BY_MODULE.get(moduleGroup.namespaceCode);
        boolean testSucceeded = true;
        if (dwrFiles != null && dwrFiles.size() > 0) {
            // the DWR file delivered with KFS (i.e. the base) should be the first element of the list
            String baseDwrFileName = dwrFiles.get(0);
            Document dwrDocument = generateDwrConfigDocument(baseDwrFileName);
            testSucceeded = testDwrModuleConfiguration(baseDwrFileName, dwrDocument, moduleGroup, errorMessage);
        }
        return testSucceeded;
    }

    protected boolean testDwrModuleConfiguration(String dwrFileName, Document dwrDocument, ModuleGroup moduleGroup, StringBuffer errorMessage) throws Exception {
       boolean beanClassNamesOK = testDwrBeanClassNames(dwrFileName, dwrDocument, moduleGroup, errorMessage);
       boolean springServicesOK = testDwrSpringServices(dwrFileName, dwrDocument, moduleGroup, errorMessage);
       return beanClassNamesOK && springServicesOK;
    }

    protected boolean testDwrBeanClassNames(String dwrFileName, Document dwrDocument, ModuleGroup moduleGroup, StringBuffer errorMessage) {
        boolean testSucceeded = true;
        List<String> dwrBeanClassNames = retrieveDwrBeanClassNames(dwrDocument);
        for (String referencedNamespaceCode : OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.keySet()) {
            if (!(referencedNamespaceCode.equals(moduleGroup.namespaceCode) || moduleGroup.optionalModuleDependencyNamespaceCodes.contains(referencedNamespaceCode))) {
                List<String> packagePrefixes = PACKAGE_PREFIXES_BY_MODULE.get(referencedNamespaceCode);
                if (packagePrefixes != null) {
                    Iterator<String> it = packagePrefixes.iterator();
                    if (it.hasNext()) {
                        String firstPackagePrefix = it.next();
                        // A module may be responsible for many packages, but the first one should be the KFS built-in package that is *not* the module's integration package
                        if (!firstPackagePrefix.endsWith(".")) {
                            firstPackagePrefix = firstPackagePrefix + ".";
                        }
                        int count = 0;
                        for (String className : dwrBeanClassNames) {
                            if (className.contains(firstPackagePrefix)) {
                                count++;
                            }
                        }
                        if (count > 0) {
                            testSucceeded = false;
                            errorMessage.append("\n\n").append(dwrFileName).append(" (in module ").append(moduleGroup.namespaceCode).append(") has ").append(count).append(" references to business objects from ").append(referencedNamespaceCode);
                        }
                    }
                } else {
                    System.err.println("no package prefixes " + referencedNamespaceCode);
                }
            }
        }
        return testSucceeded;
    }

    protected boolean testDwrSpringServices(String dwrFileName, Document dwrDocument, ModuleGroup moduleGroup, StringBuffer errorMessage) {
        boolean testSucceeded = true;

        try {
            List<String> serviceNames = retrieveDwrServiceNames(dwrDocument);
            for (String serviceName : serviceNames) {
                try {
                    SpringContext.getBean(serviceName);
                } catch ( Exception ex ) {
                    testSucceeded = false;
                    errorMessage.append("\n")
                            .append(dwrFileName)
                            .append(" (in module ")
                            .append(moduleGroup.namespaceCode)
                            .append(") has references to spring bean \"")
                            .append(serviceName).append("\" that is not defined in the available spring files");
                }
            }
        }
        catch (Exception e) {
            errorMessage.append("\n").append(moduleGroup.namespaceCode).append("\n\t").append(e.getMessage());
            e.printStackTrace();
            return testSucceeded = false;
        }

        return testSucceeded;
    }


    protected Document generateDwrConfigDocument(String fileName) throws Exception {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
        InputStream in = resourceLoader.getResource(fileName).getInputStream();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://xml.org/sax/features/namespaces", false);
        dbf.setFeature("http://xml.org/sax/features/validation", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);


        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setErrorHandler(new LogErrorHandler());

        Document doc = db.parse(in);
        return doc;
    }

    protected List<String> retrieveDwrServiceNames(Document dwrDocument) {
        List<String> serviceNames = new ArrayList<String>();
        // service names are in "create" elements
        Element root = dwrDocument.getDocumentElement();
        NodeList allows = root.getElementsByTagName("allow");
        for (int i = 0; i < allows.getLength(); i++) {
            Element allowElement = (Element) allows.item(i);
            NodeList creates = allowElement.getElementsByTagName("create");
            for (int j = 0; j < creates.getLength(); j++) {
                Element createElement = (Element) creates.item(j);
                if ("spring".equals(createElement.getAttribute("creator"))) {
                    NodeList params = createElement.getElementsByTagName("param");
                    for (int k = 0; k < params.getLength(); k++) {
                        Element paramElement = (Element) params.item(k);
                        if ("beanName".equals(paramElement.getAttribute("name"))) {
                            serviceNames.add(paramElement.getAttribute("value"));
                        }
                    }
                }

            }
        }
        return serviceNames;
    }

    protected List<String> retrieveDwrBeanClassNames(Document dwrDocument) {
        List<String> classNames = new ArrayList<String>();
        // class names are in "convert" elements
        Element root = dwrDocument.getDocumentElement();
        NodeList allows = root.getElementsByTagName("allow");
        for (int i = 0; i < allows.getLength(); i++) {
            Element allowElement = (Element) allows.item(i);
            NodeList converts = allowElement.getElementsByTagName("convert");
            for (int j = 0; j < converts.getLength(); j++) {
                Element convertElement = (Element) converts.item(j);
                if ("bean".equals(convertElement.getAttribute("converter"))) {
                    classNames.add(convertElement.getAttribute("match"));
                }
            }
        }
        return classNames;
    }

    protected boolean testDd() throws Exception {
        if (!ddTestSucceeded) {
            System.out.print(ddErrorMessage.append("\n\n").toString());
        }
        return ddTestSucceeded;
    }

    protected boolean testDdModuleConfiguration( ModuleGroup moduleGroup, StringBuffer errorMessage ) {
        boolean testPassed = true;

        List<String> disallowedPackagesForModule = new ArrayList<String>();
        for ( String otherNamespace : PACKAGE_PREFIXES_BY_MODULE.keySet() ) {
            // if an optional module
            if ( OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.containsKey( otherNamespace ) ) {
                // and not the current module or a dependency
                if ( !otherNamespace.equals( moduleGroup.namespaceCode ) && !moduleGroup.optionalModuleDependencyNamespaceCodes.contains(otherNamespace)) {
                    // add to disallowed list
                    disallowedPackagesForModule.addAll( PACKAGE_PREFIXES_BY_MODULE.get(otherNamespace) );
                }
            }
        }
        System.out.println( "---Processing DD for Module: " + moduleGroup.namespaceCode );
        System.out.println( "---Disallowed packages: " + disallowedPackagesForModule );
        DataDictionary dd = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();
        Collection<org.kuali.rice.krad.datadictionary.BusinessObjectEntry> bos = dd.getBusinessObjectEntries().values();
        for ( org.kuali.rice.krad.datadictionary.BusinessObjectEntry bo : bos ) {
            // only check bos for the current module (or all modules if checking the core)
            if ( ((KFSConstants.CoreModuleNamespaces.KFS.equals( moduleGroup.namespaceCode) && (isClassInCore(bo.getFullClassName())))
                    ||
                    doesPackagePrefixMatch( bo.getFullClassName(), PACKAGE_PREFIXES_BY_MODULE.get( moduleGroup.namespaceCode ) ))
                    && !bo.getFullClassName().startsWith("org.kuali.rice") ) {
                try {
                    if ( bo.getInactivationBlockingDefinitions() != null ) {
                        for ( InactivationBlockingDefinition ibd : bo.getInactivationBlockingDefinitions() ) {
                            testPassed &= validateDdBusinessObjectClassReference("Invalid Blocked BO Class", ibd.getBlockedBusinessObjectClass(), moduleGroup.namespaceCode, bo.getFullClassName(), null, disallowedPackagesForModule);
                            testPassed &= validateDdBusinessObjectClassReference("Invalid Blocking Reference BO Class", ibd.getBlockingReferenceBusinessObjectClass(), moduleGroup.namespaceCode, bo.getFullClassName(), null, disallowedPackagesForModule);
                            if ( ibd.getInactivationBlockingDetectionServiceBeanName() != null ) {
                                try {
                                    SpringContext.getBean( ibd.getInactivationBlockingDetectionServiceBeanName() );
                                } catch (Exception ex ) {
                                    addDdBusinessObjectError("Invalid inactivation blocking service", moduleGroup.namespaceCode, bo.getFullClassName(), null, ibd.getInactivationBlockingDetectionServiceBeanName());
                                }
                            }
                        }
                    }

                    for ( AttributeDefinition ad : bo.getAttributes() ) {
                        try {
                            ControlDefinition cd = ad.getControl();
                            testPassed &= validateDdBusinessObjectClassReference("Invalid Formatter Class", ad.getFormatterClass(), moduleGroup.namespaceCode, bo.getFullClassName(), ad.getName(), disallowedPackagesForModule);
                            if ( cd != null ) {
                                testPassed &= validateDdBusinessObjectClassReference("Invalid Control Value Finder", cd.getValuesFinderClass(), moduleGroup.namespaceCode, bo.getFullClassName(), ad.getName(), disallowedPackagesForModule);
                                testPassed &= validateDdBusinessObjectClassReference("Invalid BO class for KeyLabelBusinessObjectValueFinder", cd.getBusinessObjectClass(), moduleGroup.namespaceCode, bo.getFullClassName(), ad.getName(), disallowedPackagesForModule);
                            }
                        } catch ( Exception ex ) {
                            addDdBusinessObjectError("Exception Testing BO", moduleGroup.namespaceCode, bo.getFullClassName(), ad.getName(), ex.getClass().getName() + " : " + ex.getMessage() );
                            System.err.println( "Exception testing BO: " + bo.getFullClassName() + "/" + ad.getName() );
                            ex.printStackTrace();
                            testPassed = false;
                        }
                    }
                } catch( Exception ex ) {
                    addDdBusinessObjectError("Exception Testing BO", moduleGroup.namespaceCode, bo.getFullClassName(), null, ex.getClass().getName() + " : " + ex.getMessage() );
                    System.err.println( "Exception testing BO: " + bo.getFullClassName() );
                    ex.printStackTrace();
                    testPassed = false;
                }
            }
        }

        for ( org.kuali.rice.krad.datadictionary.DocumentEntry de : dd.getDocumentEntries().values() ) {
            if ( (de instanceof org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry && ((KFSConstants.CoreModuleNamespaces.KFS.equals( moduleGroup.namespaceCode) && isClassInCore(((org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry)de).getDataObjectClass().getName())) ||
                    doesPackagePrefixMatch( ((org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry)de).getDataObjectClass().getName(), PACKAGE_PREFIXES_BY_MODULE.get( moduleGroup.namespaceCode )) ))
                    || (de instanceof org.kuali.rice.kns.datadictionary.TransactionalDocumentEntry && ((KFSConstants.CoreModuleNamespaces.KFS.equals( moduleGroup.namespaceCode) && isClassInCore( de.getDocumentClass().getName())) ||
                            doesPackagePrefixMatch( de.getDocumentClass().getName(), PACKAGE_PREFIXES_BY_MODULE.get( moduleGroup.namespaceCode ))) ) ) {
                KNSDocumentEntry knsDocEntry = (KNSDocumentEntry)de;
                try {
                    if ( de instanceof org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry ) {
                        org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry mde = (org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry)de;
                        testPassed &= validateDdDocumentClassReference("Invalid Maintainable Class", mde.getMaintainableClass(), moduleGroup.namespaceCode, knsDocEntry.getDocumentTypeName(), null, disallowedPackagesForModule);
                        for ( MaintainableSectionDefinition msd : mde.getMaintainableSections() ) {
                            for ( MaintainableItemDefinition mid : msd.getMaintainableItems() ) {
                                if ( mid instanceof MaintainableCollectionDefinition ) {
                                    testPassed &= checkMaintainableCollection(moduleGroup.namespaceCode, knsDocEntry.getDocumentTypeName(), (MaintainableCollectionDefinition)mid, disallowedPackagesForModule);
                                }
                                if ( mid instanceof MaintainableFieldDefinition ) {
                                    testPassed &= checkMaintainableField( moduleGroup.namespaceCode, knsDocEntry.getDocumentTypeName(), (MaintainableFieldDefinition)mid, disallowedPackagesForModule);
                                }
                            }
                        }
                    } else { // trans doc

                    }
                    testPassed &= validateDdDocumentClassReference("Invalid Business Rules Class", knsDocEntry.getBusinessRulesClass(), moduleGroup.namespaceCode, knsDocEntry.getDocumentTypeName(), null, disallowedPackagesForModule);
                    testPassed &= validateDdDocumentClassReference("Invalid DerivedValuesSetterClass", knsDocEntry.getDerivedValuesSetterClass(), moduleGroup.namespaceCode, knsDocEntry.getDocumentTypeName(), null, disallowedPackagesForModule);
                    testPassed &= validateDdDocumentClassReference("Invalid DocumentAuthorizerClass", knsDocEntry.getDocumentAuthorizerClass(), moduleGroup.namespaceCode, knsDocEntry.getDocumentTypeName(), null, disallowedPackagesForModule);
                    testPassed &= validateDdDocumentClassReference("Invalid DocumentPresentationControllerClass", knsDocEntry.getDocumentPresentationControllerClass(), moduleGroup.namespaceCode, knsDocEntry.getDocumentTypeName(), null, disallowedPackagesForModule);
                    testPassed &= validateDdDocumentClassReference("Invalid PromptBeforeValidationClass", knsDocEntry.getPromptBeforeValidationClass(), moduleGroup.namespaceCode, knsDocEntry.getDocumentTypeName(), null, disallowedPackagesForModule);
                } catch ( Exception ex ) {
                    addDdDocumentError("Exception validating Document", moduleGroup.namespaceCode, knsDocEntry.getDocumentTypeName(), null, ex.getClass().getName() + " : " + ex.getMessage() );
                    System.err.println( "Exception validating Document: " + knsDocEntry.getDocumentTypeName() );
                    testPassed = false;
                }
            }
        }

        return testPassed;
    }

    private boolean isClassInCore(String fullClassName) {
        boolean isBoInCore = false;

        for (String namespaceCode : SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.keySet()) {
            if (doesPackagePrefixMatch( fullClassName, PACKAGE_PREFIXES_BY_MODULE.get( namespaceCode ) )) {
                isBoInCore = true;
                break;
            }
        }

        return isBoInCore;
    }


    protected void addDdBusinessObjectError( String errorType, String namespaceCode, String businessObjectClassName, String attributeName, String problemClassName ) {
        ddErrorMessage.append( "\n" ).append( namespaceCode ).append( " - BO: " );
        ddErrorMessage.append( businessObjectClassName );
        if ( attributeName != null ) {
            ddErrorMessage.append( " / Attrib: " ).append( attributeName );
        }
        ddErrorMessage.append( " / " ).append( errorType ).append( ": " ).append( problemClassName );
    }

    protected boolean validateDdBusinessObjectClassReference( String errorType, String testClassName, String namespaceCode, String businessObjectClassName, String attributeName, List<String> disallowedPackages ) {
        if (StringUtils.isBlank(testClassName)) {
            return true;
        }
        try {
            Class<?> testClass = Class.forName(testClassName.trim());
            return validateDdBusinessObjectClassReference(errorType, testClass, namespaceCode, businessObjectClassName, attributeName, disallowedPackages);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean validateDdBusinessObjectClassReference( String errorType, Class<? extends Object> testClass, String namespaceCode, String businessObjectClassName, String attributeName, List<String> disallowedPackages ) {
        if ( testClass != null ) {
            if ( doesPackagePrefixMatch(testClass.getName(), disallowedPackages) ) {
                addDdBusinessObjectError(errorType, namespaceCode, businessObjectClassName, attributeName, testClass.getName());
                return false;
            }
        }
        return true;
    }

    protected void addDdDocumentError( String errorType, String namespaceCode, String documentTypeName, String fieldName, String problemClassName ) {
        ddErrorMessage.append( "\n" ).append( namespaceCode ).append( " - Doc: " );
        ddErrorMessage.append( documentTypeName );
        if ( fieldName != null ) {
            ddErrorMessage.append( " / Field: " ).append( fieldName );
        }
        ddErrorMessage.append( " / " ).append( errorType ).append( ": " ).append( problemClassName );
    }

    protected boolean validateDdDocumentClassReference( String errorType, Class<? extends Object> testClass, String namespaceCode, String documentTypeName, String fieldName, List<String> disallowedPackages ) {
        if ( testClass != null ) {
            if ( doesPackagePrefixMatch(testClass.getName(), disallowedPackages) ) {
                addDdDocumentError(errorType, namespaceCode, documentTypeName, fieldName, testClass.getName());
                return false;
            }
        }
        return true;
    }

    protected boolean checkMaintainableCollection( String namespaceCode, String documentTypeName, MaintainableCollectionDefinition collection, List<String> disallowedPackages ) {
        boolean testPassed = true;
        testPassed &= validateDdDocumentClassReference("Invalid Collection BO Class", collection.getBusinessObjectClass(), namespaceCode, documentTypeName, collection.getName(), disallowedPackages);
        testPassed &= validateDdDocumentClassReference("Invalid Collection Source Class", collection.getSourceClassName(), namespaceCode, documentTypeName, collection.getName(), disallowedPackages);
        for ( MaintainableFieldDefinition mfd : collection.getMaintainableFields() ) {
            testPassed &= checkMaintainableField( namespaceCode, documentTypeName, mfd, disallowedPackages);
        }
        for ( MaintainableCollectionDefinition mcd : collection.getMaintainableCollections() ) {
            testPassed &= checkMaintainableCollection( namespaceCode, documentTypeName, mcd, disallowedPackages);
        }

        return testPassed;
    }
    protected boolean checkMaintainableField( String namespaceCode, String documentTypeName, MaintainableFieldDefinition field, List<String> disallowedPackages ) {
        boolean testPassed = true;
        try {
            testPassed &= validateDdDocumentClassReference("Invalid Default Value Finder Class", field.getDefaultValueFinderClass(), namespaceCode, documentTypeName, field.getName(), disallowedPackages);
            testPassed &= validateDdDocumentClassReference("Invalid Override Lookup Class", field.getOverrideLookupClass(), namespaceCode, documentTypeName, field.getName(), disallowedPackages);
        } catch ( Exception ex ) {
            addDdDocumentError("Exception validating Maint Doc Field", namespaceCode, documentTypeName, field.getName(), ex.getClass().getName() + " : " + ex.getMessage() );
            System.err.println( "Exception validating Maint Doc Field: " + documentTypeName + "/" + field.getName() );
            ex.printStackTrace();
            testPassed = false;
        }
        return testPassed;
    }

    protected boolean doesPackagePrefixMatch( String className, List<String> packagePrefixList ) {
        if (packagePrefixList != null) {
            for ( String pkg : packagePrefixList ) {
                if ( className.startsWith(pkg) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public class ModuleGroup {
        public String namespaceCode;
        public HashSet<String> optionalModuleDependencyNamespaceCodes = new HashSet<String>();

        public ModuleGroup() {
            // TODO Auto-generated constructor stub
        }

        public ModuleGroup( String namespaceCode ) {
            this.namespaceCode = namespaceCode;
        }

        @Override
        public String toString() {
            return namespaceCode + (optionalModuleDependencyNamespaceCodes.isEmpty()?"":(" - depends on: " + optionalModuleDependencyNamespaceCodes));
        }
    }

    public List<ModuleGroup> retrieveModuleGroups() throws Exception {
        List<ModuleGroup> moduleGroups = new ArrayList<ModuleGroup>();

        for (String systemNamespaceCode : SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.keySet()) {
            ModuleGroup systemModuleGroup = new ModuleGroup();
            systemModuleGroup.namespaceCode = systemNamespaceCode;
            moduleGroups.add(systemModuleGroup);
        }

        moduleGroups.addAll(retrieveOptionalModuleGroups());

        return moduleGroups;
    }

    public List<ModuleGroup> retrieveOptionalModuleGroups() throws Exception {
        Document designXmlDocument = getDesignXmlDocument();
        List<Element> optionalModuleDefinitions = retrieveOptionalModuleDefinitions(designXmlDocument);
        List<ModuleGroup> optionalModuleGroups = new ArrayList<ModuleGroup>();

        for (Element optionalModuleDefinition : optionalModuleDefinitions) {
            ModuleGroup optionalModuleGroup = buildOptionalModuleGroup(optionalModuleDefinition);
            if (optionalModuleGroup != null) {
                optionalModuleGroups.add(optionalModuleGroup);
            }
        }

        return optionalModuleGroups;
    }

    public Document getDesignXmlDocument() throws Exception {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
        InputStream in = resourceLoader.getResource(DefaultResourceLoader.CLASSPATH_URL_PREFIX + "design.xml").getInputStream();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(in);
        return doc;
    }

    public List<Element> retrieveOptionalModuleDefinitions(Document designXmlDocument) throws Exception {
        List<Element> optionalModuleDefinitions = new ArrayList<Element>();
        Element root = designXmlDocument.getDocumentElement();

        // in the design.xml file, an optional module/package is specified by a <package> tag that does not have the needdeclarations attribute equal false
        NodeList packages = root.getElementsByTagName("package");
        for (int i = 0; i < packages.getLength(); i++) {
            Element packageElement = (Element) packages.item(i);
            if (!"false".equals(packageElement.getAttribute("needdeclarations"))) {
                optionalModuleDefinitions.add(packageElement);
            }
        }
        return optionalModuleDefinitions;
    }

    public ModuleGroup buildOptionalModuleGroup(Element optionalPackageElement) {
        ModuleGroup moduleGroup = null;
        if (OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.containsKey(optionalPackageElement.getAttribute("name"))) {
            moduleGroup = new ModuleGroup();
            moduleGroup.namespaceCode = OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.get(optionalPackageElement.getAttribute("name"));
            if (StringUtils.isNotBlank(optionalPackageElement.getAttribute("depends"))) {
                if (OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.containsKey(optionalPackageElement.getAttribute("depends"))) {
                    moduleGroup.optionalModuleDependencyNamespaceCodes.add(OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.get(optionalPackageElement.getAttribute("depends")));
                }
            }
            NodeList dependsElements = optionalPackageElement.getElementsByTagName("depends");
            for (int i = 0; i < dependsElements.getLength(); i++) {
                Element dependsElement = (Element) dependsElements.item(i);
                if (OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.containsKey(StringUtils.trim(dependsElement.getTextContent()))) {
                    moduleGroup.optionalModuleDependencyNamespaceCodes.add(OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.get(StringUtils.trim(dependsElement.getTextContent())));
                }
            }
        }
        return moduleGroup;
    }

    protected static void stopSpringContext() {
        try {
            SpringContext.close();
        } catch (Exception e) {
            System.out.println("Caught exception shutting down spring");
            e.printStackTrace();
        }
    }
}
