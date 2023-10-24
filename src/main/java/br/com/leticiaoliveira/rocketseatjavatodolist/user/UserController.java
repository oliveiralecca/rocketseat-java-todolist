package br.com.leticiaoliveira.rocketseatjavatodolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // para retornar uma resposta HTTP (sucesso ou erro)
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

// esses @ são annotations, que são usadas para adicionar metadados ao código, é como se fossem libs
@RestController // indica que essa classe é um controller (camada de contato direto com o client, que recebe as requisições e envia as respostas)
@RequestMapping("/users") // seta a URL base para todas as rotas desse controller
public class UserController {

  @Autowired // o spring gerencia a criação de instâncias dessa interface
  private IUserRepository userRepository;
  
  @PostMapping("/") // indica que essa rota é um POST e que a URL dela é a base + a que foi passada
  public ResponseEntity create(@RequestBody UserModel user) { // @RequestBody indica que o corpo da requisição será convertido para o objeto UserModel
    var foundUser = this.userRepository.findByUsername(user.getUsername());

    if (foundUser != null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe");
    }

    var passwordHashed = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());

    user.setPassword(passwordHashed);

    var userCreated = this.userRepository.save(user);
    return ResponseEntity.status(HttpStatus.OK).body(userCreated);
  }

}
