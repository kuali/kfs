-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 4/8/10 1:15 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-04-08-5580-1-PDP_PMT_GRP_T.xml::5580-1-1::Travis::(MD5Sum: 6d2cf43c9793e314f0525f48991e85b9)
-- -- Changeset updates/2010-04-08-5580-1-PDP_PMT_GRP_T.xml::5580-1-1::Travis::(MD5Sum: ce135f6a8b516e371d811471e60ff18) -- ALTER TABLE PDP_PMT_GRP_T MODIFY(PMT_SORT_ORD_VAL varchar2(150)) ALTER TABLE `PDP_PMT_GRP_T` MODIFY `PMT_SORT_ORD_VAL` VARCHAR(150));
ALTER TABLE PDP_PMT_GRP_T MODIFY(PMT_SORT_ORD_VAL varchar2(150));
