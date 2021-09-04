package may.code.api.factory;

import may.code.api.dto.UserDto;
import may.code.api.store.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDtoFactory {

    public UserDto createUserDto(UserEntity entity) {
        return UserDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .middleName(entity.getMiddleName())
                .lastName(entity.getLastName())
                .login(entity.getLogin())
                .password(entity.getPassword())
                .birthday(entity.getBirthday())
                .role(entity.getRole())
                .schoolClassId(entity.getSchoolClass().getId())
                .build();
    }

    public List<UserDto> createUserDtoList(List<UserEntity> entities) {
        return entities
                .stream()
                .map(this::createUserDto)
                .collect(Collectors.toList());
    }
}
