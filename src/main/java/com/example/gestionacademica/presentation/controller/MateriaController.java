package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.MateriaServices;
import com.example.gestionacademica.domain.models.Materia;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materias")
public class MateriaController {

    private final MateriaServices materiaServices;

    public MateriaController(MateriaServices materiaServices) {
        this.materiaServices = materiaServices;
    }

    @PostMapping
    public ResponseEntity<Materia> crear(@Valid @RequestBody Materia materia) {
        return ResponseEntity.status(HttpStatus.CREATED).body(materiaServices.crear(materia));
    }

    @GetMapping
    public List<Materia> listar() {
        return materiaServices.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Materia obtener(@PathVariable String id) {
        return materiaServices.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public Materia actualizar(@PathVariable String id, @Valid @RequestBody Materia datos) {
        return materiaServices.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        materiaServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
