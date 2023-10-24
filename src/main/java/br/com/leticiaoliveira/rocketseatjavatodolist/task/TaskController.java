package br.com.leticiaoliveira.rocketseatjavatodolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.leticiaoliveira.rocketseatjavatodolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;
  
  @PostMapping("/")
  public ResponseEntity create(@RequestBody TaskModel task, HttpServletRequest request) {
    var idUser = request.getAttribute("idUser");
    task.setIdUser((UUID) idUser); // forçando o tipo do idUser para UUID

    // não permitir cadastro de uma data que já passou
    var currentDate = LocalDateTime.now();
    if (currentDate.isAfter(task.getStartAt()) || currentDate.isAfter(task.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início e fim não podem ser anteriores à data atual.");
    }

    if (task.getStartAt().isAfter(task.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data/hora de início não pode ser posterior à data/hora de fim.");
    }

    var createdTask = this.taskRepository.save(task);
    return ResponseEntity.status(HttpStatus.OK).body(createdTask);
  }

  @GetMapping("/")
  public List<TaskModel> list(HttpServletRequest request) {
    var idUser = request.getAttribute("idUser");
    var tasks = this.taskRepository.findByIdUser((UUID) idUser);
    return tasks;
  }

  @PutMapping("/{taskId}") // http://localhost:8080/tasks/8589346-gbefhgb-5763456
  public ResponseEntity update(@RequestBody TaskModel task, @PathVariable UUID taskId, HttpServletRequest request) {
    var foundTask = this.taskRepository.findById(taskId).orElse(null);

    if (foundTask == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada.");
    }

    var idUser = request.getAttribute("idUser");

    if (!foundTask.getIdUser().equals(idUser)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não tem permissão para alterar essa tarefa.");
    }
    
    Utils.copyNonNullProperties(task, foundTask);

    var taskUpdated = this.taskRepository.save(foundTask);

    return ResponseEntity.status(HttpStatus.OK).body(taskUpdated);
  }

}
