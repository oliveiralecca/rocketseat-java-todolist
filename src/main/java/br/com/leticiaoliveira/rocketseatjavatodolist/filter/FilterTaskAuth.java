package br.com.leticiaoliveira.rocketseatjavatodolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.leticiaoliveira.rocketseatjavatodolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // indica que é uma classe que eu quero que o spring gerencie
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

        var servletPath = request.getServletPath();

        if (servletPath.startsWith("/tasks/")) {
          // fazer autenticação (usuario e senha)
          var authorization = request.getHeader("Authorization");
          var authEncoded = authorization.substring("Basic".length()).trim();

          byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
          var authString = new String(authDecoded);

          String[] credentials = authString.split(":");
          String username = credentials[0];
          String password = credentials[1];

          // validar usuário
          var foundUser = this.userRepository.findByUsername(username);

          if (foundUser == null) {
            response.sendError(401); // usuário não autorizado, não encontrado
          } else {
            // validar senha
            var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), foundUser.getPassword());

            if (passwordVerify.verified) {
              // segue viagem
              request.setAttribute("idUser", foundUser.getId());
              filterChain.doFilter(request, response);
            } else {
              response.sendError(401); // usuário não autorizado, senha inválida
            }        
          }
        } else {
          // segue viagem
          filterChain.doFilter(request, response);
        }                
  }  

}
