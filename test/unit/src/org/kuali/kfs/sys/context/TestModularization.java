package org.kuali.kfs.sys.context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.rice.core.resourceloader.ContextClassLoaderBinder;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.kns.authorization.KualiModuleAuthorizerBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.KNSConstants;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.ltd.getahead.dwr.impl.DTDEntityResolver;
import uk.ltd.getahead.dwr.util.LogErrorHandler;

public class TestModularization extends KualiTestBase {
    private static final String BASE_SPRING_FILESET = "SpringBeans.xml,SpringDataSourceBeans.xml,SpringRiceBeans.xml,org/kuali/kfs/integration/SpringBeansModules.xml,org/kuali/kfs/sys/spring-sys.xml,org/kuali/kfs/coa/spring-coa.xml,org/kuali/kfs/fp/spring-fp.xml,org/kuali/kfs/gl/spring-gl.xml,org/kuali/kfs/pdp/spring-pdp.xml,org/kuali/kfs/vnd/spring-vnd.xml";

    private static final Map<String, String> OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX = new HashMap<String, String>();
    static {
        OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.put("KFS-AR", "ar");
        OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.put("KFS-BC", "bc");
        OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.put("KFS-CAB", "cab");
        OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.put("KFS-CAM", "cam");
        OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.put("KFS-CG", "cg");
        OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.put("KFS-EC", "ec");
        OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.put("KFS-LD", "ld");
        OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.put("KFS-PURAP", "purap");
    }
    private static final Map<String, String> SYSTEM_MODULE_IDS_TO_SPRING_FILE_SUFFIX = new HashMap<String, String>();
    static {
        SYSTEM_MODULE_IDS_TO_SPRING_FILE_SUFFIX.put("KFS-COA", "coa");
        SYSTEM_MODULE_IDS_TO_SPRING_FILE_SUFFIX.put("KFS-FP", "fp");
        SYSTEM_MODULE_IDS_TO_SPRING_FILE_SUFFIX.put("KFS-GL", "gl");
        SYSTEM_MODULE_IDS_TO_SPRING_FILE_SUFFIX.put("KFS-PDP", "pdp");
        SYSTEM_MODULE_IDS_TO_SPRING_FILE_SUFFIX.put("KFS-SYS", "sys");
        SYSTEM_MODULE_IDS_TO_SPRING_FILE_SUFFIX.put("KFS-VND", "vnd");
    }

    public void testSpring() throws Exception {
        boolean testSucceeded = true;
        StringBuffer errorMessage = new StringBuffer("The following optional modules have interdependencies in Spring configuration:");
        for (String namespaceCode : OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.keySet()) {
            testSucceeded = testSucceeded & testOptionalModuleSpringConfiguration(namespaceCode, OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.get(namespaceCode), errorMessage);
        }
        System.out.print(errorMessage.toString());
        assertTrue(errorMessage.toString(), testSucceeded);
    }

    private boolean testOptionalModuleSpringConfiguration(String namespaceCode, String springFileSuffix, StringBuffer errorMessage) {
        // switch to a different context classloader context so that we don't blow away our existing configuration
        ContextClassLoaderBinder binder = new ContextClassLoaderBinder();
        binder.bind(new URLClassLoader(new URL[0]));
        ClassPathXmlApplicationContext context = null;
        try {
            String[] configLocations = new StringBuffer(BASE_SPRING_FILESET).append(",org/kuali/kfs/module/").append(springFileSuffix).append("/spring-").append(springFileSuffix).append(".xml").toString().split(",");
            context = new ClassPathXmlApplicationContext(configLocations);
            
            Map<String, DataDictionaryService> ddServiceBeans = context.getBeansOfType(DataDictionaryService.class);
            if (ddServiceBeans.size() != 1) {
                throw new RuntimeException("There should only be one DataDictionaryService bean, but " + ddServiceBeans.size() + " were found");
            }
            DataDictionaryService dataDictionaryService = ddServiceBeans.entrySet().iterator().next().getValue();
            // DO NOT allow for concurrent validation of the DD
            dataDictionaryService.getDataDictionary().parseDataDictionaryConfigurationFiles( false );
            return true;
        }
        catch (Exception e) {
            errorMessage.append("\n").append(namespaceCode).append("\n\t").append(e.getMessage());
            return false;
        }
        finally {
            try {
                if (context != null) {
                    context.close();
                }
            }
            catch (Exception e) {
            }
            binder.unbind();
        }
    }

