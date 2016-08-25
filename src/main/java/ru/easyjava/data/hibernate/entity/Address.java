package ru.easyjava.data.hibernate.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.stream.Collectors;

@SuppressWarnings("PMD")
@Entity
@Audited
public class Address extends AbstractIdentifiableObject {
    @Getter
    @Setter
    private String city;

    @Getter
    @Setter
    private String street;

    @Getter
    @Setter
    private String building;

    @Getter
    @Setter
    @OneToMany(mappedBy = "primaryAddress", fetch = FetchType.EAGER)
    private Collection<Person> tenants;

    @Override
    public String toString() {
        return "Address{" +
                "city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", building='" + building + '\'' +
                ", tenants=" + tenants
                .stream()
                .map(Person::getFirstName)
                .collect(Collectors.joining(",")) +
                '}';
    }
}
