set echo on
REM Creating table DrawingSendERPLog for com.catl.integration.log.DrawingSendERPLog
set echo off
CREATE TABLE DrawingSendERPLog (
   fileName   VARCHAR2(600),
   objectInPromotionIteration   VARCHAR2(600),
   objectInPromotionNumber   VARCHAR2(600),
   objectInPromotionType   VARCHAR2(600),
   objectInPromotionVersion   VARCHAR2(600),
   oid   VARCHAR2(600),
   partIteration   VARCHAR2(600),
   partNumber   VARCHAR2(600),
   partVersion   VARCHAR2(600),
   relationObjectNumber   VARCHAR2(600),
   relationObjectType   VARCHAR2(600),
   rootPath   VARCHAR2(600),
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
 CONSTRAINT PK_DrawingSendERPLog PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE DrawingSendERPLog IS 'Table DrawingSendERPLog created for com.catl.integration.log.DrawingSendERPLog'
/
REM @//com/catl/integration/log/DrawingSendERPLog_UserAdditions
