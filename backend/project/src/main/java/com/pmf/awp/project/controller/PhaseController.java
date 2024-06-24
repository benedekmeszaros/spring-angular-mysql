package com.pmf.awp.project.controller;

import java.util.ArrayList;
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
import com.pmf.awp.project.model.User;
import com.pmf.awp.project.service.AuthenticationService;
import com.pmf.awp.project.service.BoardService;
import com.pmf.awp.project.service.PhaseService;
import com.pmf.awp.project.util.ExporterUtils;
import com.pmf.awp.project.util.ImporterUtils;

@RestController
@RequestMapping("/boards/{boardId}/phases")
public class PhaseController {
        @Autowired
        private BoardService boardService;
        @Autowired
        private PhaseService phaseService;

        @PostMapping
        public ResponseEntity<Map<String, Object>> createPhase(@PathVariable("boardId") Integer boardId,
                        @RequestBody Map<String, Object> data) {
                User owner = AuthenticationService.getAuthenticatedUser();
                Board board = boardService.verifyMembership(owner.getId(), boardId, Access.OWNER);
                String label = (String) data.get("label");
                if (label == null)
                        throw new GenericHttpException("Phase label can not be empty", HttpStatus.BAD_REQUEST);
                if (boardService.existingPhaseWithLabel(boardId, label))
                        throw new GenericHttpException("Label is already preserved", HttpStatus.BAD_REQUEST);
                Phase phase = Phase.builder()
                                .board(board)
                                .tasks(new ArrayList<>())
                                .position(board.getPhases().size() - 1)
                                .build();
                ImporterUtils.copy(data, phase);
                board.getPhases().add(phase);
                board.setUpdatedAt(new Date());
                board.setUpdatedBy(owner);
                phase = phaseService.save(phase);
                board = boardService.save(board);
                return ResponseEntity.ok(ExporterUtils.extract(phase));
        }

        @GetMapping("/{phaseId}")
        public ResponseEntity<Map<String, Object>> getPhase(@PathVariable("boardId") Integer boardId,
                        @PathVariable("phaseId") Integer phaseId) {
                User owner = AuthenticationService.getAuthenticatedUser();
                Board board = boardService.verifyMembership(owner.getId(), boardId, Access.OWNER);
                Phase phase = board.getPhases()
                                .stream()
                                .filter(p -> p.getId() == phaseId)
                                .findFirst()
                                .orElseThrow(() -> new GenericHttpException("Phase not found", HttpStatus.NOT_FOUND));
                return ResponseEntity.ok(ExporterUtils.extract(phase));
        }

        @PatchMapping("/{phaseId}")
        public ResponseEntity<Map<String, Object>> updatePhase(@PathVariable("boardId") Integer boardId,
                        @PathVariable("phaseId") Integer phaseId, @RequestBody Map<String, Object> data) {
                User owner = AuthenticationService.getAuthenticatedUser();
                Board board = boardService.verifyMembership(owner.getId(), boardId, Access.OWNER);
                Phase phase = board.getPhases()
                                .stream()
                                .filter(p -> p.getId() == phaseId)
                                .findFirst()
                                .orElseThrow(() -> new GenericHttpException("Phase not found", HttpStatus.NOT_FOUND));
                String label = (String) data.get("label");
                if (label != null && !phase.getLabel().equals(label)
                                && boardService.existingPhaseWithLabel(boardId, label))
                        throw new GenericHttpException("Label is already preserved", HttpStatus.BAD_REQUEST);
                ImporterUtils.copy(data, phase);
                board.setUpdatedAt(new Date());
                board.setUpdatedBy(owner);
                boardService.save(board);
                return ResponseEntity.ok(ExporterUtils.extract(phase));
        }

        @DeleteMapping("/{phaseId}")
        public ResponseEntity<String> deletePhase(@PathVariable("boardId") Integer boardId,
                        @PathVariable("phaseId") Integer phaseId) {
                User owner = AuthenticationService.getAuthenticatedUser();
                Board board = boardService.verifyMembership(owner.getId(), boardId, Access.OWNER);
                Phase phase = board.getPhases()
                                .stream()
                                .filter(p -> p.getId() == phaseId)
                                .findFirst()
                                .orElseThrow(() -> new GenericHttpException("Phase not found", HttpStatus.NOT_FOUND));
                board.getPhases().remove(phase);
                phaseService.remove(phase);
                board.setUpdatedAt(new Date());
                board.setUpdatedBy(owner);
                boardService.save(board);
                return ResponseEntity.ok("Phase deleted with id: " + phaseId);
        }

        @PutMapping("/{phaseId}")
        public ResponseEntity<String> movePhase(@PathVariable("boardId") Integer boardId,
                        @PathVariable("phaseId") Integer phaseId, @RequestParam("position") Integer position) {
                User owner = AuthenticationService.getAuthenticatedUser();
                Board board = boardService.verifyMembership(owner.getId(), boardId, Access.OWNER);
                List<Phase> phases = board.getPhases();
                Phase phase = phases.stream()
                                .filter(p -> p.getId() == phaseId)
                                .findFirst()
                                .orElseThrow(() -> new GenericHttpException("Phase not found", HttpStatus.NOT_FOUND));
                int from = phase.getPosition();
                if (from == position)
                        return ResponseEntity.ok("Position persists");
                Collections.swap(phases, from, position);
                board.setUpdatedAt(new Date());
                board.setUpdatedBy(owner);
                boardService.save(board);
                return ResponseEntity
                                .ok("'" + phase.getLabel() + "'[" + phase.getPosition() + "] swapped with '"
                                                + phases.get(from).getLabel()
                                                + "'[" + position + "] inside '" + board.getTitle() + "'");
        }
}
