package br.com.im9.pdf.endpoints;

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

import br.com.im9.pdf.providers.PDFProvider;
import br.com.im9.pdf.structures.PDFPayload;
import br.com.im9.pdf.structures.Payload;

@RestController
public class PDFController {
    private static final Logger logger = LoggerFactory.getLogger(PDFController.class);

    @PostMapping("/pdfs/create")
    public ResponseEntity<Resource> generatePDF(@RequestBody Payload<PDFPayload> payload) {
        logger.info("Generating PDF");
        try {
            ByteArrayResource resource = new ByteArrayResource(PDFProvider.generatePDF(payload.data));
            logger.info("Finished generating PDF");
            
            return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(resource.contentLength())
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.attachment()
                        .filename(payload.data.title+".pdf")
                        .build().toString())
            .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                .internalServerError()
                .body(null);
        }
    }

}
