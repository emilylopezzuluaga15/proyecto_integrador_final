package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.InscripcionServices;
import com.example.gestionacademica.domain.models.Inscripcion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionServices inscripcionServices;

    public InscripcionController(InscripcionServices inscripcionServices) {
        this.inscripcionServices = inscripcionServices;
    }

    @PostMapping
    public ResponseEntity<Inscripcion> crear(@Valid @RequestBody Inscripcion inscripcion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inscripcionServices.crear(inscripcion));
    }

    @GetMapping
    public List<Inscripcion> listar() {
        return inscripcionServices.obtenerTodos();
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public List<Inscripcion> listarPorEstudiante(@PathVariable String idEstudiante) {
        return inscripcionServices.obtenerPorEstudiante(idEstudiante);
    }

    @GetMapping("/grupo/{idGrupo}")
    public List<Inscripcion> listarPorGrupo(@PathVariable String idGrupo) {
        return inscripcionServices.obtenerPorGrupo(idGrupo);
    }

    @GetMapping("/{id}")
    public Inscripcion obtener(@PathVariable Long id) {
        return inscripcionServices.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public Inscripcion actualizar(@PathVariable Long id, @Valid @RequestBody Inscripcion datos) {
        return inscripcionServices.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inscripcionServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
