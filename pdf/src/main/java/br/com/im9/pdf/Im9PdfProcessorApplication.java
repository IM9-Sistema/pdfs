package br.com.im9.pdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import co.elastic.apm.attach.ElasticApmAttacher;

@SpringBootApplication
public class Im9PdfProcessorApplication {

	public static void main(String[] args) {
		ElasticApmAttacher.attach();
		SpringApplication.run(Im9PdfProcessorApplication.class, args);
	}

}
