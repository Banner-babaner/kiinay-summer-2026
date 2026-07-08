package dev.vorstu.parser;

import com.opencsv.bean.CsvToBeanBuilder;
import dev.vorstu.exception.parser.EmptyFileException;
import dev.vorstu.exception.parser.IllegalFileExtensionException;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Component
public class CsvParser {
    public <T> List<T> parseCsv(@NonNull MultipartFile file, @NonNull Class<T> type) throws IOException {
        validateFile(file);

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            return new CsvToBeanBuilder<T>(reader)
                    .withType(type)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withThrowExceptions(true)
                    .build()
                    .parse();
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException("File is empty");
        }

        String extension = getFileExtension(file);
        if(!Objects.equals(extension, "csv"))
            throw new IllegalFileExtensionException(extension);
    }

    private String getFileExtension(MultipartFile file){
        String name = file.getOriginalFilename();
        if(name == null) return null;
        return name.substring(name.lastIndexOf(".")+1).toLowerCase();
    }
}
