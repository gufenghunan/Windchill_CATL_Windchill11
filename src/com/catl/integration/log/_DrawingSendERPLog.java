package com.catl.integration.log;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _DrawingSendERPLog extends wt.fc.WTObject implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final java.lang.String RESOURCE = "com.catl.integration.log.logResource";
   static final java.lang.String CLASSNAME = DrawingSendERPLog.class.getName();

   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public static final java.lang.String OID = "oid";
   static int OID_UPPER_LIMIT = -1;
   java.lang.String oid;
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public java.lang.String getOid() {
      return oid;
   }
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public void setOid(java.lang.String oid) throws wt.util.WTPropertyVetoException {
      oidValidate(oid);
      this.oid = oid;
   }
   void oidValidate(java.lang.String oid) throws wt.util.WTPropertyVetoException {
      if (OID_UPPER_LIMIT < 1) {
         try { OID_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("oid").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OID_UPPER_LIMIT = 200; }
      }
      if (oid != null && !wt.fc.PersistenceHelper.checkStoredLength(oid.toString(), OID_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "oid"), java.lang.String.valueOf(java.lang.Math.min(OID_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "oid", this.oid, oid));
   }

   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public static final java.lang.String OBJECT_IN_PROMOTION_NUMBER = "objectInPromotionNumber";
   static int OBJECT_IN_PROMOTION_NUMBER_UPPER_LIMIT = -1;
   java.lang.String objectInPromotionNumber;
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public java.lang.String getObjectInPromotionNumber() {
      return objectInPromotionNumber;
   }
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public void setObjectInPromotionNumber(java.lang.String objectInPromotionNumber) throws wt.util.WTPropertyVetoException {
      objectInPromotionNumberValidate(objectInPromotionNumber);
      this.objectInPromotionNumber = objectInPromotionNumber;
   }
   void objectInPromotionNumberValidate(java.lang.String objectInPromotionNumber) throws wt.util.WTPropertyVetoException {
      if (OBJECT_IN_PROMOTION_NUMBER_UPPER_LIMIT < 1) {
         try { OBJECT_IN_PROMOTION_NUMBER_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("objectInPromotionNumber").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OBJECT_IN_PROMOTION_NUMBER_UPPER_LIMIT = 200; }
      }
      if (objectInPromotionNumber != null && !wt.fc.PersistenceHelper.checkStoredLength(objectInPromotionNumber.toString(), OBJECT_IN_PROMOTION_NUMBER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "objectInPromotionNumber"), java.lang.String.valueOf(java.lang.Math.min(OBJECT_IN_PROMOTION_NUMBER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "objectInPromotionNumber", this.objectInPromotionNumber, objectInPromotionNumber));
   }

   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public static final java.lang.String OBJECT_IN_PROMOTION_TYPE = "objectInPromotionType";
   static int OBJECT_IN_PROMOTION_TYPE_UPPER_LIMIT = -1;
   java.lang.String objectInPromotionType;
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public java.lang.String getObjectInPromotionType() {
      return objectInPromotionType;
   }
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public void setObjectInPromotionType(java.lang.String objectInPromotionType) throws wt.util.WTPropertyVetoException {
      objectInPromotionTypeValidate(objectInPromotionType);
      this.objectInPromotionType = objectInPromotionType;
   }
   void objectInPromotionTypeValidate(java.lang.String objectInPromotionType) throws wt.util.WTPropertyVetoException {
      if (OBJECT_IN_PROMOTION_TYPE_UPPER_LIMIT < 1) {
         try { OBJECT_IN_PROMOTION_TYPE_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("objectInPromotionType").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OBJECT_IN_PROMOTION_TYPE_UPPER_LIMIT = 200; }
      }
      if (objectInPromotionType != null && !wt.fc.PersistenceHelper.checkStoredLength(objectInPromotionType.toString(), OBJECT_IN_PROMOTION_TYPE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "objectInPromotionType"), java.lang.String.valueOf(java.lang.Math.min(OBJECT_IN_PROMOTION_TYPE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "objectInPromotionType", this.objectInPromotionType, objectInPromotionType));
   }

   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public static final java.lang.String OBJECT_IN_PROMOTION_VERSION = "objectInPromotionVersion";
   static int OBJECT_IN_PROMOTION_VERSION_UPPER_LIMIT = -1;
   java.lang.String objectInPromotionVersion;
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public java.lang.String getObjectInPromotionVersion() {
      return objectInPromotionVersion;
   }
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public void setObjectInPromotionVersion(java.lang.String objectInPromotionVersion) throws wt.util.WTPropertyVetoException {
      objectInPromotionVersionValidate(objectInPromotionVersion);
      this.objectInPromotionVersion = objectInPromotionVersion;
   }
   void objectInPromotionVersionValidate(java.lang.String objectInPromotionVersion) throws wt.util.WTPropertyVetoException {
      if (OBJECT_IN_PROMOTION_VERSION_UPPER_LIMIT < 1) {
         try { OBJECT_IN_PROMOTION_VERSION_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("objectInPromotionVersion").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OBJECT_IN_PROMOTION_VERSION_UPPER_LIMIT = 200; }
      }
      if (objectInPromotionVersion != null && !wt.fc.PersistenceHelper.checkStoredLength(objectInPromotionVersion.toString(), OBJECT_IN_PROMOTION_VERSION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "objectInPromotionVersion"), java.lang.String.valueOf(java.lang.Math.min(OBJECT_IN_PROMOTION_VERSION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "objectInPromotionVersion", this.objectInPromotionVersion, objectInPromotionVersion));
   }

   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public static final java.lang.String OBJECT_IN_PROMOTION_ITERATION = "objectInPromotionIteration";
   static int OBJECT_IN_PROMOTION_ITERATION_UPPER_LIMIT = -1;
   java.lang.String objectInPromotionIteration;
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public java.lang.String getObjectInPromotionIteration() {
      return objectInPromotionIteration;
   }
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public void setObjectInPromotionIteration(java.lang.String objectInPromotionIteration) throws wt.util.WTPropertyVetoException {
      objectInPromotionIterationValidate(objectInPromotionIteration);
      this.objectInPromotionIteration = objectInPromotionIteration;
   }
   void objectInPromotionIterationValidate(java.lang.String objectInPromotionIteration) throws wt.util.WTPropertyVetoException {
      if (OBJECT_IN_PROMOTION_ITERATION_UPPER_LIMIT < 1) {
         try { OBJECT_IN_PROMOTION_ITERATION_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("objectInPromotionIteration").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OBJECT_IN_PROMOTION_ITERATION_UPPER_LIMIT = 200; }
      }
      if (objectInPromotionIteration != null && !wt.fc.PersistenceHelper.checkStoredLength(objectInPromotionIteration.toString(), OBJECT_IN_PROMOTION_ITERATION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "objectInPromotionIteration"), java.lang.String.valueOf(java.lang.Math.min(OBJECT_IN_PROMOTION_ITERATION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "objectInPromotionIteration", this.objectInPromotionIteration, objectInPromotionIteration));
   }

   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public static final java.lang.String RELATION_OBJECT_NUMBER = "relationObjectNumber";
   static int RELATION_OBJECT_NUMBER_UPPER_LIMIT = -1;
   java.lang.String relationObjectNumber;
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public java.lang.String getRelationObjectNumber() {
      return relationObjectNumber;
   }
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public void setRelationObjectNumber(java.lang.String relationObjectNumber) throws wt.util.WTPropertyVetoException {
      relationObjectNumberValidate(relationObjectNumber);
      this.relationObjectNumber = relationObjectNumber;
   }
   void relationObjectNumberValidate(java.lang.String relationObjectNumber) throws wt.util.WTPropertyVetoException {
      if (RELATION_OBJECT_NUMBER_UPPER_LIMIT < 1) {
         try { RELATION_OBJECT_NUMBER_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("relationObjectNumber").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { RELATION_OBJECT_NUMBER_UPPER_LIMIT = 200; }
      }
      if (relationObjectNumber != null && !wt.fc.PersistenceHelper.checkStoredLength(relationObjectNumber.toString(), RELATION_OBJECT_NUMBER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "relationObjectNumber"), java.lang.String.valueOf(java.lang.Math.min(RELATION_OBJECT_NUMBER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "relationObjectNumber", this.relationObjectNumber, relationObjectNumber));
   }

   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public static final java.lang.String RELATION_OBJECT_TYPE = "relationObjectType";
   static int RELATION_OBJECT_TYPE_UPPER_LIMIT = -1;
   java.lang.String relationObjectType;
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public java.lang.String getRelationObjectType() {
      return relationObjectType;
   }
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public void setRelationObjectType(java.lang.String relationObjectType) throws wt.util.WTPropertyVetoException {
      relationObjectTypeValidate(relationObjectType);
      this.relationObjectType = relationObjectType;
   }
   void relationObjectTypeValidate(java.lang.String relationObjectType) throws wt.util.WTPropertyVetoException {
      if (RELATION_OBJECT_TYPE_UPPER_LIMIT < 1) {
         try { RELATION_OBJECT_TYPE_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("relationObjectType").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { RELATION_OBJECT_TYPE_UPPER_LIMIT = 200; }
      }
      if (relationObjectType != null && !wt.fc.PersistenceHelper.checkStoredLength(relationObjectType.toString(), RELATION_OBJECT_TYPE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "relationObjectType"), java.lang.String.valueOf(java.lang.Math.min(RELATION_OBJECT_TYPE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "relationObjectType", this.relationObjectType, relationObjectType));
   }

   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public static final java.lang.String PART_NUMBER = "partNumber";
   static int PART_NUMBER_UPPER_LIMIT = -1;
   java.lang.String partNumber;
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public java.lang.String getPartNumber() {
      return partNumber;
   }
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public void setPartNumber(java.lang.String partNumber) throws wt.util.WTPropertyVetoException {
      partNumberValidate(partNumber);
      this.partNumber = partNumber;
   }
   void partNumberValidate(java.lang.String partNumber) throws wt.util.WTPropertyVetoException {
      if (PART_NUMBER_UPPER_LIMIT < 1) {
         try { PART_NUMBER_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("partNumber").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { PART_NUMBER_UPPER_LIMIT = 200; }
      }
      if (partNumber != null && !wt.fc.PersistenceHelper.checkStoredLength(partNumber.toString(), PART_NUMBER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "partNumber"), java.lang.String.valueOf(java.lang.Math.min(PART_NUMBER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "partNumber", this.partNumber, partNumber));
   }

   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public static final java.lang.String PART_VERSION = "partVersion";
   static int PART_VERSION_UPPER_LIMIT = -1;
   java.lang.String partVersion;
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public java.lang.String getPartVersion() {
      return partVersion;
   }
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public void setPartVersion(java.lang.String partVersion) throws wt.util.WTPropertyVetoException {
      partVersionValidate(partVersion);
      this.partVersion = partVersion;
   }
   void partVersionValidate(java.lang.String partVersion) throws wt.util.WTPropertyVetoException {
      if (PART_VERSION_UPPER_LIMIT < 1) {
         try { PART_VERSION_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("partVersion").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { PART_VERSION_UPPER_LIMIT = 200; }
      }
      if (partVersion != null && !wt.fc.PersistenceHelper.checkStoredLength(partVersion.toString(), PART_VERSION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "partVersion"), java.lang.String.valueOf(java.lang.Math.min(PART_VERSION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "partVersion", this.partVersion, partVersion));
   }

   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public static final java.lang.String PART_ITERATION = "partIteration";
   static int PART_ITERATION_UPPER_LIMIT = -1;
   java.lang.String partIteration;
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public java.lang.String getPartIteration() {
      return partIteration;
   }
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public void setPartIteration(java.lang.String partIteration) throws wt.util.WTPropertyVetoException {
      partIterationValidate(partIteration);
      this.partIteration = partIteration;
   }
   void partIterationValidate(java.lang.String partIteration) throws wt.util.WTPropertyVetoException {
      if (PART_ITERATION_UPPER_LIMIT < 1) {
         try { PART_ITERATION_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("partIteration").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { PART_ITERATION_UPPER_LIMIT = 200; }
      }
      if (partIteration != null && !wt.fc.PersistenceHelper.checkStoredLength(partIteration.toString(), PART_ITERATION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "partIteration"), java.lang.String.valueOf(java.lang.Math.min(PART_ITERATION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "partIteration", this.partIteration, partIteration));
   }

   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public static final java.lang.String ROOT_PATH = "rootPath";
   static int ROOT_PATH_UPPER_LIMIT = -1;
   java.lang.String rootPath;
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public java.lang.String getRootPath() {
      return rootPath;
   }
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public void setRootPath(java.lang.String rootPath) throws wt.util.WTPropertyVetoException {
      rootPathValidate(rootPath);
      this.rootPath = rootPath;
   }
   void rootPathValidate(java.lang.String rootPath) throws wt.util.WTPropertyVetoException {
      if (ROOT_PATH_UPPER_LIMIT < 1) {
         try { ROOT_PATH_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("rootPath").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { ROOT_PATH_UPPER_LIMIT = 200; }
      }
      if (rootPath != null && !wt.fc.PersistenceHelper.checkStoredLength(rootPath.toString(), ROOT_PATH_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "rootPath"), java.lang.String.valueOf(java.lang.Math.min(ROOT_PATH_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "rootPath", this.rootPath, rootPath));
   }

   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public static final java.lang.String FILE_NAME = "fileName";
   static int FILE_NAME_UPPER_LIMIT = -1;
   java.lang.String fileName;
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public java.lang.String getFileName() {
      return fileName;
   }
   /**
    * @see com.catl.integration.log.DrawingSendERPLog
    */
   public void setFileName(java.lang.String fileName) throws wt.util.WTPropertyVetoException {
      fileNameValidate(fileName);
      this.fileName = fileName;
   }
   void fileNameValidate(java.lang.String fileName) throws wt.util.WTPropertyVetoException {
      if (FILE_NAME_UPPER_LIMIT < 1) {
         try { FILE_NAME_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("fileName").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { FILE_NAME_UPPER_LIMIT = 200; }
      }
      if (fileName != null && !wt.fc.PersistenceHelper.checkStoredLength(fileName.toString(), FILE_NAME_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "fileName"), java.lang.String.valueOf(java.lang.Math.min(FILE_NAME_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "fileName", this.fileName, fileName));
   }

   public java.lang.String getConceptualClassname() {
      return CLASSNAME;
   }

   public wt.introspection.ClassInfo getClassInfo() throws wt.introspection.WTIntrospectionException {
      return wt.introspection.WTIntrospector.getClassInfo(getConceptualClassname());
   }

   public java.lang.String getType() {
      try { return getClassInfo().getDisplayName(); }
      catch (wt.introspection.WTIntrospectionException wte) { return wt.util.WTStringUtilities.tail(getConceptualClassname(), '.'); }
   }

   public static final long EXTERNALIZATION_VERSION_UID = -2358330356420915985L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( fileName );
      output.writeObject( objectInPromotionIteration );
      output.writeObject( objectInPromotionNumber );
      output.writeObject( objectInPromotionType );
      output.writeObject( objectInPromotionVersion );
      output.writeObject( oid );
      output.writeObject( partIteration );
      output.writeObject( partNumber );
      output.writeObject( partVersion );
      output.writeObject( relationObjectNumber );
      output.writeObject( relationObjectType );
      output.writeObject( rootPath );
   }

   protected void super_writeExternal_DrawingSendERPLog(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (com.catl.integration.log.DrawingSendERPLog) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_DrawingSendERPLog(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "fileName", fileName );
      output.setString( "objectInPromotionIteration", objectInPromotionIteration );
      output.setString( "objectInPromotionNumber", objectInPromotionNumber );
      output.setString( "objectInPromotionType", objectInPromotionType );
      output.setString( "objectInPromotionVersion", objectInPromotionVersion );
      output.setString( "oid", oid );
      output.setString( "partIteration", partIteration );
      output.setString( "partNumber", partNumber );
      output.setString( "partVersion", partVersion );
      output.setString( "relationObjectNumber", relationObjectNumber );
      output.setString( "relationObjectType", relationObjectType );
      output.setString( "rootPath", rootPath );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      fileName = input.getString( "fileName" );
      objectInPromotionIteration = input.getString( "objectInPromotionIteration" );
      objectInPromotionNumber = input.getString( "objectInPromotionNumber" );
      objectInPromotionType = input.getString( "objectInPromotionType" );
      objectInPromotionVersion = input.getString( "objectInPromotionVersion" );
      oid = input.getString( "oid" );
      partIteration = input.getString( "partIteration" );
      partNumber = input.getString( "partNumber" );
      partVersion = input.getString( "partVersion" );
      relationObjectNumber = input.getString( "relationObjectNumber" );
      relationObjectType = input.getString( "relationObjectType" );
      rootPath = input.getString( "rootPath" );
   }

   boolean readVersion_2358330356420915985L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      fileName = (java.lang.String) input.readObject();
      objectInPromotionIteration = (java.lang.String) input.readObject();
      objectInPromotionNumber = (java.lang.String) input.readObject();
      objectInPromotionType = (java.lang.String) input.readObject();
      objectInPromotionVersion = (java.lang.String) input.readObject();
      oid = (java.lang.String) input.readObject();
      partIteration = (java.lang.String) input.readObject();
      partNumber = (java.lang.String) input.readObject();
      partVersion = (java.lang.String) input.readObject();
      relationObjectNumber = (java.lang.String) input.readObject();
      relationObjectType = (java.lang.String) input.readObject();
      rootPath = (java.lang.String) input.readObject();
      return true;
   }

   protected boolean readVersion( DrawingSendERPLog thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion_2358330356420915985L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_DrawingSendERPLog( _DrawingSendERPLog thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      throw new java.io.InvalidClassException(CLASSNAME, "Local class not compatible: stream classdesc externalizationVersionUID="+readSerialVersionUID+" local class externalizationVersionUID="+EXTERNALIZATION_VERSION_UID);
   }
}
