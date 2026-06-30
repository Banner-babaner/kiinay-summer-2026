package dev.vorstu.mapper;

import dev.vorstu.dto.output.TeacherInfo;
import dev.vorstu.entity.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = GroupMapper.class)
public interface TeacherMapper {
    @Mapping(qualifiedByName = "toGroupPreview", target = "groups")
    TeacherInfo toTeacherInfo(Teacher teacher);

}
