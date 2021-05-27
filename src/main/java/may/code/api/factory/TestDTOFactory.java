package may.code.api.factory;

import may.code.api.dto.AnswerDTO;
import may.code.api.dto.LiteTestDTO;
import may.code.api.dto.QuestionDTO;
import may.code.api.dto.TestDTO;
import may.code.api.store.entities.AnswerEntity;
import may.code.api.store.entities.QuestionEntity;
import may.code.api.store.entities.TestEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TestDTOFactory {

    public LiteTestDTO createLiteTestDTO(TestEntity entity) {
        return LiteTestDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public TestDTO createTestDTO(TestEntity entity) {
        return TestDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .questions(createQuestionDTOList(entity.getQuestions()))
                .build();
    }

    public List<TestDTO> createTestDTOList(List<TestEntity> entities) {
        return entities
                .stream()
                .map(this::createTestDTO)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<QuestionDTO> createQuestionDTOList(List<QuestionEntity> entities) {
        return entities
                .stream()
                .map(this::createQuestionDTO)
                .distinct()
                .collect(Collectors.toList());
    }

    public QuestionDTO createQuestionDTO(QuestionEntity entity) {
        return QuestionDTO.builder()
                .id(entity.getId())
                .text(entity.getText())
                .order(entity.getQuestionOrder())
                .answers(createAnswerDTOList(entity.getAnswers()))
                .build();
    }

    public List<AnswerDTO> createAnswerDTOList(List<AnswerEntity> entities) {
        return entities
                .stream()
                .map(this::createAnswerDTO)
                .distinct()
                .collect(Collectors.toList());
    }

    public AnswerDTO createAnswerDTO(AnswerEntity entity) {
        return AnswerDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .order(entity.getAnswerOrder())
                .build();
    }
}
