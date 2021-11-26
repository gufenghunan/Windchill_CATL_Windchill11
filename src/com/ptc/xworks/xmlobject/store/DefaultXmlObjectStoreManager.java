/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package com.ptc.xworks.xmlobject.store;

import com.ptc.xworks.ConfigurationException;
import com.ptc.xworks.windchill.listener.XWorksEventService;
import com.ptc.xworks.xmlobject.BaseXmlObjectLink;
import com.ptc.xworks.xmlobject.MapEntryXmlObjectLink;
import com.ptc.xworks.xmlobject.PersistentState;
import com.ptc.xworks.xmlobject.XmlObject;
import com.ptc.xworks.xmlobject.XmlObjectIdentifier;
import com.ptc.xworks.xmlobject.XmlObjectLink;
import com.ptc.xworks.xmlobject.XmlObjectRef;
import com.ptc.xworks.xmlobject.annotation.processor.XmlObjectLinkMarkerAttribute;
import com.ptc.xworks.xmlobject.annotation.processor.XmlObjectStoreAnnotationProcessor;
import com.ptc.xworks.xmlobject.annotation.processor.XmlObjectStoreMetadata;
import com.ptc.xworks.xmlobject.annotation.processor.XmlObjectLinkMarkerAttribute.AttributeType;
import com.ptc.xworks.xmlobject.search.OrderBy;
import com.ptc.xworks.xmlobject.search.XmlObjectDeleteCriteria;
import com.ptc.xworks.xmlobject.search.XmlObjectUpdateCriteria;
import com.ptc.xworks.xmlobject.search.XmlSearchCriteria;
import com.ptc.xworks.xmlobject.store.CurrentUserResolver;
import com.ptc.xworks.xmlobject.store.XmlObjectLinkMarkerProcessor;
import com.ptc.xworks.xmlobject.store.XmlObjectLinkStoreMapper;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreException;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreManager;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreManagerAware;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreMapperNotFound;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreMapperResolver;
import com.ptc.xworks.xmlobject.store.StoreOptions.CopyOption;
import com.ptc.xworks.xmlobject.store.StoreOptions.DeleteOption;
import com.ptc.xworks.xmlobject.store.StoreOptions.LoadOption;
import com.ptc.xworks.xmlobject.store.StoreOptions.SaveOption;
import com.ptc.xworks.xmlobject.store.XmlObjectLinkMarkerProcessor.XmlObjectLinkAttributeHolder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import wt.services.ManagerServiceFactory;

public class DefaultXmlObjectStoreManager implements XmlObjectStoreManager, InitializingBean {
	private XmlObjectStoreMapperResolver<? extends XmlObject> mapperResolver;
	private CurrentUserResolver currentUserResolver;
	private XmlObjectLinkStoreMapper xmlObjectLinkStoreMapper;
	private XmlObjectStoreAnnotationProcessor storeAnnotationProcessor;
	private XmlObjectLinkMarkerProcessor linkMarkerProcessor;
	private XWorksEventService eventService = (XWorksEventService) ManagerServiceFactory.getDefault()
			.getManager(XWorksEventService.class);

	public void setXmlObjectStoreAnnotationProcessor(XmlObjectStoreAnnotationProcessor storeAnnotationProcessor) {
		this.storeAnnotationProcessor = storeAnnotationProcessor;
	}

	public void setXmlObjectLinkMarkerProcessor(XmlObjectLinkMarkerProcessor linkMarkerProcessor) {
		this.linkMarkerProcessor = linkMarkerProcessor;
	}

	public void setXmlObjectStoreMapperResolver(XmlObjectStoreMapperResolver<? extends XmlObject> mapperResolver) {
		this.mapperResolver = mapperResolver;
	}

	public void setCurrentUserResolver(CurrentUserResolver currentUserResolver) {
		this.currentUserResolver = currentUserResolver;
	}

