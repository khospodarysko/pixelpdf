package com.kostya;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("usage: java -jar pixel-pdf-1.0.jar configs/file-1.json");

        if (args.length != 1) {
            System.out.println("usage: [filename.json]");
            System.exit(1);

        }
        String filename = args[0];

        ObjectMapper objectMapper = new ObjectMapper();
        ConfigFile configFile = objectMapper.readValue(new File(filename), ConfigFile.class);

        File filePdf = new File("pdfs/" + configFile.getName());
        if (filePdf.exists()) {
            filePdf.delete();
        }

        String[] names = {
            "icon-1.png",
            "icon-2.png",
            "icon-3.png",
            "icon-4.jpg",
            "icon-5.png",
            "icon-6.png",
            "icon-7.png",
            "icon-8.png",
            "icon-9.png",
            "icon-10.png",
            "icon-11.png",
            "icon-12.png",
            "icon-13.jpeg",
            "icon-14.jpeg",
            "icon-15.jpeg",
            "icon-16.png",
            "icon-17.png",
            "icon-18.png",
            "icon-19.jpeg",
            "icon-20.png"
        };

        try (PDDocument doc = new PDDocument()) {
            int pageNumber = 1;
            for (ConfigPage configPage : configFile.getPages()) {
                File file = new File("icons/" + names[ThreadLocalRandom.current().nextInt(names.length)]);

                // TODO landscape

                PDPage page = new PDPage(new PDRectangle(configPage.getW(), configPage.getH()));
                doc.addPage(page);

                PDRectangle pageSize = page.getMediaBox();
                float pageWidth = pageSize.getWidth();
                float pageHeight = pageSize.getHeight();

                try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.OVERWRITE, false)) {
                    PDFont font = PDType1Font.HELVETICA;
                    contentStream.setFont(font, 16);
                    contentStream.moveTo(0, pageHeight);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(5, pageHeight - 20);
                    contentStream.showText(String.format("Page: %d. W x H : %d x %d", pageNumber, configPage.getW(), configPage.getH()));
                    contentStream.endText();
                }

                PDImageXObject pdImage = PDImageXObject.createFromFile(file.getPath(), doc);
                try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                    float scaleX = pageWidth / pdImage.getWidth();
                    float scaleY = pageHeight / pdImage.getHeight();
                    float scale = Math.min(scaleX, scaleY);

                    float scaledW = pdImage.getWidth() * scale;
                    float scaledH = pdImage.getHeight() * scale;

                    contentStream.drawImage(pdImage,
                        pageWidth / 2 - scaledW / 2,
                        pageHeight / 2 - scaledH / 2,
                        scaledW, scaledH);
                }

                pageNumber++;
            }
            doc.save("pdfs/" + configFile.getName());
        }
    }
}

class ConfigFile {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ConfigPage> getPages() {
        return pages;
    }

    public void setPages(List<ConfigPage> pages) {
        this.pages = pages;
    }

    private String name;
    private List<ConfigPage> pages;
}

class ConfigPage {
    private int w;

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    private int h;
}
