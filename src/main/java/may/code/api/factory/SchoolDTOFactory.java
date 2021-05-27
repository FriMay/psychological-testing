package may.code.api.factory;

import may.code.api.dto.SchoolDTO;
import may.code.api.store.entities.SchoolEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SchoolDTOFactory {

    public SchoolDTO createSchoolDTO(SchoolEntity entity) {
        return SchoolDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public List<SchoolDTO> createSchoolDTOList(List<SchoolEntity> entities) {
        return entities
                .stream()
                .map(this::createSchoolDTO)
                .collect(Collectors.toList());
    }
}
