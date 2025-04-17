package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentRequest;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping("/events/{eventId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @RequestBody @Valid CommentRequest commentRequest) {
        return new ResponseEntity<>(commentService.createComment(commentRequest, userId, eventId), HttpStatus.CREATED);
    }

    @DeleteMapping("/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentPublic(@PathVariable Long commentId,
                                    @PathVariable Long userId) {
        commentService.deleteCommentPublic(commentId, userId);
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentDto>> getUserComments(@PathVariable Long userId) {
        return new ResponseEntity<>(commentService.getUserComments(userId), HttpStatus.OK);
    }
}
