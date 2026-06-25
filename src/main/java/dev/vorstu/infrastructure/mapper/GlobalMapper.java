package dev.vorstu.infrastructure.mapper;


import dev.vorstu.domain.student.Student;
import dev.vorstu.infrastructure.dto.response.StudentInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GlobalMapper {
    StudentInfo toStudentInfo(Student student);
}
