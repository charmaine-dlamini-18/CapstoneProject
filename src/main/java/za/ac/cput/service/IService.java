package za.ac.cput.service;
/*
IService.java
Service interface
Author: Sabelo Ceza - 220094489
Date: 17/06/2026
 */
public interface IService<T, ID> {

    T create(T t);

    T read(ID id);

    T update(T t);

    boolean delete(ID id);
}
