/*
 * $Id: ServiceOfferChoiceBMPBean.java,v 1.2 2005/10/16 16:19:50 eiki Exp $
 * Created on Aug 10, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.data;

import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;
import com.idega.block.process.data.AbstractCaseBMPBean;
import com.idega.block.process.data.Case;
import com.idega.user.data.User;


/**
 * 
 * The actual case that is created for a custodian of the user that is referenced in this bean.
 * The parent case is the ServiceOffer that belongs to the person who creates a new ServiceOffer
 * 
 *  Last modified: $Date: 2005/10/16 16:19:50 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public class ServiceOfferChoiceBMPBean extends AbstractCaseBMPBean implements Case , ServiceOfferConstants, ServiceOfferChoice{
	
	private static final String ENTITY_NAME = "egov_service_offer_choice";
	
	private static final String COLUMN_USER = "user_id";
	private static final String COLUMN_VIEWED = "viewed";
	private static final String COLUMN_PAYED = "payed";
	

	/* (non-Javadoc)
	 * @see com.idega.block.process.data.AbstractCaseBMPBean#getCaseCodeKey()
	 */
	public String getCaseCodeKey() {
		return CASE_CODE_KEY_SERVICE_OFFER;
	}

	/* (non-Javadoc)
	 * @see com.idega.block.process.data.AbstractCaseBMPBean#getCaseCodeDescription()
	 */
	public String getCaseCodeDescription() {
		return "Case for a service offer choice";
	}

	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#getEntityName()
	 */
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#initializeAttributes()
	 */
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
		return (boolean) getBooleanColumnValue(COLUMN_VIEWED,false);
	}
	
	public void setAsViewed(){
		setColumn(COLUMN_VIEWED, true);
	}
	
	public void setAsNotViewed(){
		setColumn(COLUMN_VIEWED, false);
	}
	
	public boolean hasBeenPaidFor() {
		return (boolean) getBooleanColumnValue(COLUMN_PAYED,false);
	}
	
	public void setAsPaidFor(){
		setColumn(COLUMN_VIEWED, true);
	}
	
	public void setAsUnPaidFor(){
		setColumn(COLUMN_VIEWED, false);
	}
	
}