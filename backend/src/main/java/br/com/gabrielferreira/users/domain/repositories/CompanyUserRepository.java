package br.com.gabrielferreira.users.domain.repositories;

import br.com.gabrielferreira.users.domain.entities.CompanyUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyUserRepository extends JpaRepository<CompanyUserEntity, Long> {

}