    @ConfigureContext
    public void testOjb() throws Exception {
        boolean testSucceeded = true;
        StringBuffer errorMessage = new StringBuffer("The following optional modules have interdependencies in OJB configuration:");
        HashSet<String> allModuleIds = new HashSet<String>();
        for (String moduleId : SYSTEM_MODULE_IDS_TO_SPRING_FILE_SUFFIX.keySet()) {
            allModuleIds.add(moduleId);
        }
        for (String moduleId : OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.keySet()) {
            allModuleIds.add(moduleId);
        }
        for (String moduleId : allModuleIds) {
            testSucceeded = testSucceeded & testOptionalModuleOjbConfiguration(moduleId, errorMessage);
        }
        assertTrue(errorMessage.toString(), testSucceeded);
    }

    private boolean testOptionalModuleOjbConfiguration(String moduleId, StringBuffer errorMessage) throws FileNotFoundException {
        boolean testSucceeded = true;
        for (String referencedModuleId : OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.keySet()) {
            if (!moduleId.equals(referencedModuleId)) {
                String firstDatabaseRepositoryFilePath = SpringContext.getBean(KualiModuleService.class).getModuleService(moduleId).getModuleConfiguration().getDatabaseRepositoryFilePaths().iterator().next();
                // the first database repository file path is typically the file that comes shipped with KFS.  If institutions override it, this unit test will not test them
                Scanner scanner = new Scanner(new File("work/src/" + firstDatabaseRepositoryFilePath));
                int count = 0;
                while (scanner.hasNext()) {
                    String token = scanner.next();
                    String firstPackagePrefix = (SpringContext.getBean(KualiModuleService.class).getModuleService(referencedModuleId).getModuleConfiguration()).getPackagePrefixes().iterator().next();
                    // A module may be responsible for many packages, but the first one should be the KFS built-in package that is *not* the module's integration package
                    if (token.contains(firstPackagePrefix)) {
                        count++;
                    }
                }
                if (count > 0) {
                    if (testSucceeded) {
                        testSucceeded = false;
                        errorMessage.append("\n").append(moduleId).append(": ");
                    }
                    else {
                        errorMessage.append(", ");
                    }
                    errorMessage.append(count).append(" references to ").append(referencedModuleId);
                }
            }
        }
        return testSucceeded;
    }
    
    @ConfigureContext
    public void testDwr() throws Exception {
        boolean testSucceeded = true;
        StringBuffer errorMessage = new StringBuffer("The following optional modules have interdependencies in DWR configuration:");
        
        
        for (String namespaceCode : SYSTEM_MODULE_IDS_TO_SPRING_FILE_SUFFIX.keySet()) {
            testSucceeded &= testDwrModuleConfiguration(namespaceCode, errorMessage);
        }
        for (String namespaceCode : OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.keySet()) {
            testSucceeded &= testDwrModuleConfiguration(namespaceCode, errorMessage);
        }

        assertTrue(errorMessage.toString(), testSucceeded);
    }
    
    private boolean testDwrModuleConfiguration(String namespaceCode, StringBuffer errorMessage) throws Exception {
        List<String> dwrFiles = SpringContext.getBean(KualiModuleService.class).getModuleServiceByNamespaceCode(namespaceCode).getModuleConfiguration().getScriptConfigurationFilePaths();
        boolean testSucceeded = true;
        if (dwrFiles != null && dwrFiles.size() > 0) {
            // the DWR file delivered with KFS (i.e. the base) should be the first element of the list
            String baseDwrFileName = dwrFiles.get(0);
            Document dwrDocument = generateDwrConfigDocument(baseDwrFileName);
            testSucceeded = testDwrModuleConfiguration(baseDwrFileName, dwrDocument, namespaceCode, errorMessage);
        }
        return testSucceeded;
    }
    
