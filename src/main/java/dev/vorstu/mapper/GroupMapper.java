package dev.vorstu.mapper;

import dev.vorstu.dto.output.GroupInfo;
import dev.vorstu.dto.output.GroupPreview;
import dev.vorstu.entity.StuddingGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = StudentMapper.class)
public interface GroupMapper {
    @Named("toGroupPreview")
    GroupPreview toGroupPreview(StuddingGroup group);

    @Mapping(target = "students", qualifiedByName = "toStudentPreview")
    GroupInfo toGroupInfo(StuddingGroup studdingGroup);
}
