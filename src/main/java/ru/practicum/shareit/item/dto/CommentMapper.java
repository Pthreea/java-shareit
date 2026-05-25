package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;

@Component
public class CommentMapper {

    public Comment toComment(CommentDto dto) {
        if (dto == null) {
            return null;
        }

        return Comment.builder()
                .id(dto.getId())
                .text(dto.getText())
                .created(dto.getCreated())
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null)
                .created(comment.getCreated())
                .build();
    }
}
