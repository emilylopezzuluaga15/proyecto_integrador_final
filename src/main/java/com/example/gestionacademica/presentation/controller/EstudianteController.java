package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.EstudianteServices;
import com.example.gestionacademica.presentation.dto.AlCrear;
import com.example.gestionacademica.presentation.dto.EstudianteDtos.EstudianteRequest;
import com.example.gestionacademica.presentation.dto.EstudianteDtos.EstudianteResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Listado: personal. Cada estudiante puede ver su propio registro. Cambios: ADMINISTRATIVO y COORDINADOR
@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteServices estudianteServices;

    public EstudianteController(EstudianteServices estudianteServices) {
        this.estudianteServices = estudianteServices;
    }

    @PostMapping
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<EstudianteResponse> crear(@Validated(AlCrear.class) @RequestBody EstudianteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EstudianteResponse.from(estudianteServices.crear(request.toEntity())));
    }

    @GetMapping
    @PreAuthorize("@acceso.esPersonal(authentication)")
    public List<EstudianteResponse> listar() {
        return estudianteServices.obtenerTodos().stream().map(EstudianteResponse::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@acceso.puedeVerEstudiante(authentication, #id)")
    public EstudianteResponse obtener(@PathVariable String id) {
        return EstudianteResponse.from(estudianteServices.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public EstudianteResponse actualizar(@PathVariable String id, @Valid @RequestBody EstudianteRequest request) {
        return EstudianteResponse.from(estudianteServices.actualizar(id, request.toEntity()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        estudianteServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
