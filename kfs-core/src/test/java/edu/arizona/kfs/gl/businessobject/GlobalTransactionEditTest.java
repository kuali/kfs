package edu.arizona.kfs.gl.businessobject;

import org.kuali.kfs.gl.businessobject.OriginEntryTestBase;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.HashMap;
import java.util.Map;



/**
 * OriginEntryTestBase...the uberpowerful base of a lot of GL tests.  Basically, this class provides
 * many convenience methods for writing tests that test against large batches of origin entries.
 */
@ConfigureContext
public class GlobalTransactionEditTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryTestBase.class);

    protected BusinessObjectService boService;


    public GlobalTransactionEditTest() {
        super();
    }


    /**
     * Sets up this test base; that means getting some services from Spring and reseting the
     * enhancement flags.
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        if (LOG.isDebugEnabled()) {
            LOG.debug("setUp() starting");
        }

        // Other objects needed for the tests
        boService = SpringContext.getBean(BusinessObjectService.class);
    }

    public void testSaveAndLoadGTE() {
        GlobalTransactionEdit gte = new GlobalTransactionEdit();

        gte.setOriginCode("AB");
        gte.setFundGroupCode("XY");
        gte.setSubFundGroupCode("ABCDEF");
        gte.setActive(true);

        GlobalTransactionEditDetail gteObject = new GlobalTransactionEditDetail();
        gteObject.setDocumentTypeCode("ABCD");
        gteObject.setObjectTypeCode("YY");
        gteObject.setObjectSubTypeCode("XX");
        gteObject.setObjectCodeRulePurpose("this is a description");
        gteObject.setActive(true);

        gte.getGlobalTransactionEditDetails().add(gteObject);

        saveAndReloadGTE(gte);

        assertEquals("AB", gte.getOriginCode());
        assertEquals("XY", gte.getFundGroupCode());
        assertEquals("ABCDEF", gte.getSubFundGroupCode());
        assertEquals(true, gte.isActive());

        gteObject = gte.getGlobalTransactionEditDetails().get(0);
        assertEquals("ABCD", gteObject.getDocumentTypeCode());
        assertEquals("YY", gteObject.getObjectTypeCode());
        assertEquals("XX", gteObject.getObjectSubTypeCode());
        assertEquals("this is a description", gteObject.getObjectCodeRulePurpose());
        assertEquals(true, gteObject.isActive());
    }

    public void testAddUpdateDeleteGTEObjects() {
        GlobalTransactionEdit gte = new GlobalTransactionEdit();

        gte.setOriginCode("XY");
        gte.setFundGroupCode("XY");
        gte.setSubFundGroupCode("XYXYXY");
        gte.setActive(true);

        GlobalTransactionEditDetail gteObject;

        gteObject = new GlobalTransactionEditDetail();
        gteObject.setDocumentTypeCode("AAAA");
        gteObject.setObjectTypeCode("AA");
        gteObject.setObjectSubTypeCode("AA");
        gteObject.setObjectCodeRulePurpose("First");
        gteObject.setActive(true);
        gte.getGlobalTransactionEditDetails().add(gteObject);

        gteObject = new GlobalTransactionEditDetail();
        gteObject.setDocumentTypeCode("BBBB");
        gteObject.setObjectTypeCode("BB");
        gteObject.setObjectSubTypeCode("BB");
        gteObject.setObjectCodeRulePurpose("Second");
        gteObject.setActive(true);
        gte.getGlobalTransactionEditDetails().add(gteObject);

        gteObject = new GlobalTransactionEditDetail();
        gteObject.setDocumentTypeCode("CCCC");
        gteObject.setObjectTypeCode("CC");
        gteObject.setObjectSubTypeCode("CC");
        gteObject.setObjectCodeRulePurpose("Third");
        gteObject.setActive(true);
        gte.getGlobalTransactionEditDetails().add(gteObject);

        saveAndReloadGTE(gte);

        gte.getGlobalTransactionEditDetails().remove(1);
        saveAndReloadGTE(gte);
        assertEquals(2, gte.getGlobalTransactionEditDetails().size());

        gteObject = gte.getGlobalTransactionEditDetails().get(0);
        assertEquals("AAAA", gteObject.getDocumentTypeCode());

        gteObject = gte.getGlobalTransactionEditDetails().get(1);
        assertEquals("CCCC", gteObject.getDocumentTypeCode());

        gteObject.setDocumentTypeCode("DDDD");
        saveAndReloadGTE(gte);
        assertEquals("DDDD", gte.getGlobalTransactionEditDetails().get(1).getDocumentTypeCode());
    }

    private GlobalTransactionEdit loadGTEByPrimaryKey(String referenceOriginCode, String fundGroupCode, String subFundGroupCode) {


        Map<String, Object> pkeys = new HashMap<String, Object>();
        pkeys.put("referenceOriginCode", referenceOriginCode);
        pkeys.put("fundGroupCode", fundGroupCode);
        pkeys.put("subFundGroupCode", subFundGroupCode);
        GlobalTransactionEdit result = boService.findByPrimaryKey(GlobalTransactionEdit.class, pkeys);

        return result;
    }

    private GlobalTransactionEdit saveAndReloadGTE(GlobalTransactionEdit gte) {
        boService.save(gte);

        gte = loadGTEByPrimaryKey(gte.getOriginCode(), gte.getFundGroupCode(), gte.getSubFundGroupCode());

        return gte;
    }

}
