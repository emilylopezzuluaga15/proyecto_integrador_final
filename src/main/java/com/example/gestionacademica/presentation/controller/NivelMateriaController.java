package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.NivelMateriaServices;
import com.example.gestionacademica.domain.models.NivelMateria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/niveles-materias")
public class NivelMateriaController {

    private final NivelMateriaServices nivelMateriaServices;

    public NivelMateriaController(NivelMateriaServices nivelMateriaServices) {
        this.nivelMateriaServices = nivelMateriaServices;
    }

    @PostMapping
    public ResponseEntity<NivelMateria> crear(@Valid @RequestBody NivelMateria nivelMateria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nivelMateriaServices.crear(nivelMateria));
    }

    @GetMapping
    public List<NivelMateria> listar() {
        return nivelMateriaServices.obtenerTodos();
    }

    @GetMapping("/{id}")
    public NivelMateria obtener(@PathVariable Long id) {
        return nivelMateriaServices.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public NivelMateria actualizar(@PathVariable Long id, @Valid @RequestBody NivelMateria datos) {
        return nivelMateriaServices.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        nivelMateriaServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
