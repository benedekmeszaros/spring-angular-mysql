package com.pmf.awp.project.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pmf.awp.project.exception.GenericHttpException;
import com.pmf.awp.project.model.Access;
import com.pmf.awp.project.model.Board;
import com.pmf.awp.project.model.Phase;
import com.pmf.awp.project.model.Task;
import com.pmf.awp.project.model.User;
import com.pmf.awp.project.service.AuthenticationService;
import com.pmf.awp.project.service.BoardService;
import com.pmf.awp.project.service.PhaseService;
import com.pmf.awp.project.service.TaskService;
import com.pmf.awp.project.util.ExporterUtils;
import com.pmf.awp.project.util.ImporterUtils;

@RestController
@RequestMapping("/boards/{boardId}/phases/{phaseId}/tasks")
public class TaskController {
        @Autowired
        private BoardService boardService;
        @Autowired
        private PhaseService phaseService;
        @Autowired
        private TaskService taskService;

        @PostMapping
        public ResponseEntity<Map<String, Object>> createTask(@PathVariable("boardId") Integer boardId,
                        @PathVariable("phaseId") Integer phaseId, @RequestBody Map<String, Object> data) {
                User owner = AuthenticationService.getAuthenticatedUser();
                Board board = boardService.verifyMembership(owner.getId(), boardId, Access.OWNER);
                String label = (String) data.get("label");
                if (label == null)
                        throw new GenericHttpException("Phase label can not be empty", HttpStatus.BAD_REQUEST);
                if (phaseService.existingTaskWithLabel(phaseId, label))
                        throw new GenericHttpException("Label is already preserved", HttpStatus.BAD_REQUEST);
                Phase phase = board.getPhases().stream()
                                .filter(p -> p.getId() == phaseId)
                                .findFirst()
                                .orElseThrow(() -> new GenericHttpException("Phase not found", HttpStatus.NOT_FOUND));
                Task task = Task.builder()
                                .phase(phase)
                                .position(phase.getTasks().size())
                                .build();
                ImporterUtils.copy(data, task);
                task = taskService.save(task);
                phase.getTasks().add(task);
                phase.setUpdatedAt(new Date());
                phase.setUpdatedBy(owner);
                board.setUpdatedAt(new Date());
                board.setUpdatedBy(owner);
                board = boardService.save(board);
                return ResponseEntity.ok(ExporterUtils.extract(task));
        }

        @GetMapping("/{taskId}")
        public ResponseEntity<Map<String, Object>> getTask(@PathVariable("boardId") Integer boardId,
                        @PathVariable("phaseId") Integer phaseId, @PathVariable("taskId") Integer taskId) {
                User owner = AuthenticationService.getAuthenticatedUser();
                Board board = boardService.verifyMembership(owner.getId(), boardId, Access.EDITOR);
                Task task = board.getPhases().stream()
                                .filter(p -> p.getId() == phaseId)
                                .findFirst()
                                .orElseThrow(() -> new GenericHttpException("Phase not found", HttpStatus.NOT_FOUND))
                                .getTasks().stream()
                                .filter(t -> t.getId() == taskId)
                                .findFirst()
                                .orElseThrow(() -> new GenericHttpException("Task not found", HttpStatus.NOT_FOUND));
                return ResponseEntity.ok(ExporterUtils.extract(task));
        }

        @PatchMapping("/{taskId}")
        public ResponseEntity<Map<String, Object>> updateTask(@PathVariable("boardId") Integer boardId,
                        @PathVariable("phaseId") Integer phaseId, @PathVariable("taskId") Integer taskId,
                        @RequestBody Map<String, Object> data) {
                User owner = AuthenticationService.getAuthenticatedUser();
                Board board = boardService.verifyMembership(owner.getId(), boardId, Access.EDITOR);
                String label = (String) data.get("label");
                Task task = board.getPhases().stream()
                                .filter(p -> p.getId() == phaseId)
                                .findFirst()
                                .orElseThrow(() -> new GenericHttpException("Phase not found", HttpStatus.NOT_FOUND))
                                .getTasks().stream()
                                .filter(t -> t.getId() == taskId)
                                .findFirst()
                                .orElseThrow(() -> new GenericHttpException("Task not found", HttpStatus.NOT_FOUND));
                if (label != null && !task.getLabel().equals(label)
                                && phaseService.existingTaskWithLabel(phaseId, label))
                        throw new GenericHttpException("Label is already preserved", HttpStatus.BAD_REQUEST);
                ImporterUtils.copy(data, task);
                task.getPhase().setUpdatedAt(new Date());
                task.getPhase().setUpdatedBy(owner);
                board.setUpdatedAt(new Date());
                board.setUpdatedBy(owner);
                board = boardService.save(board);
                return ResponseEntity.ok(ExporterUtils.extract(task));
        }

