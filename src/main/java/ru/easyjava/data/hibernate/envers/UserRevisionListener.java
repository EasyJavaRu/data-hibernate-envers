package ru.easyjava.data.hibernate.envers;

import org.hibernate.envers.RevisionListener;

/**
 * Custom revision entity processor.
 */
class UserRevisionListener implements RevisionListener {
    /**
     * Static user name for test purposes.
     */
    private final static String USERNAME = "vpupkin";

    /**
     * Here we fill our custom revision with data.
     * @param o Pre-created revision object.
     */
    @Override
    public void newRevision(Object o) {
        UserRevision r= (UserRevision) o;
        r.setUsername(USERNAME);
    }
}
