package may.code.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SchoolClassDto {

    @NonNull
    Long id;

    @NonNull
    String name;

    @NonNull
    SchoolDto school;
}
