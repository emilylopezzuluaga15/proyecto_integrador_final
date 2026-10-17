package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.Usuario;

import java.util.List;

public interface UsuarioServices {

    Usuario crear(Usuario usuario, String password);

    Usuario obtenerPorId(Long id);

    Usuario obtenerPorUsername(String username);

    List<Usuario> obtenerTodos();

    // password puede ser null para conservar la actual
    Usuario actualizar(Long id, Usuario datos, String password);

    void eliminar(Long id);
}
