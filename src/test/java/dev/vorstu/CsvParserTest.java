package dev.vorstu;

import dev.vorstu.dto.input.CreateUserCsv;
import dev.vorstu.entity.UserRole;
import dev.vorstu.exception.parser.EmptyFileException;
import dev.vorstu.exception.parser.IllegalFileExtensionException;
import dev.vorstu.parser.CsvParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class CsvParserTest {
    private final CsvParser csvParser = new CsvParser();

    @Test
    void parseCsvShouldReturnListOfCreateUserCsv() throws IOException {
        String csvContent = "FIO,ROLE,EMAIL,GROUP_NAME,PHONE_NUMBER,LOGIN,PASSWORD\n" +
                "Ivanov Ivan,STUDENT,ivanov@mail.ru,GroupA,+71234567890,ivanov,pass\n" +
                "Petrov Petr,TEACHER,petrov@mail.ru,GroupB,+79876543210,petrov,pass";

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );



        List<CreateUserCsv> result = csvParser.parseCsv(file, CreateUserCsv.class);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFio()).isEqualTo("Ivanov Ivan");
        assertThat(result.get(0).getLogin()).isEqualTo("ivanov");
        assertThat(result.get(0).getRole()).isEqualTo(UserRole.STUDENT);
        assertThat(result.get(0).getGroupName()).isEqualTo("GroupA");
        assertThat(result.get(0).getPhoneNumber()).isEqualTo("+71234567890");
        assertThat(result.get(0).getEmail()).isEqualTo("ivanov@mail.ru");
        assertThat(result.get(0).getPassword()).isEqualTo("pass");

        assertThat(result.get(1).getFio()).isEqualTo("Petrov Petr");
        assertThat(result.get(1).getLogin()).isEqualTo("petrov");
        assertThat(result.get(1).getRole()).isEqualTo(UserRole.TEACHER);
        assertThat(result.get(1).getGroupName()).isEqualTo("GroupB");
        assertThat(result.get(1).getPhoneNumber()).isEqualTo("+79876543210");
        assertThat(result.get(1).getEmail()).isEqualTo("petrov@mail.ru");
        assertThat(result.get(1).getPassword()).isEqualTo("pass");
    }

    @Test
    void parseCsvShouldThrowExceptionWhenFileIsNull() {

        assertThatThrownBy(() -> csvParser.parseCsv(null, CreateUserCsv.class))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void parseCsvShouldThrowExceptionWhenFileIsEmpty() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                new byte[0]
        );

        assertThatThrownBy(() -> csvParser.parseCsv(file, CreateUserCsv.class))
                .isInstanceOf(EmptyFileException.class)
                .hasMessageContaining("File is empty");
    }

    @Test
    void parseCsvShouldThrowExceptionWhenInvalidExtension() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "data".getBytes()
        );

        assertThatThrownBy(() -> csvParser.parseCsv(file, CreateUserCsv.class))
                .isInstanceOf(IllegalFileExtensionException.class)
                .hasMessageContaining("txt");
    }

    @Test
    void parseCsvShouldThrowExceptionWhenNoExtension() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test",
                "text/csv",
                "data".getBytes()
        );

        assertThatThrownBy(() -> csvParser.parseCsv(file, CreateUserCsv.class))
                .isInstanceOf(IllegalFileExtensionException.class);
    }

    @Test
    void parseCsvShouldThrowExceptionWhenFilenameIsNull() {
        MultipartFile file = new MockMultipartFile(
                "file",
                null,
                "text/csv",
                "data".getBytes()
        );

        assertThatThrownBy(() -> csvParser.parseCsv(file, CreateUserCsv.class))
                .isInstanceOf(IllegalFileExtensionException.class);
    }

    @Test
    void parseCsvShouldHandleEmptyCsv() throws IOException {
        String csvContent = "FIO,ROLE,EMAIL,GROUP_NAME,PHONE_NUMBER,LOGIN,PASSWORD";

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        List<CreateUserCsv> result = csvParser.parseCsv(file, CreateUserCsv.class);

        assertThat(result).isEmpty();
    }

    @Test
    void parseCsvShouldThrowExceptionWhenMalformedCsv() {
        String csvContent = "FIO,ROLE,EMAIL\nIvanov Ivan,STUDENT";

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        assertThatThrownBy(() -> csvParser.parseCsv(file, CreateUserCsv.class))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void parseCsvShouldHandleRussianCharacters() throws IOException {
        String csvContent = "FIO,ROLE,EMAIL,GROUP_NAME,PHONE_NUMBER,LOGIN,PASSWORD\n" +
                "Иванов Иван,STUDENT,ivanov@mail.ru,ГруппаА,+71234567890,ivanov,pass";

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        List<CreateUserCsv> result = csvParser.parseCsv(file, CreateUserCsv.class);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFio()).isEqualTo("Иванов Иван");
        assertThat(result.get(0).getGroupName()).isEqualTo("ГруппаА");
    }

    @Test
    void parseCsvShouldHandleEmptyFields() throws IOException {
        String csvContent = "FIO,ROLE,EMAIL,GROUP_NAME,PHONE_NUMBER,LOGIN,PASSWORD\n" +
                "Ivanov Ivan,STUDENT,ivanov@mail.ru,,,ivanov,pass";

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        List<CreateUserCsv> result = csvParser.parseCsv(file, CreateUserCsv.class);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFio()).isEqualTo("Ivanov Ivan");
        assertThat(result.get(0).getGroupName()).isEqualTo("");
        assertThat(result.get(0).getPhoneNumber()).isEqualTo("");
    }

    @Test
    void parseCsvShouldHandleDifferentColumnOrder() throws IOException {
        String csvContent = "LOGIN,PASSWORD,FIO,ROLE,EMAIL,GROUP_NAME,PHONE_NUMBER\n" +
                "ivanov,pass,Ivanov Ivan,STUDENT,ivanov@mail.ru,GroupA,+71234567890";

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        List<CreateUserCsv> result = csvParser.parseCsv(file, CreateUserCsv.class);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFio()).isEqualTo("Ivanov Ivan");
        assertThat(result.get(0).getLogin()).isEqualTo("ivanov");
        assertThat(result.get(0).getPassword()).isEqualTo("pass");
        assertThat(result.get(0).getRole()).isEqualTo(UserRole.STUDENT);
    }
}