-- This script contains any database commands which should be executed against your KFS database as part
-- of upgrading from Release 3.0 to Release 3.0.1
 
--KFSMI-5118: missed field resize
alter table PDP_PMT_GRP_T modify PMT_PAYEE_NM varchar2(123)
/