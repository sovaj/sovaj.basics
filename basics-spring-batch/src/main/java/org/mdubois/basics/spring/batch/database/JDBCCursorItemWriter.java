package org.mdubois.basics.spring.batch.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

public class JDBCCursorItemWriter<T> extends JdbcBatchItemWriter<T> {

    private String fileName;

    public String sqlFileToString() throws FileNotFoundException, IOException {

        InputStream inputStream = getClass().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileNotFoundException(fileName);
        }
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer);
        String codeSql = writer.toString();

        return codeSql;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) throws IOException {
        this.fileName = fileName;
        String codeSql = sqlFileToString();
        super.setSql(codeSql);
    }
}
