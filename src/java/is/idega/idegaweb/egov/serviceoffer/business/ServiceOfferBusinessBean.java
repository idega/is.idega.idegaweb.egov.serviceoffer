/*
 * $Id: ServiceOfferBusinessBean.java,v 1.10 2006/03/20 09:44:16 laddi Exp $
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
import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import se.idega.idegaweb.commune.business.CommuneUserBusiness;
import se.idega.idegaweb.commune.message.business.CommuneMessageBusiness;
import com.idega.block.process.business.CaseBusiness;
import com.idega.block.process.business.CaseBusinessBean;
import com.idega.block.process.data.Case;
import com.idega.block.process.message.data.Message;
import com.idega.block.school.business.SchoolBusiness;
import com.idega.block.school.data.School;
import com.idega.block.school.data.SchoolClass;
import com.idega.block.school.data.SchoolClassMember;
import com.idega.block.school.data.SchoolSeason;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOCreateException;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.PersonalIDFormatter;
import com.idega.util.text.Name;


/**
 * 
 * 
 *  Last modified: $Date: 2006/03/20 09:44:16 $ by $Author: laddi $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.10 $
 */
public class ServiceOfferBusinessBean extends CaseBusinessBean implements CaseBusiness, ServiceOfferBusiness, ServiceOfferConstants{

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

