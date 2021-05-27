package may.code.api.factory;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.dto.TestUserDTO;
import may.code.api.store.entities.TestUserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Component
public class TestUserDTOFactory {

    TestDTOFactory testDTOFactory;

    UserDTOFactory userDTOFactory;

    public List<TestUserDTO> createTestUserDTOList(List<TestUserEntity> entities) {
        return entities.stream().map(this::createTestUserDTOList).collect(Collectors.toList());
    }

    public TestUserDTO createTestUserDTOList(TestUserEntity entity) {
        return TestUserDTO.builder()
                .test(testDTOFactory.createLiteTestDTO(entity.getTest()))
                .user(userDTOFactory.createUserDTO(entity.getUser()))
                .answers(entity.getAnswers())
                .build();
    }
}
