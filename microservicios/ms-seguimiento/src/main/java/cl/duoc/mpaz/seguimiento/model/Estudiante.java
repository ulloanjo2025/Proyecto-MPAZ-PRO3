package cl.duoc.mpaz.seguimiento.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// Un estudiante registrado por el docente (datos ficticios)
@Entity
@Table(name = "estudiantes", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código es obligatorio")
    private String username;

    @NotBlank(message = "El nombre es obligatorio")
    private String fullName;

    @NotBlank(message = "El PIN es obligatorio")
    @Pattern(regexp = "[0-9]{4}", message = "El PIN debe tener 4 números")
    private String pin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
