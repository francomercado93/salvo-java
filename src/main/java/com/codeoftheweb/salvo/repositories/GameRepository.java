package com.codeoftheweb.salvo.repositories;

import com.codeoftheweb.salvo.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public interface GameRepository extends JpaRepository<Game, Long> {
}
