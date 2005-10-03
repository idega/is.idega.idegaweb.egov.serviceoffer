/*
 * $Id: ServiceOfferApplication.java,v 1.2 2005/10/03 10:21:34 eiki Exp $
 * Created on Oct 2, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.presentation;

import java.rmi.RemoteException;
import javax.ejb.FinderException;
import com.idega.block.school.data.School;
import com.idega.block.school.data.SchoolSeason;
import com.idega.business.IBORuntimeException;
import com.idega.core.builder.data.ICPage;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.SubmitButton;
import com.idega.user.data.User;

/**
 * An application for sending a service offer(description), that may have a price, to a citizen or a group of citizens
 * that then have to approve it.
 * 
 *  Last modified: $Date: 2005/10/03 10:21:34 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public class ServiceOfferApplication extends ServiceOfferBlock {
	
	private static final String PARAMETER_ACTION = "prm_action";
	
	private static final int ACTION_PHASE_ONE = 1;
	private static final int ACTION_PHASE_TWO = 2;
	private static final int ACTION_OVERVIEW = 4;
	private static final int ACTION_SAVE = 5;

	private User user;
	private School school;
	private SchoolSeason season;

	/* (non-Javadoc)
	 * @see se.idega.idegaweb.commune.school.meal.presentation.MealBlock#present(com.idega.presentation.IWContext)
	 */
	public void present(IWContext iwc) {
		try {
			try {
				season = getBusiness().getOngoingSeason();
			}
			catch (FinderException fe) {
				try {
					season = getBusiness().getNextSeason();
				}
				catch (FinderException fex) {
					add(getErrorText(localize("no_season_found", "No season found.")));
					return;
				}
			}
			
			switch (parseAction(iwc)) {
				case ACTION_PHASE_ONE:
					showPhaseOne(iwc);
					break;
					
				case ACTION_PHASE_TWO:
					showPhaseTwo(iwc);
					break;

				case ACTION_OVERVIEW:
					showOverview(iwc);
					break;

				case ACTION_SAVE:
					save(iwc);
					break;
}
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}
	
	/**
	 * Shows the form to create an Service Offer
	 * @param iwc
	 * @throws RemoteException
	 */
	private void showPhaseOne(IWContext iwc) throws RemoteException {
		Form form = createForm(iwc, ACTION_PHASE_ONE);
		
	//	form.add(getPersonInfo(iwc, user, school, group));
		
		Layer layer = new Layer(Layer.DIV);
		layer.setID("phasesDiv");
		form.add(layer);
		
		layer.add(new Heading1(localize("service.offer.application.create_a_service_offer", "Create a new service offer step 1 of 3")));
		
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(localize("service.offer.application.text", "Here you can define a service offer and in the next step you can define the recipients.")));
		layer.add(paragraph);
		
		
		
		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonDiv");
		layer.add(buttonLayer);
		
		SubmitButton next = new SubmitButton(localize("next", "Next"));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_PHASE_TWO));
		buttonLayer.add(next);

		add(form);
	}
	
	private void showPhaseTwo(IWContext iwc) throws RemoteException {
		Form form = createForm(iwc, ACTION_PHASE_TWO);
		
		Layer layer = new Layer(Layer.DIV);
		layer.setID("phasesDiv");
		form.add(layer);
		
		layer.add(new Heading1(localize("service.offer.application.create_a_service_offer2", "Create a new service offer step 2 of 3")));
		
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(localize("service.offer.application.select_recipients", "Please select the recipients of the service offer.")));
		layer.add(paragraph);
		
		
		Layer clear = new Layer(Layer.DIV);
		clear.setStyleClass("Clear");
		layer.add(clear);
		
		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonDiv");
		layer.add(buttonLayer);
		
		SubmitButton previous = new SubmitButton(localize("previous", "Previous"));
		previous.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_PHASE_ONE));
		SubmitButton next = new SubmitButton(localize("next", "Next"));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_OVERVIEW));
		buttonLayer.add(previous);
		buttonLayer.add(next);

		add(form);
	}
	
	private void showOverview(IWContext iwc) throws RemoteException {
		Form form = createForm(iwc, ACTION_OVERVIEW);
		
		Layer layer = new Layer(Layer.DIV);
		layer.setID("phasesDiv");
		form.add(layer);
		
		layer.add(new Heading1(localize("service.offer.application.create_a_service_offer3", "Create a new service offer step 3 of 3")));
		
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(localize("service.offer.application.overview", "Please confirm the information below is correct.")));
		layer.add(paragraph);
		

		Layer clear = new Layer(Layer.DIV);
		clear.setStyleClass("Clear");
		layer.add(clear);
				
		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonDiv");
		layer.add(buttonLayer);
		
		SubmitButton save = new SubmitButton(localize("save", "Save"));
		save.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_SAVE));
		//save.setDisabled(!canSave);
		SubmitButton previous = new SubmitButton(localize("previous", "Previous"));
		previous.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_PHASE_TWO));
		buttonLayer.add(previous);
		buttonLayer.add(save);

		add(form);
	}
	
	private void save(IWContext iwc) throws RemoteException {
		
//		try {
		//	getBusiness().storeChoice(null, user, school, season, comments, months, values, iwc.getCurrentUser());
			
			Layer layer = new Layer(Layer.DIV);
			layer.setID("phasesDiv");
			add(layer);
			
			layer.add(new Heading1(localize("service.offer.application.saved", "Finished creating a service offer")));
			
			Paragraph paragraph = new Paragraph();
			paragraph.add(new Text(localize("service.offer.application.saved.text", "Thank you, your service offer has been saved and sent to the corresponding recipients.")));
			layer.add(paragraph);
			
			ICPage homePage = null;
			try {
				homePage = getUserBusiness(iwc).getHomePageForUser(getUser(iwc));
			}
			catch (FinderException e) {
			//no homepage for user??
				e.printStackTrace();
			}
			
			if (homePage!= null) {
				Layer buttonLayer = new Layer(Layer.DIV);
				buttonLayer.setStyleClass("buttonDiv");
				layer.add(buttonLayer);
				
				GenericButton home = new GenericButton(localize("my_page", "My page"));
				home.setPageToOpen(homePage);
			}
//		}
//		catch (IDOCreateException ice) {
//			ice.printStackTrace();
//		}
	}
	
	private Form createForm(IWContext iwc, int actionPhase) {
		Form form = new Form();
		form.setID("serviceOfferForm");
//		if (actionPhase != ACTION_PHASE_ONE) {
//			form.maintainParameter(PARAMETER_MONTH);
//		}
//		if (actionPhase != ACTION_PHASE_THREE) {
//			form.maintainParameter(PARAMETER_COMMENTS);
//		}
		
		form.addParameter(PARAMETER_ACTION, actionPhase);
		
		return form;
	}
	
	private int parseAction(IWContext iwc) {
		int action = ACTION_PHASE_ONE;
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			action = Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}
		
		return action;
	}
	
	private User getUser(IWContext iwc) throws RemoteException {
		return iwc.getCurrentUser();
	}
	
}