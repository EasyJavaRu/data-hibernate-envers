package ru.easyjava.data.hibernate.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Common 'id' part of all entities.
 */
@SuppressWarnings("PMD")
@MappedSuperclass
@ToString
public abstract class AbstractIdentifiableObject {
    /**
     * Common id field.
     */
    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;
}