	public void setXmlObjectLinkStoreMapper(XmlObjectLinkStoreMapper<? extends XmlObject> xmlObjectLinkStoreMapper) {
		this.xmlObjectLinkStoreMapper = xmlObjectLinkStoreMapper;
	}

	public XmlObjectLinkStoreMapper getXmlObjectLinkStoreMapper() {
		return this.xmlObjectLinkStoreMapper;
	}

	public void afterPropertiesSet() throws Exception {
		if (this.mapperResolver == null) {
			throw new Exception("XmlObjectStoreMapperResolver is not set for this bean!");
		} else {
			if (this.mapperResolver instanceof XmlObjectStoreManagerAware) {
				((XmlObjectStoreManagerAware) this.mapperResolver).setXmlObjectStoreManager(this);
			}

		}
	}

	public XmlObjectStoreMapperResolver getStoreMapperResolver() {
		return this.mapperResolver;
	}

	public CurrentUserResolver getCurrentUserResolver() {
		return this.currentUserResolver;
	}

	public XmlObjectLinkMarkerProcessor getLinkMarkerProcessor() {
		return this.linkMarkerProcessor;
	}

	public XmlObjectStoreMapperResolver<? extends XmlObject> getMapperResolver() {
		return this.mapperResolver;
	}

	public XmlObjectStoreAnnotationProcessor getStoreAnnotationProcessor() {
		return this.storeAnnotationProcessor;
	}

	public boolean isSupported(Class xmlObjectType) {
		return this.isSupported(xmlObjectType.getName());
	}

	public boolean isSupported(String xmlObjectType) {
		try {
			this.getStoreMapperResolver().resolve(xmlObjectType);
			return true;
		} catch (XmlObjectStoreMapperNotFound arg2) {
			return false;
		}
	}

	public boolean isSupported(XmlObjectIdentifier xmlOid) {
		return this.isSupported(xmlOid.getType());
	}

	public XmlObject load(XmlObjectIdentifier xmlOid) throws XmlObjectStoreException {
		return this.load(xmlOid, LoadOption.ALL_CHILDS);
	}

	public XmlObject save(XmlObject xmlObj) throws XmlObjectStoreException {
		return this.save(xmlObj, SaveOption.APPEND_AND_UPDATE);
	}

	public XmlObject delete(XmlObject xmlObject) throws XmlObjectStoreException {
		return this.delete(xmlObject, DeleteOption.REMOVE_CHILD_LINKS);
	}

	public void delete(XmlObjectIdentifier xmlOid) throws XmlObjectStoreException {
		this.delete(xmlOid, DeleteOption.REMOVE_CHILD_LINKS);
	}

	public boolean isStored(XmlObject xmlObj) {
		XmlObjectIdentifier xmlOid = xmlObj.getIdentifier();
		return xmlOid == null ? false : xmlObj.isStored();
	}

	public XmlObject load(XmlObjectIdentifier xmlOid, LoadOption option) throws XmlObjectStoreException {
		if (option == null) {
			throw new NullPointerException("LoadOption cannot be null!");
		} else {
			XmlObject xmlObject = this.getStoreMapperResolver().resolve(xmlOid).load(xmlOid);
			if (option == LoadOption.ONLY_ROOT) {
				return xmlObject;
			} else {
				if (option == LoadOption.ALL_CHILDS) {
					this.loadChilds(xmlObject);
				}

				return xmlObject;
			}
		}
	}

