package com.catl.part.classification;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _ClassificationNodeConfig extends wt.fc.WTObject implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final java.lang.String RESOURCE = "com.catl.part.classification.classificationResource";
   static final java.lang.String CLASSNAME = ClassificationNodeConfig.class.getName();

   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public static final java.lang.String NODE_ID = "nodeId";
   java.lang.Long nodeId;
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public java.lang.Long getNodeId() {
      return nodeId;
   }
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public void setNodeId(java.lang.Long nodeId) throws wt.util.WTPropertyVetoException {
      nodeIdValidate(nodeId);
      this.nodeId = nodeId;
   }
   void nodeIdValidate(java.lang.Long nodeId) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public static final java.lang.String NODE_INTERNAL_NAME = "nodeInternalName";
   static int NODE_INTERNAL_NAME_UPPER_LIMIT = -1;
   java.lang.String nodeInternalName;
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public java.lang.String getNodeInternalName() {
      return nodeInternalName;
   }
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public void setNodeInternalName(java.lang.String nodeInternalName) throws wt.util.WTPropertyVetoException {
      nodeInternalNameValidate(nodeInternalName);
      this.nodeInternalName = nodeInternalName;
   }
   void nodeInternalNameValidate(java.lang.String nodeInternalName) throws wt.util.WTPropertyVetoException {
      if (NODE_INTERNAL_NAME_UPPER_LIMIT < 1) {
         try { NODE_INTERNAL_NAME_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("nodeInternalName").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { NODE_INTERNAL_NAME_UPPER_LIMIT = 250; }
      }
      if (nodeInternalName != null && !wt.fc.PersistenceHelper.checkStoredLength(nodeInternalName.toString(), NODE_INTERNAL_NAME_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "nodeInternalName"), java.lang.String.valueOf(java.lang.Math.min(NODE_INTERNAL_NAME_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "nodeInternalName", this.nodeInternalName, nodeInternalName));
   }

   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public static final java.lang.String NEED_FAE = "needFae";
   java.lang.Boolean needFae = false;
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public java.lang.Boolean getNeedFae() {
      return needFae;
   }
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public void setNeedFae(java.lang.Boolean needFae) throws wt.util.WTPropertyVetoException {
      needFaeValidate(needFae);
      this.needFae = needFae;
   }
   void needFaeValidate(java.lang.Boolean needFae) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public static final java.lang.String ATTRIBUTE_REF = "attributeRef";
   com.catl.part.classification.AttributeForFAE attributeRef = AttributeForFAE.getAttributeForFAEDefault();
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public com.catl.part.classification.AttributeForFAE getAttributeRef() {
      return attributeRef;
   }
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public void setAttributeRef(com.catl.part.classification.AttributeForFAE attributeRef) throws wt.util.WTPropertyVetoException {
      attributeRefValidate(attributeRef);
      this.attributeRef = attributeRef;
   }
   void attributeRefValidate(com.catl.part.classification.AttributeForFAE attributeRef) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public static final java.lang.String MAKE_NEED_FAE = "makeNeedFae";
   java.lang.Boolean makeNeedFae = false;
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public java.lang.Boolean getMakeNeedFae() {
      return makeNeedFae;
   }
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public void setMakeNeedFae(java.lang.Boolean makeNeedFae) throws wt.util.WTPropertyVetoException {
      makeNeedFaeValidate(makeNeedFae);
      this.makeNeedFae = makeNeedFae;
   }
   void makeNeedFaeValidate(java.lang.Boolean makeNeedFae) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public static final java.lang.String BUY_NEED_FAE = "buyNeedFae";
   java.lang.Boolean buyNeedFae = false;
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public java.lang.Boolean getBuyNeedFae() {
      return buyNeedFae;
   }
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public void setBuyNeedFae(java.lang.Boolean buyNeedFae) throws wt.util.WTPropertyVetoException {
      buyNeedFaeValidate(buyNeedFae);
      this.buyNeedFae = buyNeedFae;
   }
   void buyNeedFaeValidate(java.lang.Boolean buyNeedFae) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public static final java.lang.String MAKE_BUY_NEED_FAE = "makeBuyNeedFae";
   java.lang.Boolean makeBuyNeedFae = false;
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public java.lang.Boolean getMakeBuyNeedFae() {
      return makeBuyNeedFae;
   }
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public void setMakeBuyNeedFae(java.lang.Boolean makeBuyNeedFae) throws wt.util.WTPropertyVetoException {
      makeBuyNeedFaeValidate(makeBuyNeedFae);
      this.makeBuyNeedFae = makeBuyNeedFae;
   }
   void makeBuyNeedFaeValidate(java.lang.Boolean makeBuyNeedFae) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public static final java.lang.String CUSTOMER_NEED_FAE = "customerNeedFae";
   java.lang.Boolean customerNeedFae = false;
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public java.lang.Boolean getCustomerNeedFae() {
      return customerNeedFae;
   }
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public void setCustomerNeedFae(java.lang.Boolean customerNeedFae) throws wt.util.WTPropertyVetoException {
      customerNeedFaeValidate(customerNeedFae);
      this.customerNeedFae = customerNeedFae;
   }
   void customerNeedFaeValidate(java.lang.Boolean customerNeedFae) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public static final java.lang.String VIRTUAL_NEED_FAE = "virtualNeedFae";
   java.lang.Boolean virtualNeedFae = false;
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public java.lang.Boolean getVirtualNeedFae() {
      return virtualNeedFae;
   }
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public void setVirtualNeedFae(java.lang.Boolean virtualNeedFae) throws wt.util.WTPropertyVetoException {
      virtualNeedFaeValidate(virtualNeedFae);
      this.virtualNeedFae = virtualNeedFae;
   }
   void virtualNeedFaeValidate(java.lang.Boolean virtualNeedFae) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public static final java.lang.String NEED_NON_FAE_REPORT = "needNonFaeReport";
   java.lang.Boolean needNonFaeReport = true;
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public java.lang.Boolean getNeedNonFaeReport() {
      return needNonFaeReport;
   }
   /**
    * @see com.catl.part.classification.ClassificationNodeConfig
    */
   public void setNeedNonFaeReport(java.lang.Boolean needNonFaeReport) throws wt.util.WTPropertyVetoException {
      needNonFaeReportValidate(needNonFaeReport);
      this.needNonFaeReport = needNonFaeReport;
   }
   void needNonFaeReportValidate(java.lang.Boolean needNonFaeReport) throws wt.util.WTPropertyVetoException {
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

   public static final long EXTERNALIZATION_VERSION_UID = 3813480457609630649L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( (attributeRef == null ? null : attributeRef.getStringValue()) );
      output.writeObject( buyNeedFae );
      output.writeObject( customerNeedFae );
      output.writeObject( makeBuyNeedFae );
      output.writeObject( makeNeedFae );
      output.writeObject( needFae );
      output.writeObject( needNonFaeReport );
      output.writeObject( nodeId );
      output.writeObject( nodeInternalName );
      output.writeObject( virtualNeedFae );
   }

   protected void super_writeExternal_ClassificationNodeConfig(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (com.catl.part.classification.ClassificationNodeConfig) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_ClassificationNodeConfig(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "attributeRef", (attributeRef == null ? null : attributeRef.toString()) );
      output.setBooleanObject( "buyNeedFae", buyNeedFae );
      output.setBooleanObject( "customerNeedFae", customerNeedFae );
      output.setBooleanObject( "makeBuyNeedFae", makeBuyNeedFae );
      output.setBooleanObject( "makeNeedFae", makeNeedFae );
      output.setBooleanObject( "needFae", needFae );
      output.setBooleanObject( "needNonFaeReport", needNonFaeReport );
      output.setLongObject( "nodeId", nodeId );
      output.setString( "nodeInternalName", nodeInternalName );
      output.setBooleanObject( "virtualNeedFae", virtualNeedFae );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      java.lang.String attributeRef_string_value = (java.lang.String) input.getString("attributeRef");
      if ( attributeRef_string_value != null ) { 
         attributeRef = (com.catl.part.classification.AttributeForFAE) wt.introspection.ClassInfo.getConstrainedEnum( getClass(), "attributeRef", attributeRef_string_value );
         if ( attributeRef == null )  // hard-coded type
            attributeRef = com.catl.part.classification.AttributeForFAE.toAttributeForFAE( attributeRef_string_value );
      }
      buyNeedFae = input.getBooleanObject( "buyNeedFae" );
      customerNeedFae = input.getBooleanObject( "customerNeedFae" );
      makeBuyNeedFae = input.getBooleanObject( "makeBuyNeedFae" );
      makeNeedFae = input.getBooleanObject( "makeNeedFae" );
      needFae = input.getBooleanObject( "needFae" );
      needNonFaeReport = input.getBooleanObject( "needNonFaeReport" );
      nodeId = input.getLongObject( "nodeId" );
      nodeInternalName = input.getString( "nodeInternalName" );
      virtualNeedFae = input.getBooleanObject( "virtualNeedFae" );
   }

   boolean readVersion3813480457609630649L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      java.lang.String attributeRef_string_value = (java.lang.String) input.readObject();
      try { 
         attributeRef = (com.catl.part.classification.AttributeForFAE) wt.fc.EnumeratedTypeUtil.toEnumeratedType( attributeRef_string_value );
      } catch( wt.util.WTInvalidParameterException e ) {
         // Old Format
         attributeRef = com.catl.part.classification.AttributeForFAE.toAttributeForFAE( attributeRef_string_value );
      }
      buyNeedFae = (java.lang.Boolean) input.readObject();
      customerNeedFae = (java.lang.Boolean) input.readObject();
      makeBuyNeedFae = (java.lang.Boolean) input.readObject();
      makeNeedFae = (java.lang.Boolean) input.readObject();
      needFae = (java.lang.Boolean) input.readObject();
      needNonFaeReport = (java.lang.Boolean) input.readObject();
      nodeId = (java.lang.Long) input.readObject();
      nodeInternalName = (java.lang.String) input.readObject();
      virtualNeedFae = (java.lang.Boolean) input.readObject();
      return true;
   }

   protected boolean readVersion( ClassificationNodeConfig thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion3813480457609630649L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_ClassificationNodeConfig( _ClassificationNodeConfig thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      throw new java.io.InvalidClassException(CLASSNAME, "Local class not compatible: stream classdesc externalizationVersionUID="+readSerialVersionUID+" local class externalizationVersionUID="+EXTERNALIZATION_VERSION_UID);
   }
}
