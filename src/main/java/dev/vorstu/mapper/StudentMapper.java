package dev.vorstu.mapper;


import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.entity.student.Student;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentMapper {
    StudentInfo toStudentInfo(Student student);
}
