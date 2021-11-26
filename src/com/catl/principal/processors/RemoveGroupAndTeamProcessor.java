package com.catl.principal.processors;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.org.DirectoryContextProvider;
import wt.org.GenericDirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.ConstantExpression;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.RolePrincipalMap;
import wt.team.Team;
import wt.team.WTRoleHolder2;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;

import com.catl.pdfsignet.PDFSignetUtil;
import com.ptc.core.components.forms.DynamicRefreshInfo;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class RemoveGroupAndTeamProcessor {

	private static Logger log=Logger.getLogger(RemoveGroupAndTeamProcessor.class.getName());
	
	public static FormResult removeGroupAndTeam(NmCommandBean clientData) throws WTException {
		FormResult result = new FormResult();
		FeedbackMessage feedbackmessage;
		Object obj = clientData.getActionOid().getRefObject();
		if (obj instanceof WTUser) {
			try {
				WTUser user = (WTUser) obj;
				removeGroupAndTeam(user);
			} catch (WTException e) {
				feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, clientData.getLocale(), "", null, new String[] { "移除组和团队失败:" + e.getLocalizedMessage() });
				result.setStatus(FormProcessingStatus.FAILURE);
				result.addFeedbackMessage(feedbackmessage);
				return result;
			}
			feedbackmessage = new FeedbackMessage(FeedbackType.SUCCESS, clientData.getLocale(), "", null, new String[] { "移除组和团队成功" });
			result.setStatus(FormProcessingStatus.SUCCESS);
			result.addFeedbackMessage(feedbackmessage);
			result.addDynamicRefreshInfo(new DynamicRefreshInfo((Persistable) obj, (Persistable) obj, "U"));
		} else {
			feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, clientData.getLocale(), "", null, new String[] { "移除组和团队失败：不是用户" });
			result.setStatus(FormProcessingStatus.FAILURE);
			result.addFeedbackMessage(feedbackmessage);
		}
		return result;
	}

	private static final boolean PRESERVE_ROLE;

	static {
		try {
			WTProperties properties = WTProperties.getLocalProperties();

			PRESERVE_ROLE = properties.getProperty("wt.team.preserveRole", true);
		}

		catch (Throwable t) {
			// logger.error( ": Error reading wt.team.* properties", t);
			throw new ExceptionInInitializerError(t);
		}
	}

	public static void removeGroupAndTeam(WTUser user) throws WTException {
		String username = user.getName();
		log.debug("--------------------------" + username + "---------------------------");
		
		Transaction tx = null;
		boolean commitTransaction = true;
		boolean enforceAccess = SessionServerHelper.manager.setAccessEnforced(false);
		WTPrincipal orginalUser = SessionHelper.manager.setAdministrator();
		try {
			tx = new Transaction();
			tx.start();
/*			boolean userInLDAP = false;
			boolean userInDB = false;
			WTUser userFromLDAP = getUserByName(username);
			if (userFromLDAP == null) {
				log.debug("userFromLDAP is null!!!!! cannot query user from LDAP");
			} else {
				log.debug(userFromLDAP);
				log.debug("userFromLDAP.fullName" + userFromLDAP.getFullName());
				log.debug("userFromLDAP.isPersistent=" + PersistenceHelper.isPersistent(userFromLDAP));
				log.debug("userFromLDAP.isDisabled=" + userFromLDAP.isDisabled());
				log.debug("userFromLDAP.isRepairNeeded=" + userFromLDAP.isRepairNeeded());
			}
			
			// try query WTUser from database
			WTUser userFromDB = null;
			QuerySpec qs = new QuerySpec();
			int idx1 = qs.addClassList(WTUser.class, true);
			SQLFunction nameUpper = SQLFunction.newSQLFunction("UPPER", new ClassAttribute(WTUser.class, WTUser.NAME));
			qs.appendWhere(new SearchCondition(nameUpper, SearchCondition.EQUAL, new ConstantExpression(username.toUpperCase())), idx1);
			QueryResult rs = PersistenceHelper.manager.find(qs);
			if (rs != null && rs.hasMoreElements()) {
				Persistable[] objs = (Persistable[]) rs.nextElement();
				userFromDB = (WTUser) objs[0];
			}
			if (userFromDB == null) {
				log.debug("Cannot query database from database!!!");
				// return;
			} else {
				userInDB = true;
				log.debug(userFromDB);
				log.debug("userFromDB.fullName" + userFromDB.getFullName());
				log.debug("userFromDB.isPersistent=" + PersistenceHelper.isPersistent(userFromDB));
				log.debug("userFromDB.isDisabled=" + userFromDB.isDisabled());
				log.debug("userFromDB.isRepairNeeded=" + userFromDB.isRepairNeeded());
			}

			// 如果LDAP中没有user，但DB中有WTUser且还没有被标记为断开的承担者，userFromLDAP还是不会为null，因此用下面的方法检查是否存在对应的LDAP条目
			if (userFromLDAP != null && userFromDB != null) {
				userInLDAP = OrganizationServicesHelper.manager.validDirectoryEntry(userFromLDAP); // 如果数据库中没有WTUser，这个方法会报错NPE
			}

			WTUser user = null;
			if (userFromLDAP != null) {
				user = userFromLDAP;
			} else if (userFromDB != null) {
				user = userFromDB;
			} else {
				log.debug("Cannot query " + username + " from LDAP and Database!");
				commitTransaction = false;
				return;
			}

			log.debug(user);
			log.debug("In LDAP =" + userInLDAP);
			log.debug("In Database =" + userInDB);
			log.debug("isPersistent=" + PersistenceHelper.isPersistent(user));
			log.debug("isDisabled=" + user.isDisabled());
			log.debug("isRepairNeeded=" + user.isRepairNeeded());
*/
			//
			Enumeration groups = OrganizationServicesHelper.manager.parentGroups(user);
			while (groups.hasMoreElements()) {
				// WTGroup g = (WTGroup) groups.nextElement();
				Object o = groups.nextElement();
				if (o instanceof WTPrincipalReference) {
					WTPrincipalReference ref = (WTPrincipalReference) o;
					if (WTGroup.class.isAssignableFrom(ref.getReferencedClass())) {
						WTGroup group = (WTGroup) ref.getObject();
						log.debug("remove form group:" + group.getName());
						log.debug("remove form group:" + group);
						OrganizationServicesHelper.manager.removeMember(group, user);
					}
				}
			}

			deleteRolePrincipalMaps(user);

			if (commitTransaction) {
				tx.commit();
				log.debug("Database transaction has been committed !");
			} else {
				tx.rollback();
				log.debug("Database transaction has been rolled-back !");
			}
			tx = null;

		} catch (Exception e) {
			e.printStackTrace();
			commitTransaction = false;
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforceAccess);
			if (tx != null) {
				if (commitTransaction) {
					tx.commit(); // this line should never be executed
				} else {
					tx.rollback();
					log.debug("Database transaction has been rolled-back !");
				}
			}
		}
	}

	private static void deleteRolePrincipalMaps(WTPrincipal principal) throws WTException {
		Transaction trx = new Transaction();
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		try {
			trx.start();
			QuerySpec qs = new QuerySpec(RolePrincipalMap.class);

			SearchCondition byPrincipal = new SearchCondition(RolePrincipalMap.class, RolePrincipalMap.PRINCIPAL_PARTICIPANT + "." + ObjectReference.KEY, SearchCondition.EQUAL, PersistenceHelper.getObjectIdentifier(principal));

			qs.appendWhere(byPrincipal);
			QueryResult results = PersistenceHelper.manager.find(qs);
			WTSet set = new WTHashSet((int) (results.size() / .75 + 1));
			WTSet updset = new WTHashSet((int) (results.size() / .75 + 1));

			while (results.hasMoreElements()) {
				RolePrincipalMap rpm = (RolePrincipalMap) results.nextElement();
				WTRoleHolder2 roleHolder = null;
				try {
					roleHolder = rpm.getWTRoleHolder2();
				} catch (WTRuntimeException wtre) {
					// logger.debug("StandardTeamService:WTRoleHolder for roleprincipalmap "
					// + rpm + " does not exist");
					// logger.error("", wtre);
					roleHolder = null;
				}

				if (PRESERVE_ROLE && roleHolder instanceof Team) {
					/*
					 * changes as per (SPR#1288534) This code is check for
					 * "Team" instance, whether this "principal" is the only
					 * participants in its role. If yes, Than instead of
					 * deleting a record we update its participants as
					 * empty(null). It helps to reassign that role later.
					 */
					Map tempRolePrincipalMap = null;
					List roleAllParticipants = null;

					try {
						if (roleHolder != null) {
							// tempRolePrincipalMap = (Map)
							// roleHolder.getRolePrincipalMap();
							// roleAllParticipants = (List)
							// tempRolePrincipalMap.get(rpm.getRole());
							QuerySpec qs1 = new QuerySpec();
							int idx1 = qs1.addClassList(RolePrincipalMap.class, false);
							qs1.appendWhere(new SearchCondition(RolePrincipalMap.class, RolePrincipalMap.WTROLE_HOLDER2_REFERENCE + "." + WTAttributeNameIfc.REF_CLASSNAME, SearchCondition.EQUAL, roleHolder.getClass().getName()), idx1);
							qs1.appendAnd();
							qs1.appendWhere(new SearchCondition(RolePrincipalMap.class, RolePrincipalMap.WTROLE_HOLDER2_REFERENCE + "." + WTAttributeNameIfc.REF_OBJECT_ID, SearchCondition.EQUAL, roleHolder.getPersistInfo().getObjectIdentifier().getId()), idx1);
							qs1.appendAnd();
							qs1.appendWhere(new SearchCondition(RolePrincipalMap.class, RolePrincipalMap.ROLE, SearchCondition.EQUAL, rpm.getRole()), idx1);
							qs1.appendSelect(SQLFunction.newSQLFunction("COUNT", new ClassAttribute(RolePrincipalMap.class, WTAttributeNameIfc.ID_NAME)), idx1, false);
							qs1.setAdvancedQueryEnabled(true);
							qs1.setQuerySet(false);
							QueryResult rs1 = PersistenceServerHelper.manager.query(qs1);
							if (rs1.hasMoreElements()) {
								Number qty = (Number) rs1.nextElement();
								if (qty.longValue() == 1) {
									log.debug("count = " + qty);
									rpm.setPrincipalParticipant(null);
									updset.add(rpm);
								} else {
									set.add(rpm);
								}
							}
						}
					} catch (WTPropertyVetoException veto) {
						throw new WTException(veto);
					}
				}

			}

			if (updset.size() > 0) {
				PersistenceServerHelper.manager.update(updset);
				log.debug("start to update RolePrincipalMap");
			}
			log.debug("start to remove RolePrincipalMap");
			PersistenceServerHelper.manager.remove(set);
			trx.commit();
			trx = null;
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
			if (trx != null)
				trx.rollback();
			
		}
	}

	private static WTUser getUserByName(String name) throws WTException {
		String[] dirServiceNames = OrganizationServicesHelper.manager.getDirectoryServiceNames();
		DirectoryContextProvider dcp = new GenericDirectoryContextProvider(dirServiceNames);
		Enumeration result = OrganizationServicesHelper.manager.findLikeUsers(WTUser.NAME, name, dcp);
		if (result.hasMoreElements()) {
			return (WTUser) result.nextElement();
		} else {
			return null;
		}
	}
}