        @DeleteMapping("/{taskId}")
        public ResponseEntity<String> removeTask(@PathVariable("boardId") Integer boardId,
                        @PathVariable("phaseId") Integer phaseId, @PathVariable("taskId") Integer taskId) {
                User owner = AuthenticationService.getAuthenticatedUser();
                Board board = boardService.verifyMembership(owner.getId(), boardId, Access.EDITOR);
                Phase phase = board.getPhases().stream()
                                .filter(p -> p.getId() == phaseId)
                                .findFirst()
                                .orElseThrow(() -> new GenericHttpException("Phase not found", HttpStatus.NOT_FOUND));
                Task task = phase.getTasks().stream()
                                .filter(t -> t.getId() == taskId)
                                .findFirst()
                                .orElseThrow(() -> new GenericHttpException("Task not found", HttpStatus.NOT_FOUND));
                phase.getTasks().remove(task);
                phase.setUpdatedAt(new Date());
                phase.setUpdatedBy(owner);
                board.setUpdatedAt(new Date());
                board.setUpdatedBy(owner);
                taskService.remove(task);
                boardService.save(board);
                return ResponseEntity.ok("Task deleted with id: " + taskId);
        }

        @PutMapping("/{taskId}")
        public ResponseEntity<String> moveTask(@PathVariable("boardId") Integer boardId,
                        @PathVariable("phaseId") Integer phaseId, @PathVariable("taskId") Integer taskId,
                        @RequestParam("otherPhaseId") Integer otherPhaseId,
                        @RequestParam("position") Integer position) {
                User owner = AuthenticationService.getAuthenticatedUser();
                Board board = boardService.verifyMembership(owner.getId(), boardId, Access.EDITOR);
                List<Phase> phases = board.getPhases();
                Phase phase = phases.stream()
                                .filter(p -> p.getId() == phaseId)
                                .findFirst()
                                .orElseThrow(() -> new GenericHttpException("Phase not found", HttpStatus.NOT_FOUND));
                String message;
                Task task = phase.getTasks().stream()
                                .filter(t -> t.getId() == taskId)
                                .findFirst()
                                .orElseThrow(() -> new GenericHttpException("Task not found", HttpStatus.NOT_FOUND));
                if (phaseId != otherPhaseId) {
                        Phase other = phases.stream()
                                        .filter(p -> p.getId() == otherPhaseId)
                                        .findFirst()
                                        .orElseThrow(() -> new GenericHttpException("Phase not found",
                                                        HttpStatus.NOT_FOUND));
                        phase.getTasks().remove(task);
                        other.getTasks().add(position, task);
                        task.setPhase(other);
                        other.setUpdatedAt(new Date());
                        other.setUpdatedBy(owner);
                        message = String.format("'%s' moved from '%s' at position '%d' to position '%d' inside '%s'",
                                        task.getLabel(), phase.getLabel(), task.getPosition(), position,
                                        other.getLabel());
                } else {
                        if (task.getPosition() == position)
                                return ResponseEntity.ok("Position persists");

                        Collections.swap(phase.getTasks(), task.getPosition(), position);
                        message = String.format("'%s' moved from position '%d' to position '%d' inside '%s'",
                                        task.getLabel(), task.getPosition(), position, phase.getLabel());
                }
                phase.setUpdatedAt(new Date());
                phase.setUpdatedBy(owner);
                board.setUpdatedAt(new Date());
                board.setUpdatedBy(owner);
                board = boardService.save(board);
                return ResponseEntity.ok(message);
        }
}
