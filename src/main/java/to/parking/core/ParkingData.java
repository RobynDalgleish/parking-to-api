package to.parking.core;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ParkingData {

    private final InputStream dataSource;

    public ParkingData(InputStream dataSource) {
        this.dataSource = dataSource;
    }

    public Stream<ParkingSpot> read() throws IOException, CsvValidationException {
        var reader = new CSVReader(new InputStreamReader(dataSource));
        var header = reader.readNext();
        // header to map with key being the value in the string array, and the value of the map is the index of that item in the array
        Map<String, Integer> headerMap = new HashMap<>();
        for(int i = 0; i < header.length; i++){
            headerMap.put(header[i], i);
        }
        return StreamSupport.stream(reader.spliterator(), false)
            .map(csvLine -> new ParkingSpot(
                Integer.parseInt(csvLine[headerMap.get("ID")]),
                csvLine[headerMap.get("street")],
                csvLine[headerMap.get("park_side")],
                csvLine[headerMap.get("area_between")],
                csvLine[headerMap.get("valid_time")],
                // TODO:: fix spelling mistake in data
                csvLine[headerMap.get("permited_time")],
                // TODO:: fix spelling mistake in data
                Duration.ofMinutes(Integer.parseInt(csvLine[headerMap.get("permited_time_mins")])),
                csvLine[headerMap.get("start_zone")],
                csvLine[headerMap.get("end_zone")],
                csvLine[headerMap.get("clean")],
                Integer.parseInt(csvLine[headerMap.get("anytime_weekend")]) == 1,
                Integer.parseInt(csvLine[headerMap.get("exception")]) == 1
                ))
            .onClose(()-> {
            try {
                reader.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}