	protected void loadChilds(XmlObject xmlObject) throws XmlObjectStoreException {
		XmlObjectStoreMetadata storeMetadata = this.getStoreAnnotationProcessor()
				.getXmlObjectStoreMetadata(xmlObject.getClass());
		List childFields = storeMetadata.getXmlObjectLinkMarkerAttributes();
		Iterator arg3 = childFields.iterator();

		while (true) {
			XmlObjectLinkMarkerAttribute childField;
			List links;
			Iterator child2;
			do {
				if (!arg3.hasNext()) {
					return;
				}

				childField = (XmlObjectLinkMarkerAttribute) arg3.next();
				if (childField.getAttributeType() == AttributeType.XML_OBJECT) {
					links = this.navigate(xmlObject, childField.getChildRole());
					if (links.size() > 1) {
						throw new ConfigurationException(
								"Found more than one child by role:" + childField.getChildRole() + " of "
										+ xmlObject.getIdentifier() + ", should be one to one link!");
					}

					if (links.size() == 1) {
						childField.setFieldValue(xmlObject, links.get(0));
						this.loadChilds((XmlObject) links.get(0));
					}

					if (links.isEmpty()) {
						childField.setFieldValue(xmlObject, (Object) null);
					}
				}

				if (childField.getAttributeType() == AttributeType.LIST) {
					links = this.navigate(xmlObject, childField.getChildRole());
					childField.setFieldValue(xmlObject, links);
					Iterator childMap = links.iterator();

					while (childMap.hasNext()) {
						XmlObject child = (XmlObject) childMap.next();
						this.loadChilds(child);
					}
				}

				if (childField.getAttributeType() == AttributeType.SET) {
					links = this.navigate(xmlObject, childField.getChildRole());
					LinkedHashSet childMap1 = new LinkedHashSet(links.size());
					childMap1.addAll(links);
					childField.setFieldValue(xmlObject, childMap1);
					child2 = links.iterator();

					while (child2.hasNext()) {
						XmlObject link = (XmlObject) child2.next();
						this.loadChilds(link);
					}
				}
			} while (childField.getAttributeType() != AttributeType.MAP);

			links = this.expand(xmlObject, childField.getChildRole());
			LinkedHashMap childMap2 = new LinkedHashMap();
			child2 = links.iterator();

			while (child2.hasNext()) {
				XmlObjectLink link1 = (XmlObjectLink) child2.next();
				if (!(link1 instanceof MapEntryXmlObjectLink)) {
					throw new ConfigurationException("Unsupported XmlObjectLink for sub-XmlObject in Map" + link1);
				}

				MapEntryXmlObjectLink mapEntryLink = (MapEntryXmlObjectLink) link1;
				XmlObjectIdentifier childOid = new XmlObjectIdentifier(link1.getChild());
				XmlObject child1 = this.mapperResolver.resolve(childOid).load(childOid);
				this.loadChilds(child1);
				childMap2.put(mapEntryLink.getKey(), child1);
			}

			childField.setFieldValue(xmlObject, childMap2);
		}
	}

