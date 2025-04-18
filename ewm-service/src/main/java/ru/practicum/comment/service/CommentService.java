package ru.practicum.comment.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentRequest;

import java.util.List;

@Transactional
public interface CommentService {

    CommentDto createComment(CommentRequest commentRequest, Long userId, Long eventId);

    void deleteCommentPublic(Long commentId, Long userId);

    void deleteCommentAdmin(Long commentId);

    CommentDto getCommentById(Long commentId);

    List<CommentDto> getEventComments(Long eventId);

    List<CommentDto> getUserComments(Long userId);
}
