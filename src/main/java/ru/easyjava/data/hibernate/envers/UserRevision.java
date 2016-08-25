package ru.easyjava.data.hibernate.envers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.Entity;

/**
 * Custom revision entity that holds additional data.
 */
@SuppressWarnings("PMD")
@Entity
@RevisionEntity(UserRevisionListener.class)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
class UserRevision extends DefaultRevisionEntity {
    /**
     * Our parent implements serializable.
     */
    private static final long serialVersionUID = 42L;
    /**
     * User who created that revision.
     */
    @Getter
    @Setter
    private String username;
}
