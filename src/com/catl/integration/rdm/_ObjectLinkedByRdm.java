package com.catl.integration.rdm;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _ObjectLinkedByRdm extends wt.fc.WTObject implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final java.lang.String RESOURCE = "com.catl.integration.rdm.rdmResource";
   static final java.lang.String CLASSNAME = ObjectLinkedByRdm.class.getName();

   /**
    * @see com.catl.integration.rdm.ObjectLinkedByRdm
    */
   public static final java.lang.String OBJECT_TYPE = "objectType";
   static int OBJECT_TYPE_UPPER_LIMIT = -1;
   java.lang.String objectType;
   /**
    * @see com.catl.integration.rdm.ObjectLinkedByRdm
    */
   public java.lang.String getObjectType() {
      return objectType;
   }
   /**
    * @see com.catl.integration.rdm.ObjectLinkedByRdm
    */
   public void setObjectType(java.lang.String objectType) throws wt.util.WTPropertyVetoException {
      objectTypeValidate(objectType);
      this.objectType = objectType;
   }
   void objectTypeValidate(java.lang.String objectType) throws wt.util.WTPropertyVetoException {
      if (OBJECT_TYPE_UPPER_LIMIT < 1) {
         try { OBJECT_TYPE_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("objectType").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OBJECT_TYPE_UPPER_LIMIT = 200; }
      }
      if (objectType != null && !wt.fc.PersistenceHelper.checkStoredLength(objectType.toString(), OBJECT_TYPE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "objectType"), java.lang.String.valueOf(java.lang.Math.min(OBJECT_TYPE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "objectType", this.objectType, objectType));
   }

   /**
    * @see com.catl.integration.rdm.ObjectLinkedByRdm
    */
   public static final java.lang.String OBJECT_NUMBER = "objectNumber";
   static int OBJECT_NUMBER_UPPER_LIMIT = -1;
   java.lang.String objectNumber;
   /**
    * @see com.catl.integration.rdm.ObjectLinkedByRdm
    */
   public java.lang.String getObjectNumber() {
      return objectNumber;
   }
   /**
    * @see com.catl.integration.rdm.ObjectLinkedByRdm
    */
   public void setObjectNumber(java.lang.String objectNumber) throws wt.util.WTPropertyVetoException {
      objectNumberValidate(objectNumber);
      this.objectNumber = objectNumber;
   }
   void objectNumberValidate(java.lang.String objectNumber) throws wt.util.WTPropertyVetoException {
      if (OBJECT_NUMBER_UPPER_LIMIT < 1) {
         try { OBJECT_NUMBER_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("objectNumber").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OBJECT_NUMBER_UPPER_LIMIT = 200; }
      }
      if (objectNumber != null && !wt.fc.PersistenceHelper.checkStoredLength(objectNumber.toString(), OBJECT_NUMBER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "objectNumber"), java.lang.String.valueOf(java.lang.Math.min(OBJECT_NUMBER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "objectNumber", this.objectNumber, objectNumber));
   }

   /**
    * @see com.catl.integration.rdm.ObjectLinkedByRdm
    */
   public static final java.lang.String BRANCH_ID = "branchId";
   long branchId;
   /**
    * @see com.catl.integration.rdm.ObjectLinkedByRdm
    */
   public long getBranchId() {
      return branchId;
   }
   /**
    * @see com.catl.integration.rdm.ObjectLinkedByRdm
    */
   public void setBranchId(long branchId) throws wt.util.WTPropertyVetoException {
      branchIdValidate(branchId);
      this.branchId = branchId;
   }
   void branchIdValidate(long branchId) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.integration.rdm.ObjectLinkedByRdm
    */
   public static final java.lang.String DELIVERABLE_ID = "deliverableId";
   static int DELIVERABLE_ID_UPPER_LIMIT = -1;
   java.lang.String deliverableId;
   /**
    * @see com.catl.integration.rdm.ObjectLinkedByRdm
    */
   public java.lang.String getDeliverableId() {
      return deliverableId;
   }
   /**
    * @see com.catl.integration.rdm.ObjectLinkedByRdm
    */
   public void setDeliverableId(java.lang.String deliverableId) throws wt.util.WTPropertyVetoException {
      deliverableIdValidate(deliverableId);
      this.deliverableId = deliverableId;
   }
   void deliverableIdValidate(java.lang.String deliverableId) throws wt.util.WTPropertyVetoException {
      if (DELIVERABLE_ID_UPPER_LIMIT < 1) {
         try { DELIVERABLE_ID_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("deliverableId").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { DELIVERABLE_ID_UPPER_LIMIT = 200; }
      }
      if (deliverableId != null && !wt.fc.PersistenceHelper.checkStoredLength(deliverableId.toString(), DELIVERABLE_ID_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "deliverableId"), java.lang.String.valueOf(java.lang.Math.min(DELIVERABLE_ID_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "deliverableId", this.deliverableId, deliverableId));
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

   public static final long EXTERNALIZATION_VERSION_UID = 5108103200437163807L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeLong( branchId );
      output.writeObject( deliverableId );
      output.writeObject( objectNumber );
      output.writeObject( objectType );
   }

   protected void super_writeExternal_ObjectLinkedByRdm(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (com.catl.integration.rdm.ObjectLinkedByRdm) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_ObjectLinkedByRdm(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setLong( "branchId", branchId );
      output.setString( "deliverableId", deliverableId );
      output.setString( "objectNumber", objectNumber );
      output.setString( "objectType", objectType );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      branchId = input.getLong( "branchId" );
      deliverableId = input.getString( "deliverableId" );
      objectNumber = input.getString( "objectNumber" );
      objectType = input.getString( "objectType" );
   }

   boolean readVersion5108103200437163807L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      branchId = input.readLong();
      deliverableId = (java.lang.String) input.readObject();
      objectNumber = (java.lang.String) input.readObject();
      objectType = (java.lang.String) input.readObject();
      return true;
   }

   protected boolean readVersion( ObjectLinkedByRdm thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion5108103200437163807L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_ObjectLinkedByRdm( _ObjectLinkedByRdm thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      throw new java.io.InvalidClassException(CLASSNAME, "Local class not compatible: stream classdesc externalizationVersionUID="+readSerialVersionUID+" local class externalizationVersionUID="+EXTERNALIZATION_VERSION_UID);
   }
}
