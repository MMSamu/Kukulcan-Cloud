package com.uamishop.backend.catalogo.infrastructure.persistence;

import com.uamishop.backend.catalogo.domain.Categoria;
import com.uamishop.backend.catalogo.domain.CategoriaId;
import com.uamishop.backend.catalogo.repository.CategoriaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CategoriaRepositoryImpl implements CategoriaRepository {
    private final JpaCategoriaRepository jpaRepository;

    public CategoriaRepositoryImpl(JpaCategoriaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Categoria save(Categoria categoria) {

        CategoriaEntity entity = toEntity(categoria);
        jpaRepository.save(entity);

        return categoria;
    }

    @Override
    public Optional<Categoria> findById(CategoriaId id) {

        return jpaRepository.findById(id.valor())
                .map(this::toDomain);
    }

    @Override
    public List<Categoria> findAll() {

        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(CategoriaId id) {
        jpaRepository.deleteById(id.valor());
    }

    @Override
    public boolean existsById(CategoriaId id) {
        return jpaRepository.existsById(id.valor());
    }

    // =============================
    // MAPPERS
    // =============================

    private CategoriaEntity toEntity(Categoria categoria) {
        return new CategoriaEntity(
                categoria.getId().valor(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getCategoriaPadreId() != null
                        ? categoria.getCategoriaPadreId().valor()
                        : null
        );
    }

    private Categoria toDomain(CategoriaEntity entity) {

        Categoria categoria = new Categoria(
                new CategoriaId(entity.getId()),
                entity.getNombre(),
                entity.getDescripcion()
        );

        if (entity.getCategoriaPadreId() != null) {
            categoria.asignarPadre(
                    new CategoriaId(entity.getCategoriaPadreId())
            );
        }

        return categoria;
    }
}

