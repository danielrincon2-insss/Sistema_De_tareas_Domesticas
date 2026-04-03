package com.tareasdomesticas.hogar_service.application.service;

import com.tareasdomesticas.hogar_service.domain.model.Tarea;
import com.tareasdomesticas.hogar_service.domain.port.in.CrearTareaUseCase;
import com.tareasdomesticas.hogar_service.domain.port.out.TareaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CrearTareaService implements CrearTareaUseCase {

    private final TareaRepository tareaRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public CrearTareaService(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    @Override
    public Tarea crearTarea(String nombre, MultipartFile foto, LocalDateTime fechaLimite,
                           Tarea.Dificultad dificultad, Tarea.Prioridad prioridad, Integer hogarId) {

        String fotoPath = null;
        if (foto != null && !foto.isEmpty()) {
            fotoPath = guardarFoto(foto);
        }

        Tarea tarea = new Tarea(nombre, fotoPath, fechaLimite, dificultad, prioridad, hogarId);
        return tareaRepository.save(tarea);
    }

    private String guardarFoto(MultipartFile foto) {
        String contentType = foto.getContentType();
        if (!"image/jpeg".equals(contentType)) {
            throw new IllegalArgumentException("Formato no permitido, debe subir JPG");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID().toString() + ".jpg";
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(foto.getInputStream(), filePath);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la foto", e);
        }
    }
}