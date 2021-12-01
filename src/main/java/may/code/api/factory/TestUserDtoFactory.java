package may.code.api.factory;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.dto.TestUserDto;
import may.code.api.store.entities.TestAnswerEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Component
public class TestUserDtoFactory {

    TestDtoFactory testDtoFactory;

    TestedUserDtoFactory testedUserDtoFactory;

    public List<TestUserDto> createTestUserDtoList(List<TestAnswerEntity> entities) {
        return entities.stream().map(this::createTestUserDtoList).collect(Collectors.toList());
    }

    public TestUserDto createTestUserDtoList(TestAnswerEntity entity) {
        return TestUserDto.builder()
                .test(testDtoFactory.createLiteTestDto(entity.getTest()))
                .user(testedUserDtoFactory.createTestedUserDto(entity.getTestedUser()))
//                .answers(entity.getAnswers())
                .build();
    }
}
