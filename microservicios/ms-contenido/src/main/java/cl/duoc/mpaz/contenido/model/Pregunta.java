package cl.duoc.mpaz.contenido.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Una pregunta con 3 alternativas, vinculada a un OA y a una dificultad
@Entity
@Table(name = "preguntas")
public class Pregunta {

    @Id
    private Integer id;

    @NotBlank
    private String objectiveCode;

    @NotNull
    @Min(1)
    @Max(3)
    private Integer difficulty;

    @NotBlank
    @Column(length = 1000)
    private String statement;

    @NotBlank
    @Column(length = 500)
    private String optionA;

    @NotBlank
    @Column(length = 500)
    private String optionB;

    @NotBlank
    @Column(length = 500)
    private String optionC;

    @NotNull
    @Min(0)
    @Max(2)
    private Integer correctIndex;

    @NotBlank
    @Column(length = 1000)
    private String explanation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getObjectiveCode() {
        return objectiveCode;
    }

    public void setObjectiveCode(String objectiveCode) {
        this.objectiveCode = objectiveCode;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public Integer getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(Integer correctIndex) {
        this.correctIndex = correctIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
