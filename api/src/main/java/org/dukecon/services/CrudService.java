package org.dukecon.services;

import java.util.Collection;

/**
 * Created by christoferdutz on 02.08.16.
 */
public interface CrudService<T> {

    void create(T obj);
    T read(String id);
    void update(T obj);
    void delete(T obj);
    void delete(String id);

    Collection<T> list();

}