	public XmlObject save(XmlObject parentXmlObj, SaveOption option) throws XmlObjectStoreException {
		if (parentXmlObj == null) {
			throw new NullPointerException("cannot save a null object!");
		} else if (option == null) {
			throw new NullPointerException("SaveOption cannot be null!");
		} else {
			if (parentXmlObj.getCreator() == null || StringUtils.isBlank(parentXmlObj.getCreator())) {
				parentXmlObj.setCreator(this.getCurrentUserResolver().getCurrentUser());
			}

			if (parentXmlObj.getUpdatedBy() == null || StringUtils.isBlank(parentXmlObj.getUpdatedBy())) {
				parentXmlObj.setUpdatedBy(this.getCurrentUserResolver().getCurrentUser());
			}

			List childFields = this.getLinkMarkerProcessor().setLinkAttributeValueToNull(parentXmlObj);

			try {
				if (childFields.isEmpty()) {
					XmlObject result1 = this.getStoreMapperResolver().resolve(parentXmlObj).save(parentXmlObj);
					XmlObject removedChildField1 = result1;
					return removedChildField1;
				}

				Iterator result = childFields.iterator();

				XmlObjectLinkAttributeHolder removedChildField;
				Iterator links;
				XmlObject childObj;
				while (result.hasNext()) {
					removedChildField = (XmlObjectLinkAttributeHolder) result.next();
					links = removedChildField.getChilds().iterator();

					while (links.hasNext()) {
						childObj = (XmlObject) links.next();
						this.save(childObj, option);
					}
				}

				XmlObjectLink link;
				if (SaveOption.REPLACE_AND_UPDATE == option) {
					result = childFields.iterator();

					while (result.hasNext()) {
						removedChildField = (XmlObjectLinkAttributeHolder) result.next();
						List links1 = this.getXmlObjectLinkStoreMapper().expand(parentXmlObj,
								removedChildField.getChildRole());
						Iterator childObj1 = links1.iterator();

						while (childObj1.hasNext()) {
							link = (XmlObjectLink) childObj1.next();
							this.getStoreMapperResolver().resolve(link).delete(link);
						}
					}
				}

				this.getStoreMapperResolver().resolve(parentXmlObj).save(parentXmlObj);
				result = childFields.iterator();

				while (result.hasNext()) {
					removedChildField = (XmlObjectLinkAttributeHolder) result.next();
					if (removedChildField.getAttributeType() == AttributeType.MAP) {
						links = removedChildField.getChildMap().entrySet().iterator();

						while (links.hasNext()) {
							Entry childObj2 = (Entry) links.next();
							link = this.createMapEntryXmlObjectLink(parentXmlObj, removedChildField.getParentRole(),
									childObj2, removedChildField.getChildRole());
							this.save(link);
						}
					} else {
						links = removedChildField.getChilds().iterator();

						while (links.hasNext()) {
							childObj = (XmlObject) links.next();
							link = this.createXmlObjectLink(parentXmlObj, removedChildField.getParentRole(), childObj,
									removedChildField.getChildRole());
							this.save(link);
						}
					}
				}
			} finally {
				if (childFields != null && !childFields.isEmpty()) {
					this.getLinkMarkerProcessor().returnValueToLinkAttribute(parentXmlObj, childFields);
				}

			}

			return parentXmlObj;
		}
	}

	public XmlObject delete(XmlObject xmlObject, DeleteOption option) throws XmlObjectStoreException {
		if (xmlObject == null) {
			throw new NullPointerException("XmlObject cannot be null!");
		} else if (option == null) {
			throw new NullPointerException("DeleteOption cannot be null!");
		} else {
			List parentLinks = this.linkedBy(xmlObject);
			Iterator storeMetaData = parentLinks.iterator();

			while (storeMetaData.hasNext()) {
				XmlObjectLink childFields = (XmlObjectLink) storeMetaData.next();
				this.delete((XmlObject) childFields);
			}

			if (option != DeleteOption.REMOVE_CHILD_LINKS) {
				if (option == DeleteOption.DELETE_ALL_CHILDS) {
					this.deleteAndChilds(xmlObject);
				}

				return xmlObject;
			} else {
				XmlObjectStoreMetadata storeMetaData1 = this.getXmlObjectLinkStoreMapper()
						.getStoreMetadata(xmlObject.getClass());
				List childFields1 = storeMetaData1.getXmlObjectLinkMarkerAttributes();
				Iterator arg5 = childFields1.iterator();

				while (arg5.hasNext()) {
					XmlObjectLinkMarkerAttribute childField = (XmlObjectLinkMarkerAttribute) arg5.next();
					this.removeChilds(xmlObject, childField.getChildRole());
				}

				this.getStoreMapperResolver().resolve(xmlObject).delete(xmlObject);
				return xmlObject;
			}
		}
	}

	protected void deleteAndChilds(XmlObject parent) throws XmlObjectStoreException {
		XmlObjectStoreMetadata storeMetadata = this.getStoreAnnotationProcessor()
				.getXmlObjectStoreMetadata(parent.getClass());
		List childFields = storeMetadata.getXmlObjectLinkMarkerAttributes();
		Iterator arg3 = childFields.iterator();

		while (arg3.hasNext()) {
			XmlObjectLinkMarkerAttribute childField = (XmlObjectLinkMarkerAttribute) arg3.next();
			List childs = this.navigate(parent, childField.getChildRole());
			if (!childs.isEmpty()) {
				this.removeChilds(parent, childField.getChildRole());
			}

			Iterator arg6 = childs.iterator();

			while (arg6.hasNext()) {
				XmlObject child = (XmlObject) arg6.next();
				this.deleteAndChilds(child);
			}
		}

		this.getStoreMapperResolver().resolve(parent).delete(parent);
	}

