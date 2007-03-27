/*
 * $Id: ServiceOfferListBlock.java,v 1.13 2007/03/27 09:02:04 laddi Exp $ Created on Oct 2, 2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.presentation;

import is.idega.idegaweb.egov.serviceoffer.business.ParticipantsXLSWriter;
import is.idega.idegaweb.egov.serviceoffer.data.ServiceOffer;
import is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoice;
import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import com.idega.business.IBORuntimeException;
import com.idega.core.contact.data.Phone;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.DownloadLink;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.user.data.User;
import com.idega.util.PersonalIDFormatter;
import com.idega.util.text.Name;

/**
 * A block for viewing and editing a list of service offers
 * 
 * Last modified: $Date: 2007/03/27 09:02:04 $ by $Author: laddi $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.13 $
 */
public class ServiceOfferListBlock extends ServiceOfferBlock implements ServiceOfferConstants {

	private static final String PARAMETER_ACTION = "prm_servoflb_action";
	private static final String PARAMETER_PAID = "prm_servoflb_paid";

	private static final int ACTION_LIST = 1;
	private static final int ACTION_STORE_PAYMENT_INFO = 2;

	public void present(IWContext iwc) {
		String selectedCaseId = null;
		try {
			ServiceOffer offer = null;
			selectedCaseId = iwc.getParameter(getBusiness().getSelectedCaseParameter());
			if (selectedCaseId != null && !"".equals(selectedCaseId)) {
				offer = getBusiness().getServiceOffer(Integer.parseInt(selectedCaseId));
			}

			switch (parseAction(iwc)) {
				case ACTION_LIST:
					showChoiceList(iwc, offer);
					break;
				case ACTION_STORE_PAYMENT_INFO:
					storePaymentInfo(iwc, offer);
					showChoiceList(iwc, offer);
					break;
			}
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			add(localize("service.offer.list.case_id_not_a_number", "Case id was not a number : ") + selectedCaseId);
		}
		catch (FinderException e) {
			e.printStackTrace();
			add(localize("service.offer.list.case_not_found", "The case was not found : ") + selectedCaseId);
		}
	}

