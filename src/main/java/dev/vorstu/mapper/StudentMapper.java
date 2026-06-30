package dev.vorstu.mapper;


import dev.vorstu.dto.output.GroupPreview;
import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.dto.output.StudentPreview;
import dev.vorstu.entity.StuddingGroup;
import dev.vorstu.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = GroupMapper.class)
public interface StudentMapper {
    @Mapping(qualifiedByName = "toGroupPreview", target = "group")
    StudentInfo toStudentInfo(Student student);
}
