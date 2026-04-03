package com.tareasdomesticas.hogar_service.infrastructure.adapter.out;

import com.tareasdomesticas.hogar_service.domain.model.Hogar;
import com.tareasdomesticas.hogar_service.domain.port.out.HogarRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaHogarRepository extends JpaRepository<Hogar, Integer>, HogarRepository {

    @Override
    default Hogar guardar(Hogar hogar) {
        return save(hogar);
    }

    @Override
    @Query("SELECT h FROM Hogar h JOIN h.usuarios u WHERE u.idUsuario = :usuarioId")
    Optional<Hogar> buscarPorUsuarioId(@Param("usuarioId") Integer usuarioId);
}