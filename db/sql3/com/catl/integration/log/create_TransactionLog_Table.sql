set echo on
REM Creating table TransactionLog for com.catl.integration.log.TransactionLog
set echo off
CREATE TABLE TransactionLog (
   action   VARCHAR2(600),
   cellMode   VARCHAR2(600),
   cellVolume   VARCHAR2(600),
   childPartNumber   VARCHAR2(600),
   creator   VARCHAR2(600),
   defaultUnit   VARCHAR2(600),
   description   VARCHAR2(4000),
   drawingNumber   VARCHAR2(600),
   drawingVersion   VARCHAR2(600),
   ecnName   VARCHAR2(4000),
   ecnNumber   VARCHAR2(600),
   englishName   VARCHAR2(600),
   fullVoltage   VARCHAR2(600),
   materialGroup   VARCHAR2(600),
   model   VARCHAR2(600),
   oid   VARCHAR2(600),
   oldPartNumber   VARCHAR2(600),
   partName   VARCHAR2(600),
   partNumber   VARCHAR2(600),
   productEnergy   VARCHAR2(600),
   quantity   VARCHAR2(600),
   source   VARCHAR2(600),
   specification   VARCHAR2(3000),
   standardVoltage   VARCHAR2(600),
   str1   VARCHAR2(600),
   str10   VARCHAR2(600),
   str11   VARCHAR2(600),
   str12   VARCHAR2(600),
   str2   VARCHAR2(600),
   str3   VARCHAR2(600),
   str4   VARCHAR2(600),
   str5   VARCHAR2(600),
   str6   VARCHAR2(600),
   str7   VARCHAR2(600),
   str8   VARCHAR2(600),
   str9   VARCHAR2(600),
   substitutePartNumber   VARCHAR2(600),
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
   validDate   VARCHAR2(600),
   versionBig   VARCHAR2(600),
   versionSmall   VARCHAR2(600),
 CONSTRAINT PK_TransactionLog PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE TransactionLog IS 'Table TransactionLog created for com.catl.integration.log.TransactionLog'
/
REM @//com/catl/integration/log/TransactionLog_UserAdditions
