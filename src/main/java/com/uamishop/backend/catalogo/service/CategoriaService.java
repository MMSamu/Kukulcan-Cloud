package com.uamishop.backend.catalogo.service;

import com.uamishop.backend.catalogo.repository.CategoriaRepository;
import com.uamishop.backend.catalogo.domain.Categoria;
import com.uamishop.backend.catalogo.domain.CategoriaId;

import java.util.List;

public class CategoriaService {


        private final CategoriaRepository categoriaRepository;

        public CategoriaService(CategoriaRepository categoriaRepository) {
            this.categoriaRepository = categoriaRepository;
        }

        public Categoria crear(String nombre, String descripcion) {
            Categoria categoria = new Categoria(
                    CategoriaId.generar(),
                    nombre,
                    descripcion
            );

            return categoriaRepository.save(categoria);
        }

        public Categoria actualizar(CategoriaId id, String nombre, String descripcion) {
            Categoria categoria = categoriaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));

            categoria.actualizar(nombre, descripcion);

            return categoriaRepository.save(categoria);
        }

        public void asignarPadre(CategoriaId id, CategoriaId padreId) {
            Categoria categoria = categoriaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));

            categoria.asignarPadre(padreId);

            categoriaRepository.save(categoria);
        }

        public List<Categoria> listar() {
            return categoriaRepository.findAll();
        }

        public void eliminar(CategoriaId id) {
            categoriaRepository.deleteById(id);
        }


}
