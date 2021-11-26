package com.catl.change.inventory;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _ECAPartLink extends wt.fc.ObjectToObjectLink implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final java.lang.String RESOURCE = "com.catl.change.inventory.inventoryResource";
   static final java.lang.String CLASSNAME = ECAPartLink.class.getName();

   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public static final java.lang.String QUANTITY = "quantity";
   java.lang.Double quantity;
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public java.lang.Double getQuantity() {
      return quantity;
   }
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public void setQuantity(java.lang.Double quantity) throws wt.util.WTPropertyVetoException {
      quantityValidate(quantity);
      this.quantity = quantity;
   }
   void quantityValidate(java.lang.Double quantity) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public static final java.lang.String MATERIAL_STATUS = "materialStatus";
   com.catl.change.inventory.MaterialStatus materialStatus;
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public com.catl.change.inventory.MaterialStatus getMaterialStatus() {
      return materialStatus;
   }
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public void setMaterialStatus(com.catl.change.inventory.MaterialStatus materialStatus) throws wt.util.WTPropertyVetoException {
      materialStatusValidate(materialStatus);
      this.materialStatus = materialStatus;
   }
   void materialStatusValidate(com.catl.change.inventory.MaterialStatus materialStatus) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public static final java.lang.String DISPOSITION_OPTION = "dispositionOption";
   com.catl.change.inventory.DispositionOption dispositionOption;
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public com.catl.change.inventory.DispositionOption getDispositionOption() {
      return dispositionOption;
   }
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public void setDispositionOption(com.catl.change.inventory.DispositionOption dispositionOption) throws wt.util.WTPropertyVetoException {
      dispositionOptionValidate(dispositionOption);
      this.dispositionOption = dispositionOption;
   }
   void dispositionOptionValidate(com.catl.change.inventory.DispositionOption dispositionOption) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public static final java.lang.String OWNER = "owner";
   static int OWNER_UPPER_LIMIT = -1;
   java.lang.String owner;
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public java.lang.String getOwner() {
      return owner;
   }
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public void setOwner(java.lang.String owner) throws wt.util.WTPropertyVetoException {
      ownerValidate(owner);
      this.owner = owner;
   }
   void ownerValidate(java.lang.String owner) throws wt.util.WTPropertyVetoException {
      if (OWNER_UPPER_LIMIT < 1) {
         try { OWNER_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("owner").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OWNER_UPPER_LIMIT = 200; }
      }
      if (owner != null && !wt.fc.PersistenceHelper.checkStoredLength(owner.toString(), OWNER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "owner"), java.lang.String.valueOf(java.lang.Math.min(OWNER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "owner", this.owner, owner));
   }

   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public static final java.lang.String DUE_DAY = "dueDay";
   java.sql.Timestamp dueDay;
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public java.sql.Timestamp getDueDay() {
      return dueDay;
   }
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public void setDueDay(java.sql.Timestamp dueDay) throws wt.util.WTPropertyVetoException {
      dueDayValidate(dueDay);
      this.dueDay = dueDay;
   }
   void dueDayValidate(java.sql.Timestamp dueDay) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public static final java.lang.String REMARKS = "remarks";
   static int REMARKS_UPPER_LIMIT = -1;
   java.lang.String remarks;
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public java.lang.String getRemarks() {
      return remarks;
   }
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public void setRemarks(java.lang.String remarks) throws wt.util.WTPropertyVetoException {
      remarksValidate(remarks);
      this.remarks = remarks;
   }
   void remarksValidate(java.lang.String remarks) throws wt.util.WTPropertyVetoException {
      if (REMARKS_UPPER_LIMIT < 1) {
         try { REMARKS_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("remarks").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { REMARKS_UPPER_LIMIT = 200; }
      }
      if (remarks != null && !wt.fc.PersistenceHelper.checkStoredLength(remarks.toString(), REMARKS_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "remarks"), java.lang.String.valueOf(java.lang.Math.min(REMARKS_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "remarks", this.remarks, remarks));
   }

   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public static final java.lang.String ECA_ROLE = "eca";
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public wt.change2.WTChangeActivity2 getEca() {
      return (wt.change2.WTChangeActivity2) getRoleAObject();
   }
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public void setEca(wt.change2.WTChangeActivity2 the_eca) throws wt.util.WTPropertyVetoException {
      setRoleAObject((wt.fc.Persistable) the_eca);
   }

   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public static final java.lang.String PART_ROLE = "part";
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public wt.part.WTPart getPart() {
      return (wt.part.WTPart) getRoleBObject();
   }
   /**
    * @see com.catl.change.inventory.ECAPartLink
    */
   public void setPart(wt.part.WTPart the_part) throws wt.util.WTPropertyVetoException {
      setRoleBObject((wt.fc.Persistable) the_part);
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

   public static final long EXTERNALIZATION_VERSION_UID = 8209771296215373305L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( (dispositionOption == null ? null : dispositionOption.getStringValue()) );
      output.writeObject( dueDay );
      output.writeObject( (materialStatus == null ? null : materialStatus.getStringValue()) );
      output.writeObject( owner );
      output.writeObject( quantity );
      output.writeObject( remarks );
   }

   protected void super_writeExternal_ECAPartLink(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (com.catl.change.inventory.ECAPartLink) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_ECAPartLink(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "dispositionOption", (dispositionOption == null ? null : dispositionOption.toString()) );
      output.setTimestamp( "dueDay", dueDay );
      output.setString( "materialStatus", (materialStatus == null ? null : materialStatus.toString()) );
      output.setString( "owner", owner );
      output.setDoubleObject( "quantity", quantity );
      output.setString( "remarks", remarks );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      java.lang.String dispositionOption_string_value = (java.lang.String) input.getString("dispositionOption");
      if ( dispositionOption_string_value != null ) { 
         dispositionOption = (com.catl.change.inventory.DispositionOption) wt.introspection.ClassInfo.getConstrainedEnum( getClass(), "dispositionOption", dispositionOption_string_value );
         if ( dispositionOption == null )  // hard-coded type
            dispositionOption = com.catl.change.inventory.DispositionOption.toDispositionOption( dispositionOption_string_value );
      }
      dueDay = input.getTimestamp( "dueDay" );
      java.lang.String materialStatus_string_value = (java.lang.String) input.getString("materialStatus");
      if ( materialStatus_string_value != null ) { 
         materialStatus = (com.catl.change.inventory.MaterialStatus) wt.introspection.ClassInfo.getConstrainedEnum( getClass(), "materialStatus", materialStatus_string_value );
         if ( materialStatus == null )  // hard-coded type
            materialStatus = com.catl.change.inventory.MaterialStatus.toMaterialStatus( materialStatus_string_value );
      }
      owner = input.getString( "owner" );
      quantity = input.getDoubleObject( "quantity" );
      remarks = input.getString( "remarks" );
   }

   boolean readVersion8209771296215373305L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      java.lang.String dispositionOption_string_value = (java.lang.String) input.readObject();
      try { 
         dispositionOption = (com.catl.change.inventory.DispositionOption) wt.fc.EnumeratedTypeUtil.toEnumeratedType( dispositionOption_string_value );
      } catch( wt.util.WTInvalidParameterException e ) {
         // Old Format
         dispositionOption = com.catl.change.inventory.DispositionOption.toDispositionOption( dispositionOption_string_value );
      }
      dueDay = (java.sql.Timestamp) input.readObject();
      java.lang.String materialStatus_string_value = (java.lang.String) input.readObject();
      try { 
         materialStatus = (com.catl.change.inventory.MaterialStatus) wt.fc.EnumeratedTypeUtil.toEnumeratedType( materialStatus_string_value );
      } catch( wt.util.WTInvalidParameterException e ) {
         // Old Format
         materialStatus = com.catl.change.inventory.MaterialStatus.toMaterialStatus( materialStatus_string_value );
      }
      owner = (java.lang.String) input.readObject();
      quantity = (java.lang.Double) input.readObject();
      remarks = (java.lang.String) input.readObject();
      return true;
   }

   protected boolean readVersion( ECAPartLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion8209771296215373305L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_ECAPartLink( _ECAPartLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      throw new java.io.InvalidClassException(CLASSNAME, "Local class not compatible: stream classdesc externalizationVersionUID="+readSerialVersionUID+" local class externalizationVersionUID="+EXTERNALIZATION_VERSION_UID);
   }
}
