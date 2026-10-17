package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.AsignacionMateriaProfesorServices;
import com.example.gestionacademica.domain.models.AsignacionMateriaProfesor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones-materia-profesor")
public class AsignacionMateriaProfesorController {

    private final AsignacionMateriaProfesorServices asignacionMateriaProfesorServices;

    public AsignacionMateriaProfesorController(AsignacionMateriaProfesorServices asignacionMateriaProfesorServices) {
        this.asignacionMateriaProfesorServices = asignacionMateriaProfesorServices;
    }

    @PostMapping
    public ResponseEntity<AsignacionMateriaProfesor> crear(@Valid @RequestBody AsignacionMateriaProfesor asignacionMateriaProfesor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(asignacionMateriaProfesorServices.crear(asignacionMateriaProfesor));
    }

    @GetMapping
    public List<AsignacionMateriaProfesor> listar() {
        return asignacionMateriaProfesorServices.obtenerTodos();
    }

    @GetMapping("/profesor/{idProfesor}")
    public List<AsignacionMateriaProfesor> listarPorProfesor(@PathVariable String idProfesor) {
        return asignacionMateriaProfesorServices.obtenerPorProfesor(idProfesor);
    }

    @GetMapping("/{id}")
    public AsignacionMateriaProfesor obtener(@PathVariable Long id) {
        return asignacionMateriaProfesorServices.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public AsignacionMateriaProfesor actualizar(@PathVariable Long id, @Valid @RequestBody AsignacionMateriaProfesor datos) {
        return asignacionMateriaProfesorServices.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asignacionMateriaProfesorServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
