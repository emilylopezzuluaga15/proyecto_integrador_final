package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.AsistenciaServices;
import com.example.gestionacademica.domain.models.Asistencia;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asistencia")
public class AsistenciaController {

    private final AsistenciaServices asistenciaServices;

    public AsistenciaController(AsistenciaServices asistenciaServices) {
        this.asistenciaServices = asistenciaServices;
    }

    @PostMapping
    public ResponseEntity<Asistencia> crear(@Valid @RequestBody Asistencia asistencia) {
        return ResponseEntity.status(HttpStatus.CREATED).body(asistenciaServices.crear(asistencia));
    }

    @GetMapping
    public List<Asistencia> listar() {
        return asistenciaServices.obtenerTodos();
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public List<Asistencia> listarPorEstudiante(@PathVariable String idEstudiante) {
        return asistenciaServices.obtenerPorEstudiante(idEstudiante);
    }

    @GetMapping("/estudiante/{idEstudiante}/resumen")
    public Map<String, Object> resumen(@PathVariable String idEstudiante) {
        return asistenciaServices.resumenPorEstudiante(idEstudiante);
    }

    @GetMapping("/{id}")
    public Asistencia obtener(@PathVariable Long id) {
        return asistenciaServices.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public Asistencia actualizar(@PathVariable Long id, @Valid @RequestBody Asistencia datos) {
        return asistenciaServices.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asistenciaServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
