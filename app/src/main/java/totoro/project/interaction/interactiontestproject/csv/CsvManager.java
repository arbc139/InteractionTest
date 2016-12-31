package totoro.project.interaction.interactiontestproject.csv;

import android.content.Context;
import android.support.annotation.NonNull;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CsvManager {

  private CSVWriter writer;

  public void createCsvWriter(Context context, String[] directories, String fileName, String[] columns) {
    File csvFile = createFileWithDirectory(context.getFilesDir(), directories, fileName);
    writer = createCsvWriter(csvFile);
    writer.writeNext(columns);
  }

  public void write(String[] row) {
    writer.writeNext(row);
  }

  public void closeCsvWriter() {
    try {
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void clear() {
    closeCsvWriter();
    writer = null;
  }

  public void safeClear() {
    if (writer != null) {
      closeCsvWriter();
      writer = null;
    }
  }

  private static File createDirectory(@NonNull File basePath, @NonNull String[] directories) {
    String filePath = File.separator;
    for (String directoryName : directories) {
      filePath = filePath + directoryName + File.separator;
    }
    return new File(basePath, filePath);
  }

  private static File createFileWithDirectory(@NonNull File basePath, @NonNull String[] directories,
                                              @NonNull String fileName) {
    File directory = createDirectory(basePath, directories);
    // Directory가 필요하면 생성함.
    directory.mkdirs();
    File csvFile = new File(directory, fileName);
    try {
      csvFile.createNewFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return csvFile;
  }

  private static CSVWriter createCsvWriter(File csvFile) {
    FileOutputStream outputStream;
    try {
      outputStream = new FileOutputStream(csvFile);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    return new CSVWriter(new OutputStreamWriter(outputStream));
  }
}
