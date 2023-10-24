package br.com.leticiaoliveira.rocketseatjavatodolist.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

// interfaces só têm a representação dos métodos, não têm implementação.. a implementação fica por conta da classe que implementa a interface
public interface IUserRepository extends JpaRepository<UserModel, UUID> {

  UserModel findByUsername(String username);
  
}