	protected CommuneMessageBusiness getCommuneMessageBusiness() {
		try {
			return (CommuneMessageBusiness) this.getServiceInstance(CommuneMessageBusiness.class);
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
	
	
	public ServiceOfferChoice createServiceOfferChoiceAndSendMessage(ServiceOffer offer, User custodian, User user, User performer, boolean isOptional) throws IDOCreateException {
		try {
			ServiceOfferChoice choice = getServiceOfferChoiceHome().create();
			choice.setUser(user);
			choice.setOwner(custodian);
			choice.setHandler(performer.getPrimaryGroup());
			choice.setParentCase(offer);
			choice.store();
			
			changeCaseStatus(choice, getCaseStatusOpenString(), performer);

			if(isOptional){
				String subject = getLocalizedString("service_offer.message_subject_optional", "You have received a service offer that awaits your response");
				String body = getLocalizedString("service_offer.message_body_optional", "The service offer: \"{2}\", has been made that needs your approval for {0}, {1}. Please click on case nr.{3} in your case list for more info.");
				sendMessageToParents(choice, offer, subject, body);
			}
			else{
				String subject = getLocalizedString("service_offer.message_subject_mandatory", "You have received a mandatory service offer");
				String body = getLocalizedString("service_offer.message_body_mandatory", "The service offer: \"{2}\", has been made for {0}, {1}. Please click on case nr.{3} in your case list for more info.");
				sendMessageToParents(choice, offer,subject, body);
			}

			return choice;
		}
		catch (CreateException ce) {
			throw new IDOCreateException(ce);
		}
	}

	public void sendMessageToParents(ServiceOfferChoice application, ServiceOffer offer, String subject, String body) {
		try {
			User user = application.getUser();
			Object[] arguments = { new Name(user.getFirstName(), user.getMiddleName(), user.getLastName()).getName(getIWApplicationContext().getApplicationSettings().getDefaultLocale(), true),
					PersonalIDFormatter.format(user.getPersonalID(), getIWApplicationContext().getApplicationSettings().getDefaultLocale()),
					offer.getServiceName(),
					application.getPrimaryKey()};

			User appParent = application.getOwner();
			if (getCommuneUserBusiness().getMemberFamilyLogic().isChildInCustodyOf(user, appParent)) {
				Message message = getCommuneMessageBusiness().createUserMessage(application, appParent, subject, MessageFormat.format(body, arguments), true);
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
							getCommuneMessageBusiness().createUserMessage(application, parent, subject, MessageFormat.format(body, arguments), true);
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

	public void storeServiceOffer(String name, String paymentType, String choiceOptional, String deadline, String date, String time, String price, String location, String text, String[] schoolType, String[] school, String[] schoolClass, User performer) {
		try {
			ServiceOffer offer = getServiceOfferHome().create();
			
			offer.setServiceName(name);
			if("N".equals(choiceOptional)){
				offer.setServiceChoiceAsMandatory();
			}else{
				offer.setServiceChoiceAsOptional();
			}
			
			offer.setServicePaymentType(paymentType);
			if(deadline!=null && !"".equals(deadline)){
				offer.setServiceDeadline((new IWTimestamp(deadline)).getTimestamp());
			}
			
			if(date!=null && !"".equals(date)){			
				if(time!=null && !"".equals(time)){
					date+= " "+time;
				}
				IWTimestamp stamp = new IWTimestamp(date);
				offer.setServiceDate(stamp.getTimestamp());
			}
						
			offer.setServicePrice(Double.valueOf(price).doubleValue());
			offer.setServiceText(text);
			offer.setOwner(performer);
		
			offer.store();
			
			
			changeCaseStatus(offer, getCaseStatusOpenString(), performer);
			
						
			if(schoolClass!=null && schoolClass.length>0){
				try {
					for (int i = 0; i < schoolClass.length; i++) {
						String zeClass = schoolClass[i];
						
						SchoolClass theClass = getSchoolBusiness().getSchoolClassHome().findByPrimaryKey(zeClass);
						
						try {
							offer.addSchoolClass(theClass);
						}
						catch (IDOAddRelationshipException e) {
							e.printStackTrace();
						}
						
						Collection pupils = getSchoolBusiness().getSchoolClassMemberHome().findAllBySchoolClass(theClass);
						for (Iterator iter = pupils.iterator(); iter.hasNext();) {
							SchoolClassMember member = (SchoolClassMember) iter.next();
							User student = member.getStudent();
							
							//offer.addGroup(student);
							
							User custodian = getCommuneUserBusiness().getCustodianForChild(student);
							createServiceOfferChoiceAndSendMessage(offer, custodian, student, performer, offer.isServiceChoiceOptional());
						}
					}
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
				catch (FinderException e) {
					e.printStackTrace();
				}

			}
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
	
	public ServiceOffer getServiceOffer(Integer caseID) throws FinderException {
		return getServiceOfferHome().findByPrimaryKey(caseID);
	}
	
	public ServiceOffer getServiceOffer(int caseID) throws FinderException {
		return getServiceOffer(new Integer(caseID));
	}

	public ServiceOfferChoice getServiceOfferChoice(int caseID) throws FinderException {
		return getServiceOfferChoice(new Integer(caseID));
	}
	
	public ServiceOfferChoice getServiceOfferChoice(Integer caseID) throws FinderException {
		return getServiceOfferChoiceHome().findByPrimaryKey(caseID);
	}

	/**
	 * Can be overrided in subclasses
	 */
	protected String getBundleIdentifier() {
		return ServiceOfferConstants.IW_BUNDLE_IDENTIFIER;
	}

	public void setServiceChoiceAsViewed(ServiceOfferChoice choice){
		choice.setAsViewed();
		choice.store();
	}
	
	public void changeServiceOfferChoiceStatus(ServiceOfferChoice choice, boolean accepts, User performer){
		if(accepts){
			changeCaseStatus(choice, getCaseStatusGranted(), performer);
		}else{
			changeCaseStatus(choice, getCaseStatusDenied(), performer);
		}
	}
	

	/**
	 * @return Returns a collection of ServiceOfferChoice cases
	 */
	public Collection getServiceOfferChoices(ServiceOffer offer){
		Collection cases =  offer.getChildren();
		List choices = new ArrayList();
		for (Iterator iter = cases.iterator(); iter.hasNext();) {
			
			Case generalCase = (Case) iter.next();
			String caseCode = generalCase.getCode();
			if(CASE_CODE_KEY_SERVICE_OFFER.equals(caseCode)){
				try {
					choices.add(getServiceOfferChoice((Integer)generalCase.getPrimaryKey()));
				}
				catch (EJBException e) {
					e.printStackTrace();
				}
				catch (FinderException e) {
					e.printStackTrace();
				}
			}
		}
		
		return choices;
	}
	
	/**
	 * @return Returns a collection of ServiceOffer cases for the owner user
	 */
	public Collection getServiceOffers(User owner){
		Collection cases;
		try {
			cases = getAllActiveCasesForUser(owner, CASE_CODE_KEY_SERVICE_OFFER_PARENT);
		}
		catch (FinderException e1) {
			//e1.printStackTrace();
			return ListUtil.getEmptyList();
		}
		
		List offers = new ArrayList();
		for (Iterator iter = cases.iterator(); iter.hasNext();) {
			
			Case generalCase = (Case) iter.next();
		
				try {
					offers.add(getServiceOffer((Integer)generalCase.getPrimaryKey()));
				}
				catch (EJBException e) {
					e.printStackTrace();
				}
				catch (FinderException e) {
					e.printStackTrace();
				}
		
		}
		
		return offers;
	}
	
	public void storePaymentInfo(ServiceOffer offer, String[] offerChoices) {
		Collection choices = getServiceOfferChoices(offer);
		
		Collection paidChoices = new ArrayList();
		if (offerChoices != null) {
			for (int i = 0; i < offerChoices.length; i++) {
				try {
					ServiceOfferChoice choice = getServiceOfferChoice(new Integer(offerChoices[i]));
					choice.setAsPaidFor();
					choice.store();
					paidChoices.add(choice);
				}
				catch (FinderException fe) {
					fe.printStackTrace();
				}
			}
		}
		choices.removeAll(paidChoices);
		
		Iterator iter = choices.iterator();
		while (iter.hasNext()) {
			ServiceOfferChoice choice = (ServiceOfferChoice) iter.next();
			choice.setAsUnPaidFor();
			choice.store();
		}
	}
}