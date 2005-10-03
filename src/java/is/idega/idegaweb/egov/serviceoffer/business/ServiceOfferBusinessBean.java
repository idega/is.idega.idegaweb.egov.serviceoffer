/*
 * $Id: ServiceOfferBusinessBean.java,v 1.2 2005/10/03 16:49:09 eiki Exp $
 * Created on Aug 10, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.business;

import is.idega.block.family.business.NoCustodianFound;
import is.idega.idegaweb.egov.serviceoffer.data.ServiceOffer;
import is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoice;
import is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoiceHome;
import is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferHome;
import java.rmi.RemoteException;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import se.idega.idegaweb.commune.business.CommuneUserBusiness;
import se.idega.idegaweb.commune.message.business.MessageBusiness;
import se.idega.idegaweb.commune.message.data.Message;
import com.idega.block.process.business.CaseBusiness;
import com.idega.block.process.business.CaseBusinessBean;
import com.idega.block.school.business.SchoolBusiness;
import com.idega.block.school.data.School;
import com.idega.block.school.data.SchoolClassMember;
import com.idega.block.school.data.SchoolSeason;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.data.IDOCreateException;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;
import com.idega.util.PersonalIDFormatter;
import com.idega.util.text.Name;


/**
 * 
 * 
 *  Last modified: $Date: 2005/10/03 16:49:09 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public class ServiceOfferBusinessBean extends CaseBusinessBean implements CaseBusiness, ServiceOfferBusiness{

	public SchoolBusiness getSchoolBusiness() {
		try {
			return (SchoolBusiness) getServiceInstance(SchoolBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	public CommuneUserBusiness getCommuneUserBusiness() {
		try {
			return (CommuneUserBusiness) getServiceInstance(CommuneUserBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	protected MessageBusiness getMessageBusiness() {
		try {
			return (MessageBusiness) this.getServiceInstance(MessageBusiness.class);
		}
		catch (RemoteException e) {
			throw new IBORuntimeException(e.getMessage());
		}
	}

	protected ServiceOfferHome getServiceOfferHome() {
		try {
			return (ServiceOfferHome) getIDOHome(ServiceOffer.class);
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}
	
	protected ServiceOfferChoiceHome getServiceOfferChoiceHome() {
		try {
			return (ServiceOfferChoiceHome) getIDOHome(ServiceOfferChoice.class);
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}
	
	public SchoolSeason getOngoingSeason() throws FinderException {
		try {
			return getSchoolBusiness().getSchoolSeasonHome().findCurrentSeason(getSchoolBusiness().getCategoryElementarySchool());
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}
	
	public SchoolSeason getNextSeason() throws FinderException {
		try {
			return getSchoolBusiness().getSchoolSeasonHome().findNextSeason(getSchoolBusiness().getCategoryElementarySchool(), new IWTimestamp().getDate());
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}
	
	public SchoolClassMember getSchoolPlacing(User user, SchoolSeason season) {
		try {
			return getSchoolBusiness().getSchoolClassMemberHome().findLatestByUserAndSchCategoryAndSeason(user, getSchoolBusiness().getCategoryElementarySchool(), season);
		}
		catch (FinderException fe) {
			fe.printStackTrace();
			return null;
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}
	
	
	public ServiceOfferChoice createServiceOfferChoiceAndSendMessage(ServiceOffer offer, User user, School school, SchoolSeason season, String comments, Date[] months, Map monthValues, User performer) throws IDOCreateException {
		try {
			ServiceOfferChoice choice = getServiceOfferChoiceHome().create();
			
			choice.setUser(user);
			
			choice.setOwner(performer);
			//choice.setEmployee(user.equals(performer));
			
			choice.store();
			
			changeCaseStatus(choice, getCaseStatusOpenString(), performer);
			
			
			
			if (!user.equals(performer)) {
				String subject = getLocalizedString("service_offer.message_subject", "You have a service offer waiting for your response");
				String body = getLocalizedString("service_offer.message_body", "You have made a meal choice to {1} for {0}, {2}.");
				
				sendMessageToParents(choice, subject, body);
			}

			return choice;
		}
		catch (CreateException ce) {
			throw new IDOCreateException(ce);
		}
	}

	public void sendMessageToParents(ServiceOfferChoice application, String subject, String body) {
		try {
			User user = application.getUser();
			Object[] arguments = { new Name(user.getFirstName(), user.getMiddleName(), user.getLastName()).getName(getIWApplicationContext().getApplicationSettings().getDefaultLocale(), true), PersonalIDFormatter.format(user.getPersonalID(), getIWApplicationContext().getApplicationSettings().getDefaultLocale()) };

			User appParent = application.getOwner();
			if (getCommuneUserBusiness().getMemberFamilyLogic().isChildInCustodyOf(user, appParent)) {
				Message message = getMessageBusiness().createUserMessage(application, appParent, subject, MessageFormat.format(body, arguments), true);
				message.setParentCase(application);
				message.store();
			}
			else {
				try {
					Collection parents = getCommuneUserBusiness().getMemberFamilyLogic().getCustodiansFor(user);
					Iterator iter = parents.iterator();
					while (iter.hasNext()) {
						User parent = (User) iter.next();
						if (!parent.equals(appParent)) {
							getMessageBusiness().createUserMessage(application, parent, subject, MessageFormat.format(body, arguments), true);
						}
					}
				}
				catch (NoCustodianFound ncf) {
					ncf.printStackTrace();
				}
			}
		}
		catch (RemoteException re) {
			re.printStackTrace();
		}
	}
	
	public School getManagingSchoolForUser(User user) throws RemoteException, FinderException {
		return getCommuneUserBusiness().getFirstManagingSchoolForUser(user);
	}

	public void storeServiceOffer(String name, String paymentType, String choiceOptional, String deadline, String date, String time, String price, String location, String text, String schoolType, String school, String schoolClass, User user) {
		try {
			ServiceOffer offer = getServiceOfferHome().create();
			
			offer.setServiceName(name);
			if("N".equals(choiceOptional)){
				offer.setServiceChoiceAsMandatory();
			}else{
				offer.setServiceChoiceAsOptional();
			}
			offer.setServicePaymentType(paymentType);
			if(deadline!=null){
				offer.setServiceDeadline((new IWTimestamp(deadline)).getTimestamp());
			}
			//TODO use time
			if(date!=null){
				offer.setServiceDate((new IWTimestamp(date)).getTimestamp());
			}
			
			offer.setServicePrice(Double.valueOf(price).doubleValue());
			offer.setServiceText(text);
			offer.setOwner(user);
		
			offer.store();
			
//			ServiceOfferChoice choice = getServiceOfferChoiceHome().create();
//			
//			choice.setUser(user);
//			
//			choice.setOwner(performer);
//			//choice.setEmployee(user.equals(performer));
//			
//			choice.store();
			
			changeCaseStatus(offer, getCaseStatusOpenString(), user);
			
			
			
//			if (!user.equals(performer)) {
//				String subject = getLocalizedString("service_offer.message_subject", "You have a service offer waiting for your response");
//				String body = getLocalizedString("service_offer.message_body", "You have made a meal choice to {1} for {0}, {2}.");
//				
//				sendMessageToParents(choice, subject, body);
//			}
//
//			return choice;
		}
		catch (CreateException ce) {
		//	throw new IDOCreateException(ce);
			ce.printStackTrace();
		}
		
	}
}