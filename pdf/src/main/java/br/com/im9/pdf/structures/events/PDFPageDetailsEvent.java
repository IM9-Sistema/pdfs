package br.com.im9.pdf.structures.events;

import com.itextpdf.commons.actions.IEvent;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.VerticalAlignment;

public class PDFPageDetailsEvent implements IEventHandler{
    private IBlockElement header;
    private Integer pageNumber;

    public PDFPageDetailsEvent(IBlockElement header) {
        super();
        this.header = header;
        this.pageNumber = 0;
    }

    private Paragraph getPageNumber() {
        this.pageNumber += 1;
        return new Paragraph("Página "+pageNumber)
            .setFontSize(10.25f);
    }

    @Override
    public void handleEvent(Event event) {
        if (!(event instanceof PdfDocumentEvent)) {
            return;
        }

        PdfDocumentEvent pdfDocumentEvent = (PdfDocumentEvent) event;

        PdfPage page = pdfDocumentEvent.getPage();
        Rectangle pageSize = page.getPageSize();
        PdfCanvas pdfCanvas = new PdfCanvas(page);
        Canvas canvas = new Canvas(pdfCanvas, pageSize);
        canvas.add(getPageNumber().setHeight(pageSize.getHeight()).setVerticalAlignment(VerticalAlignment.BOTTOM));
        canvas.flush();
        canvas.close();
        canvas = new Canvas(pdfCanvas, pageSize);
        canvas.add(this.header);
        canvas.flush();
        canvas.close();
    }
    
}
