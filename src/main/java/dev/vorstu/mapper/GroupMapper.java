package dev.vorstu.mapper;

import dev.vorstu.dto.output.GroupInfo;
import dev.vorstu.dto.output.GroupPreview;
import dev.vorstu.dto.output.StudentPreview;
import dev.vorstu.entity.StuddingGroup;
import dev.vorstu.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GroupMapper {
    StudentPreview toStudentPreview(Student student);
    @Named("toGroupPreview")
    GroupPreview toGroupPreview(StuddingGroup group);
    GroupInfo toGroupInfo(StuddingGroup studdingGroup);
}