	public void delete(XmlObjectIdentifier xmlOid, DeleteOption option) throws XmlObjectStoreException {
		XmlObject xmlObject = this.load(xmlOid, LoadOption.ONLY_ROOT);
		this.delete(xmlObject, option);
	}

	protected XmlObjectLink createMapEntryXmlObjectLink(XmlObject parent, String parentRole,
			Entry<Serializable, XmlObject> child, String childRole) {
		return new MapEntryXmlObjectLink(parent, parentRole, (XmlObject) child.getValue(), childRole,
				(Serializable) child.getKey());
	}

	protected XmlObjectLink createXmlObjectLink(XmlObject parent, String parentRole, XmlObject child,
			String childRole) {
		return new BaseXmlObjectLink(parent, parentRole, child, childRole);
	}

	protected XmlObjectLink createXmlObjectLink(XmlObjectIdentifier parent, String parentRole,
			XmlObjectIdentifier child, String childRole) {
		return new BaseXmlObjectLink(parent, parentRole, child, childRole);
	}

	protected XmlObjectLink createXmlObjectLink(XmlObjectRef parent, String parentRole, XmlObjectRef child,
			String childRole) {
		return new BaseXmlObjectLink(parent, parentRole, child, childRole);
	}

	public List<XmlObject> navigate(XmlObject parent, String childRole) throws XmlObjectStoreException {
		if (parent == null) {
			throw new NullPointerException("XmlObject cannot be null!");
		} else if (childRole != null && !StringUtils.isBlank(childRole)) {
			return this.navigate(parent.getIdentifier(), childRole);
		} else {
			throw new NullPointerException("Child role cannot be null or blank!");
		}
	}

	public List<XmlObject> navigate(XmlObjectIdentifier parentOid, String childRole) throws XmlObjectStoreException {
		if (parentOid == null) {
			throw new NullPointerException("XmlObject cannot be null!");
		} else if (childRole != null && !StringUtils.isBlank(childRole)) {
			List links = this.expand(parentOid, childRole);
			ArrayList result = new ArrayList(links.size());
			Iterator arg4 = links.iterator();

			while (arg4.hasNext()) {
				XmlObjectLink link = (XmlObjectLink) arg4.next();
				XmlObjectRef ref = link.getChild();
				XmlObjectIdentifier xmlOid = new XmlObjectIdentifier(ref);
				XmlObject child = this.getMapperResolver().resolve(xmlOid).load(xmlOid);
				result.add(child);
			}

			return result;
		} else {
			throw new NullPointerException("Child role cannot be null or blank!");
		}
	}

	public List<XmlObjectLink> expand(XmlObject parent, String childRole) throws XmlObjectStoreException {
		if (parent == null) {
			throw new NullPointerException("XmlObject cannot be null!");
		} else if (childRole != null && !StringUtils.isBlank(childRole)) {
			return this.getXmlObjectLinkStoreMapper().expand(parent, childRole);
		} else {
			throw new NullPointerException("child Role cannot be null or blank!");
		}
	}

	public List<XmlObjectLink> expand(XmlObjectIdentifier parentOid, String childRole) throws XmlObjectStoreException {
		if (parentOid == null) {
			throw new NullPointerException("XmlObjectIdentifier cannot be null!");
		} else if (childRole != null && !StringUtils.isBlank(childRole)) {
			return this.getXmlObjectLinkStoreMapper().expand(parentOid, childRole);
		} else {
			throw new NullPointerException("child Role cannot be null or blank!");
		}
	}

	public List<XmlObjectLink> linkedBy(XmlObject child) throws XmlObjectStoreException {
		if (child == null) {
			throw new NullPointerException("Child XmlObject cannot be null!");
		} else {
			return this.getXmlObjectLinkStoreMapper().linkedBy(child);
		}
	}

