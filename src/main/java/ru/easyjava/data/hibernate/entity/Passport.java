package ru.easyjava.data.hibernate.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.time.Period;

@SuppressWarnings("PMD")
@Entity
@Audited
public class Passport extends AbstractIdentifiableObject {
    @Getter
    @Setter
    private String series;

    @Getter
    @Setter
    private String no;

    @Getter
    @Setter
    private LocalDate issueDate;

    @Getter
    @Setter
    private Period validity;

    @Getter
    @Setter
    @OneToOne(optional = false, mappedBy = "passport")
    private Person owner;

    @Override
    public String toString() {
        return "Passport{" +
                "series='" + series + '\'' +
                ", no='" + no + '\'' +
                ", issueDate=" + issueDate +
                ", validity=" + validity +
                ", owner=" + (owner != null ? owner.getLastName() : "None") +
                '}';
    }
}
