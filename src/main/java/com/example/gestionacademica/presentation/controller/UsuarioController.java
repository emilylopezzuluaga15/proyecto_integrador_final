package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.UsuarioServices;
import com.example.gestionacademica.presentation.dto.AlCrear;
import com.example.gestionacademica.presentation.dto.UsuarioDtos.UsuarioRequest;
import com.example.gestionacademica.presentation.dto.UsuarioDtos.UsuarioResponse;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

// Administración de cuentas: solo ADMINISTRATIVO
@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMINISTRATIVO')")
public class UsuarioController {

    private final UsuarioServices usuarioServices;
    private final Referencias referencias;

    public UsuarioController(UsuarioServices usuarioServices, Referencias referencias) {
        this.usuarioServices = usuarioServices;
        this.referencias = referencias;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Validated(AlCrear.class) @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioResponse.from(usuarioServices.crear(request.toEntity(referencias), request.password())));
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioServices.obtenerTodos().stream().map(UsuarioResponse::from).toList();
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return UsuarioResponse.from(usuarioServices.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        return UsuarioResponse.from(usuarioServices.actualizar(id, request.toEntity(referencias), request.password()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
