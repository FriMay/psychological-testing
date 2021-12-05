package may.code.api.factory;

import may.code.api.dto.TestedUserDto;
import may.code.api.store.entities.TestedUserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TestedUserDtoFactory {

    public TestedUserDto createTestedUserDto(TestedUserEntity entity) {

        return TestedUserDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .middleName(entity.getMiddleName())
                .lastName(entity.getLastName())
                .login(entity.getLogin())
                .password(entity.getPassword())
                .birthday(entity.getBirthday())
                .role(entity.getStatus())
                .schoolClassId(entity.getSchoolClass().getId())
                .build();
    }

    public List<TestedUserDto> createTestedUserDtoList(List<TestedUserEntity> entities) {

        return entities
                .stream()
                .map(this::createTestedUserDto)
                .collect(Collectors.toList());
    }
}
