package br.com.leticiaoliveira.rocketseatjavatodolist.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data; // adiciona tanto os getters quanto os setters para todos os atributos da classe
// se quiser apenas para um atributo, adiciona @Getter e @Setter na linha acima da declaração dele

@Data 
@Entity(name = "tb_users") // indica que essa classe é uma entidade e que o nome da tabela dela é tb_users
public class UserModel {

  @Id // para indicar que é a chave primária
  @GeneratedValue(generator = "UUID") // para gerar um UUID automaticamente
  private UUID id;
  
  // @Column(name = "usuario") -> se quiser alterar o nome dessa coluna no banco
  @Column(unique = true)
  private String username;
  private String name;
  private String password;

  @CreationTimestamp // para indicar que esse campo será preenchido automaticamente com a data e hora da criação do registro
  private LocalDateTime createdAt;

}
