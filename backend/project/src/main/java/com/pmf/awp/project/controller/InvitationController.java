package com.pmf.awp.project.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.pmf.awp.project.model.Invitation;
import com.pmf.awp.project.model.User;
import com.pmf.awp.project.service.AuthenticationService;
import com.pmf.awp.project.service.BoardService;
import com.pmf.awp.project.service.InvitationService;
import com.pmf.awp.project.service.MembershipService;
import com.pmf.awp.project.service.UserService;
import com.pmf.awp.project.util.ExporterUtils;

@RestController
@RequestMapping("/invitations")
public class InvitationController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private UserService userService;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private MembershipService membershipService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getInvitations() {
        User owner = AuthenticationService.getAuthenticatedUser();
        return ResponseEntity.ok(invitationService.findAllPendingByUser(owner.getId()).stream()
                .map(ExporterUtils::extract)
                .toList());
    }

    @PostMapping("/boards/{boardId}")
    public ResponseEntity<String> invite(@PathVariable("boardId") Integer boardId,
            @RequestBody Map<String, Object> data) {
        User owner = AuthenticationService.getAuthenticatedUser();
        Board board = boardService.verifyMembership(owner.getId(), boardId, Access.OWNER);
        String email = (String) data.get("email");
        if (email == null)
            throw new GenericHttpException("Invalid email address", HttpStatus.BAD_REQUEST);
        Access access = null;
        try {
            access = Access.valueOf(((String) data.get("access")).toUpperCase());
        } catch (Exception e) {
            throw new GenericHttpException("Invalid accesss", HttpStatus.BAD_REQUEST);
        }
        User user = userService.findByEmail(email);
        if (invitationService.existsPendingByUser(boardId, user.getId()))
            throw new GenericHttpException("Invitation is already pending", HttpStatus.FORBIDDEN);
        if (boardService.existingMember(boardId, user.getId()))
            throw new GenericHttpException("'" + user.getFullName() + "' is already member of this board",
                    HttpStatus.FORBIDDEN);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 7);
        Invitation invitation = Invitation.builder()
                .board(board)
                .status(Invitation.Status.PENDING)
                .expireAt(calendar.getTime())
                .access(access)
                .user(user)
                .build();
        invitationService.save(invitation);
        return ResponseEntity.ok("Invitation sent");
    }

    @PatchMapping("/boards/{boardId}")
    public ResponseEntity<?> decide(@PathVariable("boardId") Integer boardId,
            @RequestParam("invitationId") Integer invitationId, @RequestParam("isAccepted") Boolean isAccepted) {
        User owner = AuthenticationService.getAuthenticatedUser();
        Invitation invitation = invitationService.findById(invitationId);
        if (owner.getId() != invitation.getUser().getId())
            throw new GenericHttpException("You have no permission to this invitation", HttpStatus.FORBIDDEN);
        if (isAccepted)
            return ResponseEntity.ok(ExporterUtils.extract(membershipService.acceptInvitation(invitation)));
        else {
            membershipService.dennyInvitation(invitation);
            return ResponseEntity.ok("Invitation denied");
        }
    }
}
