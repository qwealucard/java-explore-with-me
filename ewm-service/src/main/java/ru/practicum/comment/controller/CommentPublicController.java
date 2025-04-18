package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<CommentDto>> getEventComments(@PathVariable Long eventId) {
        return new ResponseEntity<>(commentService.getEventComments(eventId), HttpStatus.OK);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long commentId) {
        return new ResponseEntity<>(commentService.getCommentById(commentId), HttpStatus.OK);
    }
}
