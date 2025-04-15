package ru.practicum.comment.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentRequest;

import java.util.List;

public interface CommentService {

    @Transactional
    CommentDto createComment(CommentRequest commentRequest, Long userId, Long eventId);

    @Transactional
    void deleteCommentPublic(Long commentId, Long userId);

    @Transactional
    void deleteCommentAdmin(Long commentId);

    @Transactional(readOnly = true)
    CommentDto getCommentById(Long commentId);

    @Transactional(readOnly = true)
    List<CommentDto> getEventComments(Long eventId);

    @Transactional(readOnly = true)
    List<CommentDto> getUserComments(Long userId);
}