	private void showChoiceList(IWContext iwc, ServiceOffer offer) throws RemoteException {
		Form form = createForm(iwc, ACTION_LIST);

		Layer section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);

		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(localize("service.offer.list.service_offer_list", "Below you will find details about the selected service offer and its participants.")));
		// section.add(paragraph);

		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");

		DropdownMenu offers = new DropdownMenu(getBusiness().getSelectedCaseParameter());
		offers.setID("serviceOfferDropdown");
		offers.addMenuElements(getBusiness().getServiceOffers(iwc.getCurrentUser()));
		offers.addMenuElementFirst("", localize("service.offer.list.select_service_offer", "Select service offer"));
		offers.setToSubmit(true);
		offers.keepStatusOnAction(true);

		Layer formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		Label label = new Label(localize("service.offer.application.name_of_service_offer", "Name of service offer"), offers);
		formItem.add(label);
		formItem.add(offers);
		section.add(formItem);

		if (offer != null) {
			addServiceOffer(iwc, section, offer, false);
			section.add(clearLayer);

			form.add(getPrintouts(iwc, offer));
			form.add(getServiceOfferChoiceList(iwc, offer));
			form.add(getLegend());

			Layer buttonLayer = new Layer(Layer.DIV);
			buttonLayer.setStyleClass("buttonLayer");
			form.add(buttonLayer);

			SubmitButton save = new SubmitButton(localize("save", "Save"));
			save.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_STORE_PAYMENT_INFO));

			buttonLayer.add(save);
		}

		add(form);
	}

	private Table2 getServiceOfferChoiceList(IWContext iwc, ServiceOffer offer) throws RemoteException {
		Table2 table = new Table2();
		table.setWidth("100%");
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setStyleClass("adminTable");

		Collection choices = getBusiness().getServiceOfferChoices(offer);

		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();

		TableCell2 cell = row.createHeaderCell();
		cell.setStyleClass("firstColumn");
		cell.add(new Text(localize("service.offer.choice.name", "Child's name")));

		row.createHeaderCell().add(new Text(localize("service.offer.choice.personal_id", "Child's ssn")));
		row.createHeaderCell().add(new Text(localize("service.offer.choice.custodian_name", "Custodian's name")));
		row.createHeaderCell().add(new Text(localize("service.offer.choice.custodian_personal_id", "Custodian's ssn")));
		row.createHeaderCell().add(new Text(localize("service.offer.choice.phone_number", "Phone number")));
		row.createHeaderCell().add(new Text(localize("service.offer.choice.status", "Status")));
		row.createHeaderCell().add(new Text(localize("service.offer.choice.viewed", "Viewed")));

		cell = row.createHeaderCell();
		cell.setStyleClass("lastColumn");
		cell.add(new Text(localize("service.offer.choice.payment_status", "Payment")));

		group = table.createBodyRowGroup();
		int iRow = 1;

		int totalAgreed = 0;
		int totalDenied = 0;
		int totalPaid = 0;
		int totalViewed = 0;
		int total = choices.size();

		String agreedStatusCode = getBusiness().getCaseStatusGranted().getStatus();
		String deniedStatusCode = getBusiness().getCaseStatusDenied().getStatus();

		Iterator iter = choices.iterator();

		while (iter.hasNext()) {
			row = group.createRow();
			ServiceOfferChoice choice = (ServiceOfferChoice) iter.next();
			User user = choice.getUser();
			Name name = new Name(user.getFirstName(), user.getMiddleName(), user.getLastName());

			User owner = choice.getOwner();
			Name ownerName = new Name(owner.getFirstName(), owner.getMiddleName(), owner.getLastName());
			boolean hasAccount = getBusiness().getUserBusiness().hasCitizenAccount(owner);

			String status = choice.getStatus();
			if (agreedStatusCode.equals(status)) {
				totalAgreed++;
			}
			else if (deniedStatusCode.equals(status)) {
				totalDenied++;
			}

			boolean paid = choice.hasBeenPaidFor();
			if (paid) {
				totalPaid++;
			}

			boolean viewed = choice.hasBeenViewed();
			if (viewed) {
				totalViewed++;
			}

			try {
				cell = row.createCell();

				cell.setStyleClass("firstColumn");
				cell.add(new Text(name.getName(iwc.getCurrentLocale())));

				row.createCell().add(new Text(user.getPersonalID() != null ? PersonalIDFormatter.format(user.getPersonalID(), iwc.getCurrentLocale()) : "-"));

				row.createCell().add(new Text(ownerName.getName(iwc.getCurrentLocale())));
				row.createCell().add(new Text(owner.getPersonalID() != null ? PersonalIDFormatter.format(owner.getPersonalID(), iwc.getCurrentLocale()) : "-"));

				Collection userPhones = owner.getPhones();
				TableCell2 phoneCell = row.createCell();
				for (Iterator phones = userPhones.iterator(); phones.hasNext();) {
					Phone phone = (Phone) phones.next();
					String number = phone.getNumber();
					if (number != null && !"".equals(number)) {
						phoneCell.add(new Text(phone.getNumber()));
						break;
					}
				}

				row.createCell().add(new Text(localize(status, status)));
				row.createCell().add(new Text((viewed) ? "X" : "-"));

				CheckBox hasPaid = new CheckBox(PARAMETER_PAID, choice.getPrimaryKey().toString());
				hasPaid.setChecked(paid);
				cell = row.createCell();
				cell.setStyleClass("lastColumn");
				cell.add(hasPaid);

				if (hasAccount) {
					row.setStyleClass("hasAccount");
				}

				if (iRow % 2 == 0) {
					row.setStyleClass("evenRow");
				}
				else {
					row.setStyleClass("oddRow");
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}

			if (iter.hasNext()) {
				iRow++;
			}
		}

		group = table.createFooterRowGroup();
		row = group.createRow();
		cell = row.createCell();
		cell.setStyleClass("firstColumn");
		cell.setColumnSpan(5);
		cell.add(new Text(localize("service.offer.choice.total", "Total")));

		row.createCell().add(new Text(String.valueOf(totalAgreed) + "/" + String.valueOf(totalDenied) + "/" + total));
		row.createCell().add(new Text(String.valueOf(totalViewed) + "/" + total));

		cell = row.createCell();
		cell.setStyleClass("lastColumn");
		cell.add(new Text(String.valueOf(totalPaid) + "/" + total));

		return table;
	}

	private Form createForm(IWContext iwc, int actionPhase) throws RemoteException {
		Form form = new Form();
		form.setID("serviceOfferForm");
		form.setStyleClass("adminForm");
		form.addParameter(PARAMETER_ACTION, actionPhase);

		return form;
	}

	private Lists getLegend() {
		Lists list = new Lists();
		list.setStyleClass("legend");

		ListItem item = new ListItem();
		item.setStyleClass("hasAccount");
		item.add(new Text(this.getResourceBundle().getLocalizedString("service.offer.choice.has_account", "Parent has citizen account")));
		list.add(item);

		return list;
	}

	private Layer getPrintouts(IWContext iwc, ServiceOffer offer) throws RemoteException {
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("printIcons");

		DownloadLink link = new DownloadLink(getBundle().getImage("xls.gif"));
		link.setStyleClass("xls");
		link.setTarget(Link.TARGET_NEW_WINDOW);
		link.setMediaWriterClass(ParticipantsXLSWriter.class);
		link.addParameter(ParticipantsXLSWriter.PARAMETER_SERVICE_OFFER, offer.getPrimaryKey().toString());

		layer.add(link);

		return layer;
	}

	private int parseAction(IWContext iwc) {
		int action = ACTION_LIST;
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			action = Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}

		return action;
	}

	private void storePaymentInfo(IWContext iwc, ServiceOffer offer) throws RemoteException {
		String[] choices = iwc.getParameterValues(PARAMETER_PAID);
		getBusiness().storePaymentInfo(offer, choices);
	}
}