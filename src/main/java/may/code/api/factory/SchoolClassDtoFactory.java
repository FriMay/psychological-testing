package may.code.api.factory;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.dto.SchoolClassDto;
import may.code.api.store.entities.SchoolClassEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Component
public class SchoolClassDtoFactory {

    SchoolDtoFactory schoolDtoFactory;

    public SchoolClassDto createSchoolClassDto(SchoolClassEntity entity) {
        return SchoolClassDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .school(schoolDtoFactory.createSchoolDto(entity.getSchool()))
                .build();
    }

    public List<SchoolClassDto> createSchoolClassDtoList(List<SchoolClassEntity> entities) {
        return entities
                .stream()
                .map(this::createSchoolClassDto)
                .collect(Collectors.toList());
    }
}
