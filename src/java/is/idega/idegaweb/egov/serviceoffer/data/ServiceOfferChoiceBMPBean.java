/*
 * $Id: ServiceOfferChoiceBMPBean.java,v 1.5 2009/05/25 13:43:15 valdas Exp $
 * Created on Aug 10, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.data;

import java.util.Collection;

import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;
import com.idega.block.process.data.AbstractCaseBMPBean;
import com.idega.block.process.data.Case;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.user.data.User;


/**
 * 
 * The actual case that is created for a custodian of the user that is referenced in this bean.
 * The parent case is the ServiceOffer that belongs to the person who creates a new ServiceOffer
 * 
 *  Last modified: $Date: 2009/05/25 13:43:15 $ by $Author: valdas $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.5 $
 */
public class ServiceOfferChoiceBMPBean extends AbstractCaseBMPBean implements Case , ServiceOfferConstants, ServiceOfferChoice{
	
	private static final String ENTITY_NAME = "egov_service_offer_choice";
	
	private static final String COLUMN_USER = "user_id";
	private static final String COLUMN_VIEWED = "viewed";
	private static final String COLUMN_PAYED = "payed";
	

	/* (non-Javadoc)
	 * @see com.idega.block.process.data.AbstractCaseBMPBean#getCaseCodeKey()
	 */
	@Override
	public String getCaseCodeKey() {
		return CASE_CODE_KEY_SERVICE_OFFER;
	}

	/* (non-Javadoc)
	 * @see com.idega.block.process.data.AbstractCaseBMPBean#getCaseCodeDescription()
	 */
	@Override
	public String getCaseCodeDescription() {
		return "Case for a service offer choice";
	}

	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#getEntityName()
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#initializeAttributes()
	 */
	@Override
	public void initializeAttributes() {
		addGeneralCaseRelation();		
		addManyToOneRelationship(COLUMN_USER, "Citizen", User.class);
		addAttribute(COLUMN_VIEWED, "Has been viewed?", Boolean.class);
		addAttribute(COLUMN_PAYED, "Has been paid for?", Boolean.class);
	}
	
	//Getters and setters
	public User getUser() {
		return (User) getColumnValue(COLUMN_USER);
	}
	
	public void setUser(User user){
		setColumn(COLUMN_USER, user);
	}
	
	public boolean hasBeenViewed() {
		return getBooleanColumnValue(COLUMN_VIEWED,false);
	}
	
	public void setAsViewed(){
		setColumn(COLUMN_VIEWED, true);
	}
	
	public void setAsNotViewed(){
		setColumn(COLUMN_VIEWED, false);
	}
	
	public boolean hasBeenPaidFor() {
		return getBooleanColumnValue(COLUMN_PAYED,false);
	}
	
	public void setAsPaidFor(){
		setColumn(COLUMN_PAYED, true);
	}
	
	public void setAsUnPaidFor(){
		setColumn(COLUMN_PAYED, false);
	}

	public void addSubscriber(User subscriber)
			throws IDOAddRelationshipException {
		throw new UnsupportedOperationException("This method is not implemented!");
	}

	public Collection<User> getSubscribers() {
		throw new UnsupportedOperationException("This method is not implemented!");
	}

	public void removeSubscriber(User subscriber)
			throws IDORemoveRelationshipException {
		throw new UnsupportedOperationException("This method is not implemented!");
	}
	
}