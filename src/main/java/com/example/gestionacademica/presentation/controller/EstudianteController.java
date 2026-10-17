package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.EstudianteServices;
import com.example.gestionacademica.domain.models.Estudiante;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apiv1/estudiantes")
public class EstudianteController {

    private final EstudianteServices estudianteServices;

    public EstudianteController(EstudianteServices estudianteServices) {
        this.estudianteServices = estudianteServices;
    }

    @PostMapping
    public ResponseEntity<Estudiante> crear(@Valid @RequestBody Estudiante estudiante) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estudianteServices.crear(estudiante));
    }

    @GetMapping
    public List<Estudiante> listar() {
        return estudianteServices.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Estudiante obtener(@PathVariable String id) {
        return estudianteServices.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public Estudiante actualizar(@PathVariable String id, @Valid @RequestBody Estudiante datos) {
        return estudianteServices.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        estudianteServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
