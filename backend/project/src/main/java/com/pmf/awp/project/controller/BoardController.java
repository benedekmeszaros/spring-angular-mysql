package com.pmf.awp.project.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pmf.awp.project.exception.GenericHttpException;
import com.pmf.awp.project.model.Access;
import com.pmf.awp.project.model.Board;
import com.pmf.awp.project.model.Membership;
import com.pmf.awp.project.model.Phase;
import com.pmf.awp.project.model.User;
import com.pmf.awp.project.service.AuthenticationService;
import com.pmf.awp.project.service.BoardService;
import com.pmf.awp.project.service.MembershipService;
import com.pmf.awp.project.util.ExporterUtils;
import com.pmf.awp.project.util.ImporterUtils;

@RestController
@RequestMapping("/boards")
public class BoardController {

        @Autowired
        private BoardService boardService;
        @Autowired
        private MembershipService membershipService;

        @PostMapping
        public ResponseEntity<Integer> createBoard(@RequestBody Map<String, Object> data) {
                User user = AuthenticationService.getAuthenticatedUser();
                String title = (String) data.get("title");
                if (title == null)
                        throw new GenericHttpException("Boar title can not be empty",
                                        HttpStatus.BAD_REQUEST);
                if (boardService.existingTitleByOwner(title, user.getId()))
                        throw new GenericHttpException("You have already own a board with title: '" + title + "'",
                                        HttpStatus.BAD_REQUEST);
                Board board = Board.builder()
                                .members(new ArrayList<>())
                                .phases(new ArrayList<>())
                                .build();
                ImporterUtils.copy(data, board);
                board.getMembers().add(Membership.builder()
                                .access(Access.OWNER)
                                .user(user)
                                .board(board)
                                .build());
                Phase todo = Phase.builder()
                                .board(board)
                                .label("TODO")
                                .tasks(new ArrayList<>())
                                .build();
                board.getPhases().add(todo);
                Phase doing = Phase.builder()
                                .board(board)
                                .label("Doing")
                                .tasks(new ArrayList<>())
                                .build();
                board.getPhases().add(doing);
                Phase done = Phase.builder()
                                .board(board)
                                .label("Done")
                                .tasks(new ArrayList<>())
                                .build();
                board.getPhases().add(done);
                board = boardService.save(board);
                return ResponseEntity.ok(board.getId());
        }

        @GetMapping("/{boardId}")
        public ResponseEntity<Map<String, Object>> getBoard(@PathVariable("boardId") Integer boardId) {
                User user = AuthenticationService.getAuthenticatedUser();
                Board board = boardService.verifyMembership(user.getId(), boardId, Access.VIEWER);
                return ResponseEntity.ok(ExporterUtils.extract(board));
        }

        @GetMapping
        public ResponseEntity<List<Map<String, Object>>> getBoards() {
                User user = AuthenticationService.getAuthenticatedUser();
                return ResponseEntity.ok(boardService.findAllWithMember(user.getId()).stream()
                                .map(ExporterUtils::extract)
                                .toList());
        }

        @PatchMapping("/{boardId}")
        public ResponseEntity<Map<String, Object>> updateBoard(@PathVariable("boardId") Integer boardId,
                        @RequestBody Map<String, Object> data) {
                User user = AuthenticationService.getAuthenticatedUser();
                Board board = boardService.verifyMembership(user.getId(), boardId, Access.OWNER);
                String title = (String) data.get("title");
                if (title != null && !board.getTitle().equals(title)
                                && boardService.existingTitleByOwner(title, boardId))
                        throw new GenericHttpException("Title is already preserved", HttpStatus.BAD_REQUEST);
                ImporterUtils.copy(data, board);
                board = boardService.save(board);
                return ResponseEntity.ok(ExporterUtils.extract(board));
        }

        @DeleteMapping("/{boardId}")
        public ResponseEntity<String> removeBoard(@PathVariable("boardId") Integer boardId) {
                User user = AuthenticationService.getAuthenticatedUser();
                Board board = boardService.findById(boardId);
                if (board.getOwner().getId() != user.getId())
                        throw new GenericHttpException("You have no permission", HttpStatus.FORBIDDEN);
                boardService.remove(board);
                return ResponseEntity.ok("Board delted with id: " + boardId);
        }

        @PatchMapping("/{boardId}/members/{memberId}")
        public ResponseEntity<String> updateMember(@PathVariable("boardId") Integer boardId,
                        @PathVariable("memberId") Integer memberId, @RequestParam("access") String access) {
                User user = AuthenticationService.getAuthenticatedUser();
                Board board = boardService.verifyMembership(user.getId(), boardId, Access.VIEWER);
                Membership member = board.getMembers().stream()
                                .filter(m -> m.getId() == memberId)
                                .findFirst()
                                .orElseThrow(() -> new GenericHttpException("Member not found", HttpStatus.NOT_FOUND));
                Access accessLevel;
                try {
                        accessLevel = Access.valueOf(access.toUpperCase());
                } catch (Exception e) {
                        throw new GenericHttpException("Invalid access", HttpStatus.BAD_REQUEST);
                }
                if (accessLevel == member.getAccess())
                        return ResponseEntity.ok("Nothing changed");
                member.setAccess(accessLevel);
                membershipService.save(member);
                return ResponseEntity.ok(String.format("'%s' granted for user '%s'", access, user.getFullName()));
        }

        @DeleteMapping("/{boardId}/members/{memberId}")
        public ResponseEntity<String> removeMember(@PathVariable("boardId") Integer boardId,
                        @PathVariable("memberId") Integer memberId) {
                User user = AuthenticationService.getAuthenticatedUser();
                Board board = boardService.verifyMembership(user.getId(), boardId, Access.VIEWER);
                Membership member = board.getMembers().stream()
                                .filter(m -> m.getId() == memberId)
                                .findFirst()
                                .orElseThrow(() -> new GenericHttpException("Member not found", HttpStatus.NOT_FOUND));
                membershipService.remove(member);
                return ResponseEntity.ok(
                                member.getUser().getFullName() + " has removed from " + board.getTitle() + " board");
        }
}
