package is.idega.idegaweb.egov.serviceoffer.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.idega.business.IBOLookup;
import com.idega.core.contact.data.Phone;
import com.idega.core.file.util.MimeTypeUtil;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.io.DownloadWriter;
import com.idega.io.MediaWritable;
import com.idega.io.MemoryFileBuffer;
import com.idega.io.MemoryInputStream;
import com.idega.io.MemoryOutputStream;
import com.idega.presentation.IWContext;
import com.idega.user.data.User;
import com.idega.util.IOUtil;
import com.idega.util.PersonalIDFormatter;
import com.idega.util.text.Name;

import is.idega.idegaweb.egov.serviceoffer.data.ServiceOffer;
import is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoice;
import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;

/**
 * Title: Description: Copyright: Copyright (c) 2001 Company: idega multimedia
 *
 * @author <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */
public class ParticipantsXLSWriter extends DownloadWriter implements MediaWritable {

	private MemoryFileBuffer buffer = null;
	private ServiceOfferBusiness business;
	private Locale locale;
	private IWResourceBundle iwrb;

	public static final String PARAMETER_SERVICE_OFFER = "prm_service_offer";

	public ParticipantsXLSWriter() {
	}

	@Override
	public void init(HttpServletRequest req, IWContext iwc) {
		if (iwc == null || !iwc.isLoggedOn()) {
			return;
		}

		try {
			this.locale = iwc.getApplicationSettings().getApplicationLocale();
			this.business = getBusiness(iwc);
			this.iwrb = iwc.getIWMainApplication().getBundle(ServiceOfferConstants.IW_BUNDLE_IDENTIFIER).getResourceBundle(this.locale);

			ServiceOffer offer = this.business.getServiceOffer(new Integer(iwc.getParameter(PARAMETER_SERVICE_OFFER)).intValue());
			Collection choices = this.business.getServiceOfferChoices(offer);

			this.buffer = writeXLS(choices, iwc);
			setAsDownload(iwc, "participants.xls", this.buffer.length());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getMimeType() {
		if (this.buffer != null) {
			return this.buffer.getMimeType();
		}
		return super.getMimeType();
	}

	@Override
	public void writeTo(IWContext iwc, OutputStream out) throws IOException {
		if (this.buffer != null) {
			MemoryInputStream mis = new MemoryInputStream(this.buffer);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while (mis.available() > 0) {
				baos.write(mis.read());
			}
			baos.writeTo(out);
			IOUtil.close(mis);
		}
		else {
			System.err.println("buffer is null");
		}
	}

	public MemoryFileBuffer writeXLS(Collection students, IWContext iwc) throws Exception {
		MemoryFileBuffer buffer = new MemoryFileBuffer();
		MemoryOutputStream mos = new MemoryOutputStream(buffer);
		HSSFWorkbook wb = new HSSFWorkbook();
		if (!students.isEmpty()) {
	    HSSFSheet sheet = wb.createSheet("Participants");
	    sheet.setColumnWidth((short)0, (short) (30 * 256));
	    sheet.setColumnWidth((short)1, (short) (14 * 256));
	    sheet.setColumnWidth((short)2, (short) (30 * 256));
	    sheet.setColumnWidth((short)3, (short) (14 * 256));
	    sheet.setColumnWidth((short)4, (short) (14 * 256));
	    sheet.setColumnWidth((short)5, (short) (14 * 256));

	    HSSFFont font = wb.createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short)12);
	    HSSFCellStyle style = wb.createCellStyle();
	    style.setFont(font);

	    int cellColumn = 0;
	    int cellRow = 0;
			HSSFRow row = sheet.createRow(cellRow++);

			HSSFCell cell = row.createCell((short) cellColumn++);
			cell.setCellValue(this.iwrb.getLocalizedString("service.offer.choice.name", "Child's name"));
			cell.setCellStyle(style);

			cell = row.createCell((short) cellColumn++);
			cell.setCellValue(this.iwrb.getLocalizedString("service.offer.choice.personal_id", "Child's ssn"));
			cell.setCellStyle(style);

			cell = row.createCell((short) cellColumn++);
			cell.setCellValue(this.iwrb.getLocalizedString("service.offer.choice.custodian_name", "Custodian's name"));
			cell.setCellStyle(style);

			cell = row.createCell((short) cellColumn++);
			cell.setCellValue(this.iwrb.getLocalizedString("service.offer.choice.custodian_personal_id", "Custodian's ssn"));
			cell.setCellStyle(style);

			cell = row.createCell((short) cellColumn++);
			cell.setCellValue(this.iwrb.getLocalizedString("service.offer.choice.phone_number", "Phone number"));
			cell.setCellStyle(style);

			cell = row.createCell((short) cellColumn++);
			cell.setCellValue(this.iwrb.getLocalizedString("service.offer.choice.status", "Status"));
			cell.setCellStyle(style);

			cell = row.createCell((short) cellColumn++);
			cell.setCellValue(this.iwrb.getLocalizedString("service.offer.choice.viewed", "Viewed"));
			cell.setCellStyle(style);

			cell = row.createCell((short) cellColumn++);
			cell.setCellValue(this.iwrb.getLocalizedString("service.offer.choice.payment_status", "Payment"));
			cell.setCellStyle(style);

			Iterator iter = students.iterator();
			while (iter.hasNext()) {
				row = sheet.createRow(cellRow++);
				cellColumn = 0;

				ServiceOfferChoice choice = (ServiceOfferChoice) iter.next();
				User user = choice.getUser();
				Name name = new Name(user.getFirstName(), user.getMiddleName(), user.getLastName());

				User owner = choice.getOwner();
				Name ownerName = new Name(owner.getFirstName(), owner.getMiddleName(), owner.getLastName());

				String status = choice.getStatus();
				boolean paid = choice.hasBeenPaidFor();
				boolean viewed = choice.hasBeenViewed();

				row.createCell((short) cellColumn++).setCellValue(name.getName(this.locale, true));
		    row.createCell((short) cellColumn++).setCellValue(PersonalIDFormatter.format(user.getPersonalID(), this.locale));

		    row.createCell((short) cellColumn++).setCellValue(ownerName.getName(this.locale, true));
		    row.createCell((short) cellColumn++).setCellValue(PersonalIDFormatter.format(owner.getPersonalID(), this.locale));

				Collection userPhones = owner.getPhones();
		    cell = row.createCell((short) cellColumn++);
				for (Iterator phones = userPhones.iterator(); phones.hasNext();) {
					Phone phone = (Phone) phones.next();
					String number = phone.getNumber();
					if (number != null && !"".equals(number)) {
						cell.setCellValue(phone.getNumber());
						break;
					}
				}

				row.createCell((short) cellColumn++).setCellValue(this.iwrb.getLocalizedString(status, status));
				row.createCell((short) cellColumn++).setCellValue(viewed ? "X" : "-");
				row.createCell((short) cellColumn++).setCellValue(paid ? "X" : "-");
			}
		}

		wb.write(mos);
		wb.close();

		buffer.setMimeType(MimeTypeUtil.MIME_TYPE_EXCEL_2);
		return buffer;
	}

	protected ServiceOfferBusiness getBusiness(IWApplicationContext iwac) throws RemoteException {
		return IBOLookup.getServiceInstance(iwac, ServiceOfferBusiness.class);
	}
}