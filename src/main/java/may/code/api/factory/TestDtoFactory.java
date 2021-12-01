package may.code.api.factory;

import may.code.api.dto.AnswerDto;
import may.code.api.dto.LiteTestDto;
import may.code.api.dto.QuestionDto;
import may.code.api.dto.TestDto;
import may.code.api.store.entities.AnswerEntity;
import may.code.api.store.entities.QuestionEntity;
import may.code.api.store.entities.TestEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TestDtoFactory {

    public LiteTestDto createLiteTestDto(TestEntity entity) {
        return LiteTestDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public TestDto createTestDto(TestEntity entity) {
        return TestDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .questions(createQuestionDtoList(entity.getQuestions()))
                .build();
    }

    public List<TestDto> createTestDtoList(List<TestEntity> entities) {
        return entities
                .stream()
                .map(this::createTestDto)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<QuestionDto> createQuestionDtoList(List<QuestionEntity> entities) {
        return entities
                .stream()
                .map(this::createQuestionDto)
                .distinct()
                .collect(Collectors.toList());
    }

    public QuestionDto createQuestionDto(QuestionEntity entity) {
        return QuestionDto.builder()
                .id(entity.getId())
                .text(entity.getText())
                .order(entity.getQuestionOrder())
                .answers(createAnswerDtoList(entity.getAnswers()))
                .build();
    }

    public List<AnswerDto> createAnswerDtoList(List<AnswerEntity> entities) {
        return entities
                .stream()
                .map(this::createAnswerDto)
                .distinct()
                .collect(Collectors.toList());
    }

    public AnswerDto createAnswerDto(AnswerEntity entity) {
        return AnswerDto.builder()
                .id(entity.getId())
                .text(entity.getText())
                .order(entity.getAnswerOrder())
                .build();
    }
}
