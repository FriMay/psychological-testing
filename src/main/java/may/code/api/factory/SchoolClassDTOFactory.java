package may.code.api.factory;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.dto.SchoolClassDTO;
import may.code.api.store.entities.SchoolClassEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Component
public class SchoolClassDTOFactory {

    SchoolDTOFactory schoolDTOFactory;

    public SchoolClassDTO createSchoolClassDTO(SchoolClassEntity entity) {
        return SchoolClassDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .school(schoolDTOFactory.createSchoolDTO(entity.getSchool()))
                .build();
    }

    public List<SchoolClassDTO> createSchoolClassDTOList(List<SchoolClassEntity> entities) {
        return entities
                .stream()
                .map(this::createSchoolClassDTO)
                .collect(Collectors.toList());
    }
}
