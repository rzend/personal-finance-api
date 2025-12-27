package br.com.gestao.financeira.dominio.repository;

import br.com.gestao.financeira.dominio.entity.Familia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamiliaRepository extends JpaRepository<Familia, Long> {
}
