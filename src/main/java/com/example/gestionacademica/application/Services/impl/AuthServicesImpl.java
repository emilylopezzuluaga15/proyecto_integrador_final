package com.example.gestionacademica.application.Services.impl;

import com.example.gestionacademica.application.Exceptions.CredencialesInvalidasException;
import com.example.gestionacademica.application.Services.AuthServices;
import com.example.gestionacademica.application.Services.Sesion;
import com.example.gestionacademica.application.Services.TokenServices;
import com.example.gestionacademica.domain.models.Usuario;
import com.example.gestionacademica.domain.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthServicesImpl implements AuthServices {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenServices tokenServices;

    public AuthServicesImpl(UsuarioRepository usuarioRepository,
                            PasswordEncoder passwordEncoder,
                            TokenServices tokenServices) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenServices = tokenServices;
    }

    // Mismo mensaje para usuario inexistente, inactivo o clave errada: no se revela cuál falló
    @Override
    public Sesion login(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario o contraseña incorrectos"));
        if (!usuario.getActivo() || !passwordEncoder.matches(password, usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException("Usuario o contraseña incorrectos");
        }
        return tokenServices.generar(usuario);
    }
}
