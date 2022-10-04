package com.example.evaluacion1.repositories;

import com.example.evaluacion1.entities.AutorizacionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface AutorizacionRepository extends CrudRepository<AutorizacionEntity, Long> {

    @Query(value = "select * from Autorizaciones as a where a.rut = :rut",
            nativeQuery = true)
    AutorizacionEntity getByRut(@Param("rut") String rut);

}