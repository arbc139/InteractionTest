package totoro.project.interaction.interactiontestproject.csv;

import android.content.Context;
import android.support.annotation.NonNull;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.IOException;

public class CsvManager {

  private CSVWriter writer;

  public void createCsvWriter(Context context, String[] directories, String fileName) {
    File csvFile = createFileWithDirectory(context.getFilesDir(), directories, fileName);
    System.out.println("Generated CSV file: " + csvFile);
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
}