	public List<XmlObjectLink> linkedBy(XmlObject child, String parentRole) throws XmlObjectStoreException {
		if (child == null) {
			throw new NullPointerException("Child XmlObject cannot be null!");
		} else {
			return this.getXmlObjectLinkStoreMapper().linkedBy(child, parentRole);
		}
	}

	public List<XmlObjectLink> linkedBy(XmlObjectIdentifier childOid, String parentRole)
			throws XmlObjectStoreException {
		if (childOid == null) {
			throw new NullPointerException("child XmlObjectIdentifier cannot be null!");
		} else {
			return this.getXmlObjectLinkStoreMapper().linkedBy(childOid, parentRole);
		}
	}

	public List<XmlObject> usedBy(XmlObject child) throws XmlObjectStoreException {
		return this.usedBy((XmlObjectIdentifier) child.getIdentifier(), (String) null);
	}

	public List<XmlObject> usedBy(XmlObject child, String parentRole) throws XmlObjectStoreException {
		return this.usedBy(child.getIdentifier(), parentRole);
	}

	public List<XmlObject> usedBy(XmlObjectIdentifier childOid, String parentRole) throws XmlObjectStoreException {
		List links = this.linkedBy(childOid, parentRole);
		ArrayList result = new ArrayList();
		Iterator arg4 = links.iterator();

		while (arg4.hasNext()) {
			XmlObjectLink link = (XmlObjectLink) arg4.next();
			XmlObjectRef parentRef = link.getParent();
			if (!this.isXmlObject(parentRef.getType())) {
				throw new ConfigurationException("Non-XmlObject linked to " + childOid + " by " + parentRef
						+ ", please use linkedBy() insteaed");
			}

			XmlObject parent = this.load(parentRef, LoadOption.ONLY_ROOT);
			result.add(parent);
		}

		return result;
	}

	public List<XmlObjectLink> removeChilds(XmlObject parent, String childRole) throws XmlObjectStoreException {
		if (parent == null) {
			throw new NullPointerException("XmlObject cannot be null!");
		} else if (childRole != null && !StringUtils.isBlank(childRole)) {
			return this.getXmlObjectLinkStoreMapper().removeChilds(parent, childRole);
		} else {
			throw new NullPointerException("Child role cannot be null or blank!");
		}
	}

	public List<XmlObjectLink> removeChilds(XmlObjectIdentifier parentOid, String childRole)
			throws XmlObjectStoreException {
		if (parentOid == null) {
			throw new NullPointerException("XmlObjectIdentifier cannot be null!");
		} else if (childRole != null && !StringUtils.isBlank(childRole)) {
			return this.getXmlObjectLinkStoreMapper().removeChilds(parentOid, childRole);
		} else {
			throw new NullPointerException("Child Role cannot be null or blank!");
		}
	}

	public XmlObject copy(XmlObjectIdentifier xmlOid, CopyOption option) throws XmlObjectStoreException {
		if (option == null) {
			throw new NullPointerException("CopyOption cannot be null!");
		} else {
			XmlObject xmlObject;
			if (option == CopyOption.ONLY_ROOT) {
				xmlObject = this.load(xmlOid, LoadOption.ONLY_ROOT);
				xmlObject.getIdentifier().setId(-1L);
				xmlObject.getIdentifier().setState(PersistentState.NON_PERSISTED);
				return this.save(xmlObject);
			} else if (option == CopyOption.ALL_CHILDS) {
				xmlObject = this.load(xmlOid, LoadOption.ALL_CHILDS);
				this.resetId(xmlObject);
				return this.save(xmlObject);
			} else {
				throw new UnsupportedOperationException("code should not run to here!");
			}
		}
	}

