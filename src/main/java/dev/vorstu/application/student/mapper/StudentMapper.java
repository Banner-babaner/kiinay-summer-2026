package dev.vorstu.application.student.mapper;


import dev.vorstu.application.student.dto.output.StudentInfo;
import dev.vorstu.domain.student.Student;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentMapper {
    StudentInfo toStudentInfo(Student student);
}
