package com.pmf.awp.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.pmf.awp.project.exception.GenericHttpException;
import com.pmf.awp.project.model.Access;
import com.pmf.awp.project.model.Board;
import com.pmf.awp.project.model.Membership;
import com.pmf.awp.project.repository.BoardRepository;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    public boolean existingTitleByOwner(String title, Integer ownerId) {
        return boardRepository.existingTitleByOwner(title, ownerId);
    }

    public boolean existingPhaseWithLabel(Integer boarId, String label) {
        return boardRepository.existingPhaseWithLabel(boarId, label);
    }

    public boolean existingMember(Integer boardId, Integer userId) {
        return boardRepository.existingMember(boardId, userId);
    }

    public Board findById(Integer boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new GenericHttpException("Board not found", HttpStatus.NOT_FOUND));
    }

    public Board save(Board board) {
        try {
            return boardRepository.save(board);
        } catch (Exception e) {
            throw new GenericHttpException("Unable to create/update new board", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Board verifyMembership(Integer userId, Integer boardId, Access access) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(
                        () -> new GenericHttpException("Board not found with id: " + boardId, HttpStatus.NOT_FOUND));
        Membership member = board.getMembers().stream()
                .filter(m -> m.getUser().getId() == userId)
                .findFirst()
                .orElseThrow(
                        () -> new GenericHttpException("User is not member of this board", HttpStatus.BAD_REQUEST));
        if (member.getAccess().getLevel() < access.getLevel())
            throw new GenericHttpException("User's access level is too low", HttpStatus.FORBIDDEN);
        return board;
    }

    public List<Board> findAllWithMember(Integer userId) {
        return boardRepository.findAllWithMember(userId);
    }

    public boolean remove(Board board) {
        try {
            boardRepository.delete(board);
            return true;
        } catch (Exception e) {
            throw new GenericHttpException("Unable to delete board", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