    private boolean testDwrModuleConfiguration(String dwrFileName, Document dwrDocument, String namespaceCode, StringBuffer errorMessage) throws Exception {
       boolean beanClassNamesOK = testDwrBeanClassNames(dwrFileName, dwrDocument, namespaceCode, errorMessage);
       boolean springServicesOK = testDwrSpringServices(dwrFileName, dwrDocument, namespaceCode, errorMessage);
       return beanClassNamesOK && springServicesOK;
    }
    
    private boolean testDwrBeanClassNames(String dwrFileName, Document dwrDocument, String namespaceCode, StringBuffer errorMessage) {
        boolean testSucceeded = true;
        List<String> dwrBeanClassNames = retrieveDwrBeanClassNames(dwrDocument);
        for (String referencedNamespaceCode : OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.keySet()) {
            if (!referencedNamespaceCode.equals(namespaceCode)) {
                String firstPackagePrefix = (SpringContext.getBean(KualiModuleService.class).getModuleService(referencedNamespaceCode).getModuleConfiguration()).getPackagePrefixes().iterator().next();
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
                    errorMessage.append("\n").append(dwrFileName).append(" (in module ").append(namespaceCode).append(") has ").append(count).append(" references to business objects from ").append(referencedNamespaceCode);
                }
            }
        }
        return testSucceeded;
    }
    
    private boolean testDwrSpringServices(String dwrFileName, Document dwrDocument, String namespaceCode, StringBuffer errorMessage) {
        boolean testSucceeded = true;
        boolean isSystemModule = SYSTEM_MODULE_IDS_TO_SPRING_FILE_SUFFIX.containsKey(namespaceCode);
        
        // switch to a different context classloader context so that we don't blow away our existing configuration
        ContextClassLoaderBinder binder = new ContextClassLoaderBinder();
        binder.bind(new URLClassLoader(new URL[0]));
        ClassPathXmlApplicationContext context = null;
        try {
            String[] configLocations = null;
            if (isSystemModule) {
                // if we're testing a system module, then we will only load up the base spring files in our new app context
                configLocations = BASE_SPRING_FILESET.split(",");
            }
            else {
                // if we're testing an optional module, then we will need to load up the base spring files as well as the optional module's beans
                String springFileSuffix = OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.get(namespaceCode);
                configLocations = new StringBuffer(BASE_SPRING_FILESET).append(",org/kuali/kfs/module/").append(springFileSuffix).append("/spring-").append(springFileSuffix).append(".xml").toString().split(",");
            }
            context = new ClassPathXmlApplicationContext(configLocations);
            
            List<String> serviceNames = retrieveDwrServiceNames(dwrDocument);
            for (String serviceName : serviceNames) {
                if (!context.containsBean(serviceName)) {
                    testSucceeded = false;
                    errorMessage.append("\n").append(dwrFileName).append(" (in module ").append(namespaceCode).append(") has references to spring bean \"").append(serviceName).append("\" that is not defined in the base spring files");
                    if (!isSystemModule) {
                        String springFileSuffix = OPTIONAL_MODULE_IDS_TO_SPRING_FILE_SUFFIX.get(namespaceCode);
                        errorMessage.append(" or in ").append(",org/kuali/kfs/module/").append(springFileSuffix).append("/spring-").append(springFileSuffix).append(".xml").toString().split(",");
                    }
                }
            }
        }
        catch (Exception e) {
            errorMessage.append("\n").append(namespaceCode).append("\n\t").append(e.getMessage());
            return false;
        }
        finally {
            try {
                if (context != null) {
                    context.close();
                }
            }
            catch (Exception e) {
            }
            binder.unbind();
        }
        
        return testSucceeded;
    }
    
    private Document generateDwrConfigDocument(String fileName) throws Exception {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
        InputStream in = resourceLoader.getResource(fileName).getInputStream();
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver(new DTDEntityResolver());
        db.setErrorHandler(new LogErrorHandler());

        Document doc = db.parse(in);
        return doc;
    }
    
    private List<String> retrieveDwrServiceNames(Document dwrDocument) {
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
    
    private List<String> retrieveDwrBeanClassNames(Document dwrDocument) {
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
}
