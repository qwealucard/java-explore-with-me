package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentRequest;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    @Override
    public CommentDto createComment(CommentRequest commentRequest, Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with ID " + eventId + " not found"));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with ID " + userId + " not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("You cannot add comments to an unpublished event");
        }

        Comment comment = commentMapper.toComment(commentRequest, user, event);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteCommentPublic(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with ID " + commentId + " not found"));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with ID " + " not found"));
        if (!userId.equals(comment.getUser().getId())) {
            throw new ForbiddenException("Access denied to user with ID " + userId);
        }
        commentRepository.delete(comment);
    }

    @Override
    public void deleteCommentAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with ID " + commentId + " not found"));
        commentRepository.delete(comment);
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with ID " + commentId + " not found"));
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getEventComments(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with ID " + eventId + " not found"));
        List<Comment> comments = commentRepository.findAllByEventId(eventId);
        return commentMapper.toCommentDto(comments);
    }

    @Override
    public List<CommentDto> getUserComments(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with ID " + " not found"));
        List<Comment> comments = commentRepository.findAllByUserId(userId);
        return commentMapper.toCommentDto(comments);
    }
}
