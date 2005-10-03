/*
 * $Id: ServiceOfferBMPBean.java,v 1.2 2005/10/03 15:03:19 eiki Exp $ Created on
 * Aug 10, 2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.data;

import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;
import java.sql.Timestamp;
import java.util.Collection;
import com.idega.block.process.data.AbstractCaseBMPBean;
import com.idega.block.process.data.Case;
import com.idega.block.school.data.SchoolClass;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDORelationshipException;
import com.idega.user.data.Group;
import com.idega.util.ListUtil;

/**
 * A parent case for ServiceOfferChoices
 * 
 * Last modified: $Date: 2005/10/03 15:03:19 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public class ServiceOfferBMPBean extends AbstractCaseBMPBean implements Case, ServiceOfferConstants, ServiceOffer {

	public static final String COLUMN_SERVICE_PAYMENT_TYPE = "SERVICE_PAYMENT_TYPE";
	public static final String COLUMN_SERVICE_CHOICE_OPTIONAL = "SERVICE_CHOICE_OPTIONAL";
	public static final String COLUMN_SERVICE_DEADLINE = "SERVICE_DEADLINE";
	public static final String COLUMN_SERVICE_DATE = "SERVICE_DATE";
	public static final String COLUMN_SERVICE_PRICE = "SERVICE_PRICE";
	public static final String COLUMN_SERVICE_LOCATION = "SERVICE_LOCATION";
	public static final String COLUMN_SERVICE_NAME = "SERVICE_NAME";
	public static final String COLUMN_SERVICE_TEXT = "SERVICE_TEXT";
	public static final String ENTITY_NAME = "egov_service_offer";
	public static final String GROUP_MIDDLE_TABLE_NAME = "egov_serv_off_ic_group";
	public static final String SCHOOL_CLASS_MIDDLE_TABLE_NAME = "egov_serv_off_sch_class";


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.block.process.data.AbstractCaseBMPBean#getCaseCodeKey()
	 */
	public String getCaseCodeKey() {
		return CASE_CODE_KEY_SERVICE_OFFER_PARENT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.block.process.data.AbstractCaseBMPBean#getCaseCodeDescription()
	 */
	public String getCaseCodeDescription() {
		return "Parent case for service offer choices";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.GenericEntity#getEntityName()
	 */
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.GenericEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
		addGeneralCaseRelation();
		addManyToManyRelationShip(SchoolClass.class, SCHOOL_CLASS_MIDDLE_TABLE_NAME);
		addManyToManyRelationShip(Group.class, GROUP_MIDDLE_TABLE_NAME);
		
		addAttribute(COLUMN_SERVICE_NAME, "Service name", String.class, 50);
		addAttribute(COLUMN_SERVICE_TEXT, "Service text", String.class, 4000);
		addAttribute(COLUMN_SERVICE_LOCATION, "Service location", String.class,50);
		
		addAttribute(COLUMN_SERVICE_DATE, "Service date and time", Timestamp.class);
		addAttribute(COLUMN_SERVICE_DEADLINE, "Service deadline", Timestamp.class);
		
		addAttribute(COLUMN_SERVICE_PRICE, "Service price", Double.class);
		addAttribute(COLUMN_SERVICE_CHOICE_OPTIONAL, "Service choice optional?", Boolean.class);
		addAttribute(COLUMN_SERVICE_PAYMENT_TYPE, "Service payment type, cash,credit,invoice...", String.class, 10);
		
	}

	public String getServiceName() {
		return getStringColumnValue(COLUMN_SERVICE_NAME);
	}

	public void setServiceName(String name) {
		setColumn(COLUMN_SERVICE_NAME, name);
	}
	
	public String getServiceText() {
		return getStringColumnValue(COLUMN_SERVICE_TEXT);
	}

	public void setServiceText(String text) {
		setColumn(COLUMN_SERVICE_TEXT, text);
	}
	
	public String getServiceLocation() {
		return getStringColumnValue(COLUMN_SERVICE_LOCATION);
	}

	public void setServiceLocation(String location) {
		setColumn(COLUMN_SERVICE_LOCATION, location);
	}
	
	public String getServicePaymentType() {
		return getStringColumnValue(COLUMN_SERVICE_PAYMENT_TYPE);
	}

	public void setServicePaymentType(String paymentType) {
		setColumn(COLUMN_SERVICE_PAYMENT_TYPE, paymentType);
	}

	public Timestamp getServiceDate() {
		return getTimestampColumnValue(COLUMN_SERVICE_DATE);
	}

	public void setServiceDate(Timestamp date) {
		setColumn(COLUMN_SERVICE_DATE, date);
	}
	
	public Timestamp getServiceDeadline() {
		return getTimestampColumnValue(COLUMN_SERVICE_DEADLINE);
	}

	public void setServiceDeadline(Timestamp deadline) {
		setColumn(COLUMN_SERVICE_DEADLINE, deadline);
	}
	
	public double getServicePrice() {
		return getDoubleColumnValue(COLUMN_SERVICE_PRICE,0);
	}

	public void setServicePrice(double price) {
		setColumn(COLUMN_SERVICE_PRICE, price);
	}
	
	public boolean isServiceChoiceOptional() {
		return getBooleanColumnValue(COLUMN_SERVICE_CHOICE_OPTIONAL,false);
	}

	public void setServiceChoiceAsOptional() {
		setColumn(COLUMN_SERVICE_CHOICE_OPTIONAL, true);
	}
	
	public void setServiceChoiceAsMandatory() {
		setColumn(COLUMN_SERVICE_CHOICE_OPTIONAL, false);
	}
		
	public Collection getGroups() {
		try {
			return super.idoGetRelatedEntities(Group.class);
		}
		catch (IDORelationshipException e) {
			return ListUtil.getEmptyList();
		}
	}

	public Collection getSchoolClasses() {
		try {
			return super.idoGetRelatedEntities(SchoolClass.class);
		}
		catch (IDORelationshipException e) {
			return ListUtil.getEmptyList();
		}
	}
	
	public void addGroup(Group group) throws IDOAddRelationshipException {
		this.idoAddTo(group);
	}
	
	public void addSchoolClass(SchoolClass schoolClass) throws IDOAddRelationshipException {
		this.idoAddTo(schoolClass);
	}
	
	//	
	// //Finders
	// public Object ejbFindByUserAndSeason(User user, School school,
	// SchoolSeason season) throws FinderException {
	// Table table = new Table(this);
	//		
	// SelectQuery query = new SelectQuery(table);
	// query.addColumn(table, getIDColumnName());
	// query.addCriteria(new MatchCriteria(table, COLUMN_USER,
	// MatchCriteria.EQUALS, user));
	// query.addCriteria(new MatchCriteria(table, COLUMN_SCHOOL,
	// MatchCriteria.EQUALS, school));
	// query.addCriteria(new MatchCriteria(table, COLUMN_SEASON,
	// MatchCriteria.EQUALS, season));
	//		
	// return idoFindOnePKByQuery(query);
	// }
	//	
	// public Collection ejbFindAllBySchoolAndSeason(School school, SchoolSeason
	// season) throws FinderException {
	// Table table = new Table(this);
	//		
	// SelectQuery query = new SelectQuery(table);
	// query.addColumn(table, getIDColumnName());
	// query.addCriteria(new MatchCriteria(table, COLUMN_SCHOOL,
	// MatchCriteria.EQUALS, school));
	// query.addCriteria(new MatchCriteria(table, COLUMN_SEASON,
	// MatchCriteria.EQUALS, season));
	//		
	// return idoFindPKsByQuery(query);
	// }
}