package br.com.im9.pdf.providers;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.commons.utils.Base64;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import br.com.im9.pdf.structures.PDFPayload;
import br.com.im9.pdf.structures.events.PDFPageDetailsEvent;

public class PDFProvider {

	private static final Logger logger = LoggerFactory.getLogger(PDFProvider.class);

	protected static float getHeaderFontSize() {
		return 10f;
	}

	protected static Cell getCell(String content) {
		return new Cell()
				.setFontSize(8f)
				.setBorder(Border.NO_BORDER)
				.setTextAlignment(TextAlignment.CENTER)
				.add(new Paragraph(content));
	}

	protected static Cell getHeaderCell(String content) {
		return getCell(content)
				.setBold()
				.setFontSize(getHeaderFontSize());
	}

	protected static Style getTableStyle() {
		return new Style()
				.setMargin(0)
				.setBorder(Border.NO_BORDER)
				.setBorderRadius(new BorderRadius(5))
				.setHorizontalAlignment(HorizontalAlignment.CENTER);
	}

	protected static Table getTable(Integer columnSize) {
		return new Table(UnitValue.createPercentArray(columnSize), true)
				.addStyle(getTableStyle());
	}

	public static Image getImage(byte[] bytes) {
		Image img = new Image(ImageDataFactory.create(bytes))
				.scale(0.25f, 0.25f)
				.setHorizontalAlignment(HorizontalAlignment.LEFT);
		return img;
	}

	public static Image getImage(String b64) {
		return getImage(Base64.decode(b64));
	}

	public static Paragraph getTitle(String content) {
		return new Paragraph(content)
				.setBold()
				.setFontSize(getHeaderFontSize() + 1.5f)
				.setPaddingLeft(1.25f)
				.setWidth(UnitValue.createPercentValue(100f))
				.setTextAlignment(TextAlignment.CENTER);
	}

	public static Paragraph getDescription(String content) {
		return new Paragraph(content)
				.setFontSize(getHeaderFontSize())
				.setPaddingLeft(1.25f)
				.setHorizontalAlignment(HorizontalAlignment.LEFT);
	}

	public static Table getHeader(String title, String description, String logo) {
		return getTable(3)
				.setMarginTop(15f)
				.addCell(

						new Cell()
						.add(
							getImage(logo)		
							.setHorizontalAlignment(HorizontalAlignment.CENTER)					
							)
						.setHorizontalAlignment(HorizontalAlignment.CENTER)					
						.setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setWidth(UnitValue.createPercentValue(33))
						.setBorder(Border.NO_BORDER)
				)

				.addCell(
						new Cell()
							.add(
								getTitle(title)
						)
						.setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setWidth(UnitValue.createPercentValue(33))
						.setBorder(Border.NO_BORDER)
				)
				.addCell(
						new Cell()
						.add(
							getDescription(description)
						)
						.setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setWidth(UnitValue.createPercentValue(33))
						.setBorder(Border.NO_BORDER))
				.setWidth(UnitValue.createPercentValue(100));
	}

	public static byte[] generatePDF(PDFPayload pdfData) {
		logger.debug("Starting PDF generation process");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PdfDocument pdfDocument = new PdfDocument(new PdfWriter(os));
		Table header = getHeader(pdfData.title, pdfData.description, pdfData.logo);

		pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, new PDFPageDetailsEvent(header));
		pdfDocument.setDefaultPageSize(PageSize.A4.rotate());
		Document document = new Document(pdfDocument);
		document.setTopMargin(125f);

		document.setFontSize(7);

		logger.debug("Including heading");

		logger.debug("Creating table");
		Table table = getTable(pdfData.headers.size());
		table.setAutoLayout();
		pdfData.headers.forEach((h) -> {
			table.addHeaderCell(getHeaderCell(h));
		});

		document.add(table);
		logger.debug("Adding rows");
		for (int i = 0; i < pdfData.rows.size(); i++) {
			table.flush();
			pdfData.rows.get(i).forEach((c) -> table.addCell(getCell(c)));

		}

		table.complete();
		document.close();

		return os.toByteArray();
	}
}