	protected void resetId(XmlObject xmlObject) {
		XmlObjectStoreMetadata storeMetadata = this.getStoreAnnotationProcessor()
				.getXmlObjectStoreMetadata(xmlObject.getClass());
		List childFields = storeMetadata.getXmlObjectLinkMarkerAttributes();
		Iterator arg3 = childFields.iterator();

		while (true) {
			XmlObjectLinkMarkerAttribute childField;
			Object childs;
			do {
				do {
					if (!arg3.hasNext()) {
						xmlObject.getIdentifier().setId(-1L);
						xmlObject.getIdentifier().setState(PersistentState.NON_PERSISTED);
						return;
					}

					childField = (XmlObjectLinkMarkerAttribute) arg3.next();
					childs = childField.getFieldValue(xmlObject);
				} while (childs == null);

				if (childField.getAttributeType() == AttributeType.XML_OBJECT) {
					this.resetId((XmlObject) childs);
				}

				if (childField.getAttributeType() == AttributeType.LIST
						|| childField.getAttributeType() == AttributeType.SET) {
					Iterator childMap = ((Collection) childs).iterator();

					while (childMap.hasNext()) {
						XmlObject child = (XmlObject) childMap.next();
						this.resetId(child);
					}
				}
			} while (childField.getAttributeType() != AttributeType.MAP);

			Map childMap1 = (Map) childs;
			Iterator child2 = childMap1.values().iterator();

			while (child2.hasNext()) {
				XmlObject child1 = (XmlObject) child2.next();
				this.resetId(child1);
			}
		}
	}

	public XmlObject load(XmlObjectRef objRef) throws XmlObjectStoreException {
		return this.load(new XmlObjectIdentifier(objRef));
	}

	public XmlObject load(XmlObjectRef objRef, LoadOption option) throws XmlObjectStoreException {
		return this.load(new XmlObjectIdentifier(objRef), option);
	}

	public List<XmlObject> search(XmlSearchCriteria criteria, LoadOption option) throws XmlObjectStoreException {
		List result = this.getStoreMapperResolver().resolve(criteria.getType().getName()).search(criteria);
		if (option == LoadOption.ONLY_ROOT) {
			return result;
		} else if (option != LoadOption.ALL_CHILDS) {
			throw new IllegalArgumentException("unsupport LoadOption : " + option);
		} else {
			ArrayList newResult = new ArrayList(result.size());
			Iterator arg4 = result.iterator();

			while (arg4.hasNext()) {
				XmlObject xmlObject = (XmlObject) arg4.next();
				newResult.add(this.load(xmlObject.getIdentifier()));
			}

			return newResult;
		}
	}

	public List<XmlObject> search(XmlSearchCriteria criteria, OrderBy orderBy, LoadOption option)
			throws XmlObjectStoreException {
		List result = this.getStoreMapperResolver().resolve(criteria.getType().getName()).search(criteria, orderBy);
		if (option == LoadOption.ONLY_ROOT) {
			return result;
		} else if (option != LoadOption.ALL_CHILDS) {
			throw new IllegalArgumentException("unsupport LoadOption : " + option);
		} else {
			ArrayList newResult = new ArrayList(result.size());
			Iterator arg5 = result.iterator();

			while (arg5.hasNext()) {
				XmlObject xmlObject = (XmlObject) arg5.next();
				newResult.add(this.load(xmlObject.getIdentifier()));
			}

			return newResult;
		}
	}

	public int batchUpdate(XmlObjectUpdateCriteria updateCriteria) throws XmlObjectStoreException {
		return this.getStoreMapperResolver().resolve(updateCriteria.getType().getName()).batchUpdate(updateCriteria);
	}

	public int batchDelete(XmlObjectDeleteCriteria deleteCriteria) throws XmlObjectStoreException {
		return this.getStoreMapperResolver().resolve(deleteCriteria.getType().getName()).batchDelete(deleteCriteria);
	}

	private boolean isXmlObject(String className) {
		try {
			Class e = Class.forName(className);
			return XmlObject.class.isAssignableFrom(e);
		} catch (ClassNotFoundException arg2) {
			throw new ConfigurationException(arg2);
		}
	}
}