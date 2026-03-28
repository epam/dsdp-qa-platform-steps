package platform.qa.data.entities;

public enum ExcerptFormats {
    PDF("pdf"),
    DOCX("docx"),
    CSV("csv");

    private String formatName;

    ExcerptFormats(String formatName) {
        this.formatName = formatName;
    }

    public String getFormatName() {
        return formatName;
    }
}
