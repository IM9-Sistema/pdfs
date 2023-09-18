FROM maven:latest

WORKDIR /pdfs/.

COPY . .

RUN mvn install

CMD java -jar target/pdf_generator.jar