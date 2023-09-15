package br.com.im9.pdf.endpoints;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Table;

import br.com.im9.pdf.structures.PDFPayload;
import br.com.im9.pdf.structures.Payload;

@RestController
public class PDFController {
    private static final Logger logger = LoggerFactory.getLogger(PDFController.class); 

    @PostMapping("/hello")
    public ResponseEntity<Resource> generatePDF(@RequestBody Payload<PDFPayload> payload) {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(os));

        Document document = new Document(pdfDocument);

        Table table = new Table(payload.data.headers.size());

        Style style = new Style();
        style.setMargin(0);
        style.setBorder(Border.NO_BORDER);

        // table.addStyle(style);

        payload.data.headers.forEach((h) -> {table.addHeaderCell(h);});

        payload.data.rows.forEach((r) -> {
            r.forEach((c) -> table.addCell(c));
        });
        document.add(table);
        document.close();

        ByteArrayResource resource = new ByteArrayResource(os.toByteArray());
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(resource.contentLength())
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.attachment()
                        .filename(payload.data.title+".pdf")
                        .build().toString())
            .body(resource);
    } 
    
}
