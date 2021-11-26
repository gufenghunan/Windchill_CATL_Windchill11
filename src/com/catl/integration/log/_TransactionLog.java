package com.catl.integration.log;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _TransactionLog extends wt.fc.WTObject implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final java.lang.String RESOURCE = "com.catl.integration.log.logResource";
   static final java.lang.String CLASSNAME = TransactionLog.class.getName();

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String ACTION = "action";
   static int ACTION_UPPER_LIMIT = -1;
   java.lang.String action;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getAction() {
      return action;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setAction(java.lang.String action) throws wt.util.WTPropertyVetoException {
      actionValidate(action);
      this.action = action;
   }
   void actionValidate(java.lang.String action) throws wt.util.WTPropertyVetoException {
      if (ACTION_UPPER_LIMIT < 1) {
         try { ACTION_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("action").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { ACTION_UPPER_LIMIT = 200; }
      }
      if (action != null && !wt.fc.PersistenceHelper.checkStoredLength(action.toString(), ACTION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "action"), java.lang.String.valueOf(java.lang.Math.min(ACTION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "action", this.action, action));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String PART_NUMBER = "partNumber";
   static int PART_NUMBER_UPPER_LIMIT = -1;
   java.lang.String partNumber;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getPartNumber() {
      return partNumber;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
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
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String CHILD_PART_NUMBER = "childPartNumber";
   static int CHILD_PART_NUMBER_UPPER_LIMIT = -1;
   java.lang.String childPartNumber;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getChildPartNumber() {
      return childPartNumber;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setChildPartNumber(java.lang.String childPartNumber) throws wt.util.WTPropertyVetoException {
      childPartNumberValidate(childPartNumber);
      this.childPartNumber = childPartNumber;
   }
   void childPartNumberValidate(java.lang.String childPartNumber) throws wt.util.WTPropertyVetoException {
      if (CHILD_PART_NUMBER_UPPER_LIMIT < 1) {
         try { CHILD_PART_NUMBER_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("childPartNumber").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CHILD_PART_NUMBER_UPPER_LIMIT = 200; }
      }
      if (childPartNumber != null && !wt.fc.PersistenceHelper.checkStoredLength(childPartNumber.toString(), CHILD_PART_NUMBER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "childPartNumber"), java.lang.String.valueOf(java.lang.Math.min(CHILD_PART_NUMBER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "childPartNumber", this.childPartNumber, childPartNumber));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String SUBSTITUTE_PART_NUMBER = "substitutePartNumber";
   static int SUBSTITUTE_PART_NUMBER_UPPER_LIMIT = -1;
   java.lang.String substitutePartNumber;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getSubstitutePartNumber() {
      return substitutePartNumber;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setSubstitutePartNumber(java.lang.String substitutePartNumber) throws wt.util.WTPropertyVetoException {
      substitutePartNumberValidate(substitutePartNumber);
      this.substitutePartNumber = substitutePartNumber;
   }
   void substitutePartNumberValidate(java.lang.String substitutePartNumber) throws wt.util.WTPropertyVetoException {
      if (SUBSTITUTE_PART_NUMBER_UPPER_LIMIT < 1) {
         try { SUBSTITUTE_PART_NUMBER_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("substitutePartNumber").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { SUBSTITUTE_PART_NUMBER_UPPER_LIMIT = 200; }
      }
      if (substitutePartNumber != null && !wt.fc.PersistenceHelper.checkStoredLength(substitutePartNumber.toString(), SUBSTITUTE_PART_NUMBER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "substitutePartNumber"), java.lang.String.valueOf(java.lang.Math.min(SUBSTITUTE_PART_NUMBER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "substitutePartNumber", this.substitutePartNumber, substitutePartNumber));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String ECN_NUMBER = "ecnNumber";
   static int ECN_NUMBER_UPPER_LIMIT = -1;
   java.lang.String ecnNumber;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getEcnNumber() {
      return ecnNumber;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setEcnNumber(java.lang.String ecnNumber) throws wt.util.WTPropertyVetoException {
      ecnNumberValidate(ecnNumber);
      this.ecnNumber = ecnNumber;
   }
   void ecnNumberValidate(java.lang.String ecnNumber) throws wt.util.WTPropertyVetoException {
      if (ECN_NUMBER_UPPER_LIMIT < 1) {
         try { ECN_NUMBER_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("ecnNumber").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { ECN_NUMBER_UPPER_LIMIT = 200; }
      }
      if (ecnNumber != null && !wt.fc.PersistenceHelper.checkStoredLength(ecnNumber.toString(), ECN_NUMBER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "ecnNumber"), java.lang.String.valueOf(java.lang.Math.min(ECN_NUMBER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "ecnNumber", this.ecnNumber, ecnNumber));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String DRAWING_NUMBER = "drawingNumber";
   static int DRAWING_NUMBER_UPPER_LIMIT = -1;
   java.lang.String drawingNumber;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getDrawingNumber() {
      return drawingNumber;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setDrawingNumber(java.lang.String drawingNumber) throws wt.util.WTPropertyVetoException {
      drawingNumberValidate(drawingNumber);
      this.drawingNumber = drawingNumber;
   }
   void drawingNumberValidate(java.lang.String drawingNumber) throws wt.util.WTPropertyVetoException {
      if (DRAWING_NUMBER_UPPER_LIMIT < 1) {
         try { DRAWING_NUMBER_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("drawingNumber").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { DRAWING_NUMBER_UPPER_LIMIT = 200; }
      }
      if (drawingNumber != null && !wt.fc.PersistenceHelper.checkStoredLength(drawingNumber.toString(), DRAWING_NUMBER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "drawingNumber"), java.lang.String.valueOf(java.lang.Math.min(DRAWING_NUMBER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "drawingNumber", this.drawingNumber, drawingNumber));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String DRAWING_VERSION = "drawingVersion";
   static int DRAWING_VERSION_UPPER_LIMIT = -1;
   java.lang.String drawingVersion;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getDrawingVersion() {
      return drawingVersion;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setDrawingVersion(java.lang.String drawingVersion) throws wt.util.WTPropertyVetoException {
      drawingVersionValidate(drawingVersion);
      this.drawingVersion = drawingVersion;
   }
   void drawingVersionValidate(java.lang.String drawingVersion) throws wt.util.WTPropertyVetoException {
      if (DRAWING_VERSION_UPPER_LIMIT < 1) {
         try { DRAWING_VERSION_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("drawingVersion").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { DRAWING_VERSION_UPPER_LIMIT = 200; }
      }
      if (drawingVersion != null && !wt.fc.PersistenceHelper.checkStoredLength(drawingVersion.toString(), DRAWING_VERSION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "drawingVersion"), java.lang.String.valueOf(java.lang.Math.min(DRAWING_VERSION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "drawingVersion", this.drawingVersion, drawingVersion));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String QUANTITY = "quantity";
   static int QUANTITY_UPPER_LIMIT = -1;
   java.lang.String quantity;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getQuantity() {
      return quantity;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setQuantity(java.lang.String quantity) throws wt.util.WTPropertyVetoException {
      quantityValidate(quantity);
      this.quantity = quantity;
   }
   void quantityValidate(java.lang.String quantity) throws wt.util.WTPropertyVetoException {
      if (QUANTITY_UPPER_LIMIT < 1) {
         try { QUANTITY_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("quantity").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { QUANTITY_UPPER_LIMIT = 200; }
      }
      if (quantity != null && !wt.fc.PersistenceHelper.checkStoredLength(quantity.toString(), QUANTITY_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "quantity"), java.lang.String.valueOf(java.lang.Math.min(QUANTITY_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "quantity", this.quantity, quantity));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String ECN_NAME = "ecnName";
   static int ECN_NAME_UPPER_LIMIT = -1;
   java.lang.String ecnName;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getEcnName() {
      return ecnName;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setEcnName(java.lang.String ecnName) throws wt.util.WTPropertyVetoException {
      ecnNameValidate(ecnName);
      this.ecnName = ecnName;
   }
   void ecnNameValidate(java.lang.String ecnName) throws wt.util.WTPropertyVetoException {
      if (ECN_NAME_UPPER_LIMIT < 1) {
         try { ECN_NAME_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("ecnName").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { ECN_NAME_UPPER_LIMIT = 4000; }
      }
      if (ecnName != null && !wt.fc.PersistenceHelper.checkStoredLength(ecnName.toString(), ECN_NAME_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "ecnName"), java.lang.String.valueOf(java.lang.Math.min(ECN_NAME_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "ecnName", this.ecnName, ecnName));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String DESCRIPTION = "description";
   static int DESCRIPTION_UPPER_LIMIT = -1;
   java.lang.String description;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getDescription() {
      return description;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setDescription(java.lang.String description) throws wt.util.WTPropertyVetoException {
      descriptionValidate(description);
      this.description = description;
   }
   void descriptionValidate(java.lang.String description) throws wt.util.WTPropertyVetoException {
      if (DESCRIPTION_UPPER_LIMIT < 1) {
         try { DESCRIPTION_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("description").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { DESCRIPTION_UPPER_LIMIT = 4000; }
      }
      if (description != null && !wt.fc.PersistenceHelper.checkStoredLength(description.toString(), DESCRIPTION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "description"), java.lang.String.valueOf(java.lang.Math.min(DESCRIPTION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "description", this.description, description));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String VALID_DATE = "validDate";
   static int VALID_DATE_UPPER_LIMIT = -1;
   java.lang.String validDate;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getValidDate() {
      return validDate;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setValidDate(java.lang.String validDate) throws wt.util.WTPropertyVetoException {
      validDateValidate(validDate);
      this.validDate = validDate;
   }
   void validDateValidate(java.lang.String validDate) throws wt.util.WTPropertyVetoException {
      if (VALID_DATE_UPPER_LIMIT < 1) {
         try { VALID_DATE_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("validDate").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { VALID_DATE_UPPER_LIMIT = 200; }
      }
      if (validDate != null && !wt.fc.PersistenceHelper.checkStoredLength(validDate.toString(), VALID_DATE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "validDate"), java.lang.String.valueOf(java.lang.Math.min(VALID_DATE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "validDate", this.validDate, validDate));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String PART_NAME = "partName";
   static int PART_NAME_UPPER_LIMIT = -1;
   java.lang.String partName;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getPartName() {
      return partName;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setPartName(java.lang.String partName) throws wt.util.WTPropertyVetoException {
      partNameValidate(partName);
      this.partName = partName;
   }
   void partNameValidate(java.lang.String partName) throws wt.util.WTPropertyVetoException {
      if (PART_NAME_UPPER_LIMIT < 1) {
         try { PART_NAME_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("partName").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { PART_NAME_UPPER_LIMIT = 200; }
      }
      if (partName != null && !wt.fc.PersistenceHelper.checkStoredLength(partName.toString(), PART_NAME_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "partName"), java.lang.String.valueOf(java.lang.Math.min(PART_NAME_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "partName", this.partName, partName));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String MATERIAL_GROUP = "materialGroup";
   static int MATERIAL_GROUP_UPPER_LIMIT = -1;
   java.lang.String materialGroup;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getMaterialGroup() {
      return materialGroup;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setMaterialGroup(java.lang.String materialGroup) throws wt.util.WTPropertyVetoException {
      materialGroupValidate(materialGroup);
      this.materialGroup = materialGroup;
   }
   void materialGroupValidate(java.lang.String materialGroup) throws wt.util.WTPropertyVetoException {
      if (MATERIAL_GROUP_UPPER_LIMIT < 1) {
         try { MATERIAL_GROUP_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("materialGroup").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { MATERIAL_GROUP_UPPER_LIMIT = 200; }
      }
      if (materialGroup != null && !wt.fc.PersistenceHelper.checkStoredLength(materialGroup.toString(), MATERIAL_GROUP_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "materialGroup"), java.lang.String.valueOf(java.lang.Math.min(MATERIAL_GROUP_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "materialGroup", this.materialGroup, materialGroup));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String DEFAULT_UNIT = "defaultUnit";
   static int DEFAULT_UNIT_UPPER_LIMIT = -1;
   java.lang.String defaultUnit;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getDefaultUnit() {
      return defaultUnit;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setDefaultUnit(java.lang.String defaultUnit) throws wt.util.WTPropertyVetoException {
      defaultUnitValidate(defaultUnit);
      this.defaultUnit = defaultUnit;
   }
   void defaultUnitValidate(java.lang.String defaultUnit) throws wt.util.WTPropertyVetoException {
      if (DEFAULT_UNIT_UPPER_LIMIT < 1) {
         try { DEFAULT_UNIT_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("defaultUnit").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { DEFAULT_UNIT_UPPER_LIMIT = 200; }
      }
      if (defaultUnit != null && !wt.fc.PersistenceHelper.checkStoredLength(defaultUnit.toString(), DEFAULT_UNIT_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "defaultUnit"), java.lang.String.valueOf(java.lang.Math.min(DEFAULT_UNIT_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "defaultUnit", this.defaultUnit, defaultUnit));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String SPECIFICATION = "specification";
   static int SPECIFICATION_UPPER_LIMIT = -1;
   java.lang.String specification;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getSpecification() {
      return specification;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setSpecification(java.lang.String specification) throws wt.util.WTPropertyVetoException {
      specificationValidate(specification);
      this.specification = specification;
   }
   void specificationValidate(java.lang.String specification) throws wt.util.WTPropertyVetoException {
      if (SPECIFICATION_UPPER_LIMIT < 1) {
         try { SPECIFICATION_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("specification").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { SPECIFICATION_UPPER_LIMIT = 1000; }
      }
      if (specification != null && !wt.fc.PersistenceHelper.checkStoredLength(specification.toString(), SPECIFICATION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "specification"), java.lang.String.valueOf(java.lang.Math.min(SPECIFICATION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "specification", this.specification, specification));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String ENGLISH_NAME = "englishName";
   static int ENGLISH_NAME_UPPER_LIMIT = -1;
   java.lang.String englishName;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getEnglishName() {
      return englishName;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setEnglishName(java.lang.String englishName) throws wt.util.WTPropertyVetoException {
      englishNameValidate(englishName);
      this.englishName = englishName;
   }
   void englishNameValidate(java.lang.String englishName) throws wt.util.WTPropertyVetoException {
      if (ENGLISH_NAME_UPPER_LIMIT < 1) {
         try { ENGLISH_NAME_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("englishName").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { ENGLISH_NAME_UPPER_LIMIT = 200; }
      }
      if (englishName != null && !wt.fc.PersistenceHelper.checkStoredLength(englishName.toString(), ENGLISH_NAME_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "englishName"), java.lang.String.valueOf(java.lang.Math.min(ENGLISH_NAME_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "englishName", this.englishName, englishName));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String SOURCE = "source";
   static int SOURCE_UPPER_LIMIT = -1;
   java.lang.String source;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getSource() {
      return source;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setSource(java.lang.String source) throws wt.util.WTPropertyVetoException {
      sourceValidate(source);
      this.source = source;
   }
   void sourceValidate(java.lang.String source) throws wt.util.WTPropertyVetoException {
      if (SOURCE_UPPER_LIMIT < 1) {
         try { SOURCE_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("source").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { SOURCE_UPPER_LIMIT = 200; }
      }
      if (source != null && !wt.fc.PersistenceHelper.checkStoredLength(source.toString(), SOURCE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "source"), java.lang.String.valueOf(java.lang.Math.min(SOURCE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "source", this.source, source));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String CREATOR = "creator";
   static int CREATOR_UPPER_LIMIT = -1;
   java.lang.String creator;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getCreator() {
      return creator;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setCreator(java.lang.String creator) throws wt.util.WTPropertyVetoException {
      creatorValidate(creator);
      this.creator = creator;
   }
   void creatorValidate(java.lang.String creator) throws wt.util.WTPropertyVetoException {
      if (CREATOR_UPPER_LIMIT < 1) {
         try { CREATOR_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("creator").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CREATOR_UPPER_LIMIT = 200; }
      }
      if (creator != null && !wt.fc.PersistenceHelper.checkStoredLength(creator.toString(), CREATOR_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "creator"), java.lang.String.valueOf(java.lang.Math.min(CREATOR_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "creator", this.creator, creator));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String STANDARD_VOLTAGE = "standardVoltage";
   static int STANDARD_VOLTAGE_UPPER_LIMIT = -1;
   java.lang.String standardVoltage;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getStandardVoltage() {
      return standardVoltage;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setStandardVoltage(java.lang.String standardVoltage) throws wt.util.WTPropertyVetoException {
      standardVoltageValidate(standardVoltage);
      this.standardVoltage = standardVoltage;
   }
   void standardVoltageValidate(java.lang.String standardVoltage) throws wt.util.WTPropertyVetoException {
      if (STANDARD_VOLTAGE_UPPER_LIMIT < 1) {
         try { STANDARD_VOLTAGE_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("standardVoltage").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { STANDARD_VOLTAGE_UPPER_LIMIT = 200; }
      }
      if (standardVoltage != null && !wt.fc.PersistenceHelper.checkStoredLength(standardVoltage.toString(), STANDARD_VOLTAGE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "standardVoltage"), java.lang.String.valueOf(java.lang.Math.min(STANDARD_VOLTAGE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "standardVoltage", this.standardVoltage, standardVoltage));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String PRODUCT_ENERGY = "productEnergy";
   static int PRODUCT_ENERGY_UPPER_LIMIT = -1;
   java.lang.String productEnergy;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getProductEnergy() {
      return productEnergy;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setProductEnergy(java.lang.String productEnergy) throws wt.util.WTPropertyVetoException {
      productEnergyValidate(productEnergy);
      this.productEnergy = productEnergy;
   }
   void productEnergyValidate(java.lang.String productEnergy) throws wt.util.WTPropertyVetoException {
      if (PRODUCT_ENERGY_UPPER_LIMIT < 1) {
         try { PRODUCT_ENERGY_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("productEnergy").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { PRODUCT_ENERGY_UPPER_LIMIT = 200; }
      }
      if (productEnergy != null && !wt.fc.PersistenceHelper.checkStoredLength(productEnergy.toString(), PRODUCT_ENERGY_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "productEnergy"), java.lang.String.valueOf(java.lang.Math.min(PRODUCT_ENERGY_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "productEnergy", this.productEnergy, productEnergy));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String CELL_VOLUME = "cellVolume";
   static int CELL_VOLUME_UPPER_LIMIT = -1;
   java.lang.String cellVolume;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getCellVolume() {
      return cellVolume;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setCellVolume(java.lang.String cellVolume) throws wt.util.WTPropertyVetoException {
      cellVolumeValidate(cellVolume);
      this.cellVolume = cellVolume;
   }
   void cellVolumeValidate(java.lang.String cellVolume) throws wt.util.WTPropertyVetoException {
      if (CELL_VOLUME_UPPER_LIMIT < 1) {
         try { CELL_VOLUME_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("cellVolume").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CELL_VOLUME_UPPER_LIMIT = 200; }
      }
      if (cellVolume != null && !wt.fc.PersistenceHelper.checkStoredLength(cellVolume.toString(), CELL_VOLUME_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "cellVolume"), java.lang.String.valueOf(java.lang.Math.min(CELL_VOLUME_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "cellVolume", this.cellVolume, cellVolume));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String FULL_VOLTAGE = "fullVoltage";
   static int FULL_VOLTAGE_UPPER_LIMIT = -1;
   java.lang.String fullVoltage;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getFullVoltage() {
      return fullVoltage;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setFullVoltage(java.lang.String fullVoltage) throws wt.util.WTPropertyVetoException {
      fullVoltageValidate(fullVoltage);
      this.fullVoltage = fullVoltage;
   }
   void fullVoltageValidate(java.lang.String fullVoltage) throws wt.util.WTPropertyVetoException {
      if (FULL_VOLTAGE_UPPER_LIMIT < 1) {
         try { FULL_VOLTAGE_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("fullVoltage").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { FULL_VOLTAGE_UPPER_LIMIT = 200; }
      }
      if (fullVoltage != null && !wt.fc.PersistenceHelper.checkStoredLength(fullVoltage.toString(), FULL_VOLTAGE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "fullVoltage"), java.lang.String.valueOf(java.lang.Math.min(FULL_VOLTAGE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "fullVoltage", this.fullVoltage, fullVoltage));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String MODEL = "model";
   static int MODEL_UPPER_LIMIT = -1;
   java.lang.String model;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getModel() {
      return model;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setModel(java.lang.String model) throws wt.util.WTPropertyVetoException {
      modelValidate(model);
      this.model = model;
   }
   void modelValidate(java.lang.String model) throws wt.util.WTPropertyVetoException {
      if (MODEL_UPPER_LIMIT < 1) {
         try { MODEL_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("model").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { MODEL_UPPER_LIMIT = 200; }
      }
      if (model != null && !wt.fc.PersistenceHelper.checkStoredLength(model.toString(), MODEL_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "model"), java.lang.String.valueOf(java.lang.Math.min(MODEL_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "model", this.model, model));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String VERSION_BIG = "versionBig";
   static int VERSION_BIG_UPPER_LIMIT = -1;
   java.lang.String versionBig;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getVersionBig() {
      return versionBig;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setVersionBig(java.lang.String versionBig) throws wt.util.WTPropertyVetoException {
      versionBigValidate(versionBig);
      this.versionBig = versionBig;
   }
   void versionBigValidate(java.lang.String versionBig) throws wt.util.WTPropertyVetoException {
      if (VERSION_BIG_UPPER_LIMIT < 1) {
         try { VERSION_BIG_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("versionBig").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { VERSION_BIG_UPPER_LIMIT = 200; }
      }
      if (versionBig != null && !wt.fc.PersistenceHelper.checkStoredLength(versionBig.toString(), VERSION_BIG_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "versionBig"), java.lang.String.valueOf(java.lang.Math.min(VERSION_BIG_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "versionBig", this.versionBig, versionBig));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String VERSION_SMALL = "versionSmall";
   static int VERSION_SMALL_UPPER_LIMIT = -1;
   java.lang.String versionSmall;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getVersionSmall() {
      return versionSmall;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setVersionSmall(java.lang.String versionSmall) throws wt.util.WTPropertyVetoException {
      versionSmallValidate(versionSmall);
      this.versionSmall = versionSmall;
   }
   void versionSmallValidate(java.lang.String versionSmall) throws wt.util.WTPropertyVetoException {
      if (VERSION_SMALL_UPPER_LIMIT < 1) {
         try { VERSION_SMALL_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("versionSmall").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { VERSION_SMALL_UPPER_LIMIT = 200; }
      }
      if (versionSmall != null && !wt.fc.PersistenceHelper.checkStoredLength(versionSmall.toString(), VERSION_SMALL_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "versionSmall"), java.lang.String.valueOf(java.lang.Math.min(VERSION_SMALL_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "versionSmall", this.versionSmall, versionSmall));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String OID = "oid";
   static int OID_UPPER_LIMIT = -1;
   java.lang.String oid;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getOid() {
      return oid;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
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
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String OLD_PART_NUMBER = "oldPartNumber";
   static int OLD_PART_NUMBER_UPPER_LIMIT = -1;
   java.lang.String oldPartNumber;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getOldPartNumber() {
      return oldPartNumber;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setOldPartNumber(java.lang.String oldPartNumber) throws wt.util.WTPropertyVetoException {
      oldPartNumberValidate(oldPartNumber);
      this.oldPartNumber = oldPartNumber;
   }
   void oldPartNumberValidate(java.lang.String oldPartNumber) throws wt.util.WTPropertyVetoException {
      if (OLD_PART_NUMBER_UPPER_LIMIT < 1) {
         try { OLD_PART_NUMBER_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("oldPartNumber").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OLD_PART_NUMBER_UPPER_LIMIT = 200; }
      }
      if (oldPartNumber != null && !wt.fc.PersistenceHelper.checkStoredLength(oldPartNumber.toString(), OLD_PART_NUMBER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "oldPartNumber"), java.lang.String.valueOf(java.lang.Math.min(OLD_PART_NUMBER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "oldPartNumber", this.oldPartNumber, oldPartNumber));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String CELL_MODE = "cellMode";
   static int CELL_MODE_UPPER_LIMIT = -1;
   java.lang.String cellMode;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getCellMode() {
      return cellMode;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setCellMode(java.lang.String cellMode) throws wt.util.WTPropertyVetoException {
      cellModeValidate(cellMode);
      this.cellMode = cellMode;
   }
   void cellModeValidate(java.lang.String cellMode) throws wt.util.WTPropertyVetoException {
      if (CELL_MODE_UPPER_LIMIT < 1) {
         try { CELL_MODE_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("cellMode").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CELL_MODE_UPPER_LIMIT = 200; }
      }
      if (cellMode != null && !wt.fc.PersistenceHelper.checkStoredLength(cellMode.toString(), CELL_MODE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "cellMode"), java.lang.String.valueOf(java.lang.Math.min(CELL_MODE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "cellMode", this.cellMode, cellMode));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String STR1 = "str1";
   static int STR1_UPPER_LIMIT = -1;
   java.lang.String str1;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getStr1() {
      return str1;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setStr1(java.lang.String str1) throws wt.util.WTPropertyVetoException {
      str1Validate(str1);
      this.str1 = str1;
   }
   void str1Validate(java.lang.String str1) throws wt.util.WTPropertyVetoException {
      if (STR1_UPPER_LIMIT < 1) {
         try { STR1_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("str1").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { STR1_UPPER_LIMIT = 200; }
      }
      if (str1 != null && !wt.fc.PersistenceHelper.checkStoredLength(str1.toString(), STR1_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "str1"), java.lang.String.valueOf(java.lang.Math.min(STR1_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "str1", this.str1, str1));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String STR2 = "str2";
   static int STR2_UPPER_LIMIT = -1;
   java.lang.String str2;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getStr2() {
      return str2;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setStr2(java.lang.String str2) throws wt.util.WTPropertyVetoException {
      str2Validate(str2);
      this.str2 = str2;
   }
   void str2Validate(java.lang.String str2) throws wt.util.WTPropertyVetoException {
      if (STR2_UPPER_LIMIT < 1) {
         try { STR2_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("str2").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { STR2_UPPER_LIMIT = 200; }
      }
      if (str2 != null && !wt.fc.PersistenceHelper.checkStoredLength(str2.toString(), STR2_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "str2"), java.lang.String.valueOf(java.lang.Math.min(STR2_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "str2", this.str2, str2));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String STR3 = "str3";
   static int STR3_UPPER_LIMIT = -1;
   java.lang.String str3;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getStr3() {
      return str3;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setStr3(java.lang.String str3) throws wt.util.WTPropertyVetoException {
      str3Validate(str3);
      this.str3 = str3;
   }
   void str3Validate(java.lang.String str3) throws wt.util.WTPropertyVetoException {
      if (STR3_UPPER_LIMIT < 1) {
         try { STR3_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("str3").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { STR3_UPPER_LIMIT = 200; }
      }
      if (str3 != null && !wt.fc.PersistenceHelper.checkStoredLength(str3.toString(), STR3_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "str3"), java.lang.String.valueOf(java.lang.Math.min(STR3_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "str3", this.str3, str3));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String STR4 = "str4";
   static int STR4_UPPER_LIMIT = -1;
   java.lang.String str4;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getStr4() {
      return str4;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setStr4(java.lang.String str4) throws wt.util.WTPropertyVetoException {
      str4Validate(str4);
      this.str4 = str4;
   }
   void str4Validate(java.lang.String str4) throws wt.util.WTPropertyVetoException {
      if (STR4_UPPER_LIMIT < 1) {
         try { STR4_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("str4").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { STR4_UPPER_LIMIT = 200; }
      }
      if (str4 != null && !wt.fc.PersistenceHelper.checkStoredLength(str4.toString(), STR4_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "str4"), java.lang.String.valueOf(java.lang.Math.min(STR4_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "str4", this.str4, str4));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String STR5 = "str5";
   static int STR5_UPPER_LIMIT = -1;
   java.lang.String str5;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getStr5() {
      return str5;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setStr5(java.lang.String str5) throws wt.util.WTPropertyVetoException {
      str5Validate(str5);
      this.str5 = str5;
   }
   void str5Validate(java.lang.String str5) throws wt.util.WTPropertyVetoException {
      if (STR5_UPPER_LIMIT < 1) {
         try { STR5_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("str5").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { STR5_UPPER_LIMIT = 200; }
      }
      if (str5 != null && !wt.fc.PersistenceHelper.checkStoredLength(str5.toString(), STR5_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "str5"), java.lang.String.valueOf(java.lang.Math.min(STR5_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "str5", this.str5, str5));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String STR6 = "str6";
   static int STR6_UPPER_LIMIT = -1;
   java.lang.String str6;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getStr6() {
      return str6;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setStr6(java.lang.String str6) throws wt.util.WTPropertyVetoException {
      str6Validate(str6);
      this.str6 = str6;
   }
   void str6Validate(java.lang.String str6) throws wt.util.WTPropertyVetoException {
      if (STR6_UPPER_LIMIT < 1) {
         try { STR6_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("str6").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { STR6_UPPER_LIMIT = 200; }
      }
      if (str6 != null && !wt.fc.PersistenceHelper.checkStoredLength(str6.toString(), STR6_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "str6"), java.lang.String.valueOf(java.lang.Math.min(STR6_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "str6", this.str6, str6));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String STR7 = "str7";
   static int STR7_UPPER_LIMIT = -1;
   java.lang.String str7;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getStr7() {
      return str7;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setStr7(java.lang.String str7) throws wt.util.WTPropertyVetoException {
      str7Validate(str7);
      this.str7 = str7;
   }
   void str7Validate(java.lang.String str7) throws wt.util.WTPropertyVetoException {
      if (STR7_UPPER_LIMIT < 1) {
         try { STR7_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("str7").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { STR7_UPPER_LIMIT = 200; }
      }
      if (str7 != null && !wt.fc.PersistenceHelper.checkStoredLength(str7.toString(), STR7_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "str7"), java.lang.String.valueOf(java.lang.Math.min(STR7_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "str7", this.str7, str7));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String STR8 = "str8";
   static int STR8_UPPER_LIMIT = -1;
   java.lang.String str8;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getStr8() {
      return str8;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setStr8(java.lang.String str8) throws wt.util.WTPropertyVetoException {
      str8Validate(str8);
      this.str8 = str8;
   }
   void str8Validate(java.lang.String str8) throws wt.util.WTPropertyVetoException {
      if (STR8_UPPER_LIMIT < 1) {
         try { STR8_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("str8").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { STR8_UPPER_LIMIT = 200; }
      }
      if (str8 != null && !wt.fc.PersistenceHelper.checkStoredLength(str8.toString(), STR8_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "str8"), java.lang.String.valueOf(java.lang.Math.min(STR8_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "str8", this.str8, str8));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String STR9 = "str9";
   static int STR9_UPPER_LIMIT = -1;
   java.lang.String str9;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getStr9() {
      return str9;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setStr9(java.lang.String str9) throws wt.util.WTPropertyVetoException {
      str9Validate(str9);
      this.str9 = str9;
   }
   void str9Validate(java.lang.String str9) throws wt.util.WTPropertyVetoException {
      if (STR9_UPPER_LIMIT < 1) {
         try { STR9_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("str9").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { STR9_UPPER_LIMIT = 200; }
      }
      if (str9 != null && !wt.fc.PersistenceHelper.checkStoredLength(str9.toString(), STR9_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "str9"), java.lang.String.valueOf(java.lang.Math.min(STR9_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "str9", this.str9, str9));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String STR10 = "str10";
   static int STR10_UPPER_LIMIT = -1;
   java.lang.String str10;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getStr10() {
      return str10;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setStr10(java.lang.String str10) throws wt.util.WTPropertyVetoException {
      str10Validate(str10);
      this.str10 = str10;
   }
   void str10Validate(java.lang.String str10) throws wt.util.WTPropertyVetoException {
      if (STR10_UPPER_LIMIT < 1) {
         try { STR10_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("str10").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { STR10_UPPER_LIMIT = 200; }
      }
      if (str10 != null && !wt.fc.PersistenceHelper.checkStoredLength(str10.toString(), STR10_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "str10"), java.lang.String.valueOf(java.lang.Math.min(STR10_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "str10", this.str10, str10));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String STR11 = "str11";
   static int STR11_UPPER_LIMIT = -1;
   java.lang.String str11;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getStr11() {
      return str11;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setStr11(java.lang.String str11) throws wt.util.WTPropertyVetoException {
      str11Validate(str11);
      this.str11 = str11;
   }
   void str11Validate(java.lang.String str11) throws wt.util.WTPropertyVetoException {
      if (STR11_UPPER_LIMIT < 1) {
         try { STR11_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("str11").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { STR11_UPPER_LIMIT = 200; }
      }
      if (str11 != null && !wt.fc.PersistenceHelper.checkStoredLength(str11.toString(), STR11_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "str11"), java.lang.String.valueOf(java.lang.Math.min(STR11_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "str11", this.str11, str11));
   }

   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public static final java.lang.String STR12 = "str12";
   static int STR12_UPPER_LIMIT = -1;
   java.lang.String str12;
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public java.lang.String getStr12() {
      return str12;
   }
   /**
    * @see com.catl.integration.log.TransactionLog
    */
   public void setStr12(java.lang.String str12) throws wt.util.WTPropertyVetoException {
      str12Validate(str12);
      this.str12 = str12;
   }
   void str12Validate(java.lang.String str12) throws wt.util.WTPropertyVetoException {
      if (STR12_UPPER_LIMIT < 1) {
         try { STR12_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("str12").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { STR12_UPPER_LIMIT = 200; }
      }
      if (str12 != null && !wt.fc.PersistenceHelper.checkStoredLength(str12.toString(), STR12_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "str12"), java.lang.String.valueOf(java.lang.Math.min(STR12_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "str12", this.str12, str12));
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

   public static final long EXTERNALIZATION_VERSION_UID = -2634963502532591912L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( action );
      output.writeObject( cellMode );
      output.writeObject( cellVolume );
      output.writeObject( childPartNumber );
      output.writeObject( creator );
      output.writeObject( defaultUnit );
      output.writeObject( description );
      output.writeObject( drawingNumber );
      output.writeObject( drawingVersion );
      output.writeObject( ecnName );
      output.writeObject( ecnNumber );
      output.writeObject( englishName );
      output.writeObject( fullVoltage );
      output.writeObject( materialGroup );
      output.writeObject( model );
      output.writeObject( oid );
      output.writeObject( oldPartNumber );
      output.writeObject( partName );
      output.writeObject( partNumber );
      output.writeObject( productEnergy );
      output.writeObject( quantity );
      output.writeObject( source );
      output.writeObject( specification );
      output.writeObject( standardVoltage );
      output.writeObject( str1 );
      output.writeObject( str10 );
      output.writeObject( str11 );
      output.writeObject( str12 );
      output.writeObject( str2 );
      output.writeObject( str3 );
      output.writeObject( str4 );
      output.writeObject( str5 );
      output.writeObject( str6 );
      output.writeObject( str7 );
      output.writeObject( str8 );
      output.writeObject( str9 );
      output.writeObject( substitutePartNumber );
      output.writeObject( validDate );
      output.writeObject( versionBig );
      output.writeObject( versionSmall );
   }

   protected void super_writeExternal_TransactionLog(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (com.catl.integration.log.TransactionLog) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_TransactionLog(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "action", action );
      output.setString( "cellMode", cellMode );
      output.setString( "cellVolume", cellVolume );
      output.setString( "childPartNumber", childPartNumber );
      output.setString( "creator", creator );
      output.setString( "defaultUnit", defaultUnit );
      output.setString( "description", description );
      output.setString( "drawingNumber", drawingNumber );
      output.setString( "drawingVersion", drawingVersion );
      output.setString( "ecnName", ecnName );
      output.setString( "ecnNumber", ecnNumber );
      output.setString( "englishName", englishName );
      output.setString( "fullVoltage", fullVoltage );
      output.setString( "materialGroup", materialGroup );
      output.setString( "model", model );
      output.setString( "oid", oid );
      output.setString( "oldPartNumber", oldPartNumber );
      output.setString( "partName", partName );
      output.setString( "partNumber", partNumber );
      output.setString( "productEnergy", productEnergy );
      output.setString( "quantity", quantity );
      output.setString( "source", source );
      output.setString( "specification", specification );
      output.setString( "standardVoltage", standardVoltage );
      output.setString( "str1", str1 );
      output.setString( "str10", str10 );
      output.setString( "str11", str11 );
      output.setString( "str12", str12 );
      output.setString( "str2", str2 );
      output.setString( "str3", str3 );
      output.setString( "str4", str4 );
      output.setString( "str5", str5 );
      output.setString( "str6", str6 );
      output.setString( "str7", str7 );
      output.setString( "str8", str8 );
      output.setString( "str9", str9 );
      output.setString( "substitutePartNumber", substitutePartNumber );
      output.setString( "validDate", validDate );
      output.setString( "versionBig", versionBig );
      output.setString( "versionSmall", versionSmall );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      action = input.getString( "action" );
      cellMode = input.getString( "cellMode" );
      cellVolume = input.getString( "cellVolume" );
      childPartNumber = input.getString( "childPartNumber" );
      creator = input.getString( "creator" );
      defaultUnit = input.getString( "defaultUnit" );
      description = input.getString( "description" );
      drawingNumber = input.getString( "drawingNumber" );
      drawingVersion = input.getString( "drawingVersion" );
      ecnName = input.getString( "ecnName" );
      ecnNumber = input.getString( "ecnNumber" );
      englishName = input.getString( "englishName" );
      fullVoltage = input.getString( "fullVoltage" );
      materialGroup = input.getString( "materialGroup" );
      model = input.getString( "model" );
      oid = input.getString( "oid" );
      oldPartNumber = input.getString( "oldPartNumber" );
      partName = input.getString( "partName" );
      partNumber = input.getString( "partNumber" );
      productEnergy = input.getString( "productEnergy" );
      quantity = input.getString( "quantity" );
      source = input.getString( "source" );
      specification = input.getString( "specification" );
      standardVoltage = input.getString( "standardVoltage" );
      str1 = input.getString( "str1" );
      str10 = input.getString( "str10" );
      str11 = input.getString( "str11" );
      str12 = input.getString( "str12" );
      str2 = input.getString( "str2" );
      str3 = input.getString( "str3" );
      str4 = input.getString( "str4" );
      str5 = input.getString( "str5" );
      str6 = input.getString( "str6" );
      str7 = input.getString( "str7" );
      str8 = input.getString( "str8" );
      str9 = input.getString( "str9" );
      substitutePartNumber = input.getString( "substitutePartNumber" );
      validDate = input.getString( "validDate" );
      versionBig = input.getString( "versionBig" );
      versionSmall = input.getString( "versionSmall" );
   }

   boolean readVersion_2634963502532591912L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      action = (java.lang.String) input.readObject();
      cellMode = (java.lang.String) input.readObject();
      cellVolume = (java.lang.String) input.readObject();
      childPartNumber = (java.lang.String) input.readObject();
      creator = (java.lang.String) input.readObject();
      defaultUnit = (java.lang.String) input.readObject();
      description = (java.lang.String) input.readObject();
      drawingNumber = (java.lang.String) input.readObject();
      drawingVersion = (java.lang.String) input.readObject();
      ecnName = (java.lang.String) input.readObject();
      ecnNumber = (java.lang.String) input.readObject();
      englishName = (java.lang.String) input.readObject();
      fullVoltage = (java.lang.String) input.readObject();
      materialGroup = (java.lang.String) input.readObject();
      model = (java.lang.String) input.readObject();
      oid = (java.lang.String) input.readObject();
      oldPartNumber = (java.lang.String) input.readObject();
      partName = (java.lang.String) input.readObject();
      partNumber = (java.lang.String) input.readObject();
      productEnergy = (java.lang.String) input.readObject();
      quantity = (java.lang.String) input.readObject();
      source = (java.lang.String) input.readObject();
      specification = (java.lang.String) input.readObject();
      standardVoltage = (java.lang.String) input.readObject();
      str1 = (java.lang.String) input.readObject();
      str10 = (java.lang.String) input.readObject();
      str11 = (java.lang.String) input.readObject();
      str12 = (java.lang.String) input.readObject();
      str2 = (java.lang.String) input.readObject();
      str3 = (java.lang.String) input.readObject();
      str4 = (java.lang.String) input.readObject();
      str5 = (java.lang.String) input.readObject();
      str6 = (java.lang.String) input.readObject();
      str7 = (java.lang.String) input.readObject();
      str8 = (java.lang.String) input.readObject();
      str9 = (java.lang.String) input.readObject();
      substitutePartNumber = (java.lang.String) input.readObject();
      validDate = (java.lang.String) input.readObject();
      versionBig = (java.lang.String) input.readObject();
      versionSmall = (java.lang.String) input.readObject();
      return true;
   }

   protected boolean readVersion( TransactionLog thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion_2634963502532591912L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_TransactionLog( _TransactionLog thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      throw new java.io.InvalidClassException(CLASSNAME, "Local class not compatible: stream classdesc externalizationVersionUID="+readSerialVersionUID+" local class externalizationVersionUID="+EXTERNALIZATION_VERSION_UID);
   }
}
