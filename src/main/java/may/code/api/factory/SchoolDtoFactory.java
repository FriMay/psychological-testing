package may.code.api.factory;

import may.code.api.dto.SchoolDto;
import may.code.api.store.entities.SchoolEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SchoolDtoFactory {

    public SchoolDto createSchoolDto(SchoolEntity entity) {
        return SchoolDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public List<SchoolDto> createSchoolDtoList(List<SchoolEntity> entities) {
        return entities
                .stream()
                .map(this::createSchoolDto)
                .collect(Collectors.toList());
    }
}
