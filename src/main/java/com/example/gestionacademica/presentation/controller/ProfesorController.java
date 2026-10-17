package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.ProfesorServices;
import com.example.gestionacademica.domain.models.Profesor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profesores")
public class ProfesorController {

    private final ProfesorServices profesorServices;

    public ProfesorController(ProfesorServices profesorServices) {
        this.profesorServices = profesorServices;
    }

    @PostMapping
    public ResponseEntity<Profesor> crear(@Valid @RequestBody Profesor profesor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profesorServices.crear(profesor));
    }

    @GetMapping
    public List<Profesor> listar() {
        return profesorServices.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Profesor obtener(@PathVariable String id) {
        return profesorServices.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public Profesor actualizar(@PathVariable String id, @Valid @RequestBody Profesor datos) {
        return profesorServices.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        profesorServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
