package co.dazma.api.shared.infrastructure.output.repositories.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "municipalities", schema = "app_shared")
public class MunicipalityEntityJPA {

    @Id
    @Column(length = 5)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String department;

    protected MunicipalityEntityJPA() {}

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }
}
