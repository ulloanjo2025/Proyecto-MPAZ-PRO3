package cl.duoc.mpaz.contenido.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// Un Objetivo de Aprendizaje (OA) con su material de estudio
@Entity
@Table(name = "objetivos")
public class Objetivo {

    @Id
    @NotBlank
    private String code;

    @NotBlank
    private String subjectId;

    @NotBlank
    private String unitName;

    @NotBlank
    private String title;

    @NotBlank
    @Column(length = 2000)
    private String summary;

    private String readingTitle;

    @Column(length = 4000)
    private String readingText;

    @NotBlank
    @Pattern(regexp = "PENDIENTE|AUTORIZADO|RECHAZADO", message = "El estado debe ser PENDIENTE, AUTORIZADO o RECHAZADO")
    private String status = "PENDIENTE";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getReadingTitle() {
        return readingTitle;
    }

    public void setReadingTitle(String readingTitle) {
        this.readingTitle = readingTitle;
    }

    public String getReadingText() {
        return readingText;
    }

    public void setReadingText(String readingText) {
        this.readingText = readingText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
